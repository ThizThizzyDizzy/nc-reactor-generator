package discord.play.game;
import discord.Bot;
import discord.play.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import multiblock.Block;
import multiblock.CuboidalMultiblock;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.ITemplateAccess;
import multiblock.ppe.ClearInvalid;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import planner.Searchable;
import planner.file.FileWriter;
import planner.file.FormatWriter;
import planner.file.NCPFFile;
import simplelibrary.CircularStream;
public class Hangman extends Game{
    public Multiblock basis;
    public Multiblock current;
    public ArrayList<Block> guesses = new ArrayList<>();
    public int badGuesses;
    private final Configuration config;
    private final int maxGuesses;
    private boolean usesActive = false;
    private final boolean blind;
    public Hangman(boolean blind, ArrayList<Multiblock> allowedMultiblocks){
        super("Hangman");
        this.blind = blind;
        ArrayList<Multiblock> multis = new ArrayList<>();
        HashMap<Multiblock, Configuration> configs = new HashMap<>();
        synchronized(Bot.storedMultiblocks){
            for(NCPFFile ncpf : Bot.storedMultiblocks){
                for(Multiblock multi : ncpf.multiblocks){
                    configs.put(multi, ncpf.configuration);
                }
                multis.addAll(ncpf.multiblocks);
            }
        }
        for(Iterator<Multiblock> it = multis.iterator(); it.hasNext();){
            Multiblock next = it.next();
            boolean isAllowed = false;
            for(Multiblock m : allowedMultiblocks){
                if(m.getDefinitionName().equals(next.getDefinitionName()))isAllowed = true;
            }
            if(!isAllowed)it.remove();
        }
        if(multis.isEmpty()){
            config = null;
            maxGuesses = 0;
            return;
        }
        basis = multis.get(new Random().nextInt(multis.size())).copy();
        this.config = configs.get(basis);
        basis.configuration = config;//why is this here...?
        basis.recalculate();
        new ClearInvalid().apply(basis, null);
        if(basis instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)basis).buildDefaultCasing();
        }
        basis.recalculate();
        basis.metadata.clear();
        current = basis.blankCopy();
        ArrayList<Block> blocks = basis.getBlocks(true);
        ArrayList<Block> unique = new ArrayList<>();
        for(Block b : blocks){
            if(b.getName().toLowerCase(Locale.ROOT).contains("active")){
                usesActive = true;
            }
        }
        FOR:for(Block b : blocks){
            for(Block un : unique){
                if(un.isEqual(b))continue FOR;
            }
            unique.add(b);
        }
        ArrayList<Block> available = new ArrayList<>(basis.getAvailableBlocks());
        if(!usesActive){
            for(Iterator<Block> it = available.iterator(); it.hasNext();){
                Block next = it.next();
                if(next.getName().toLowerCase(Locale.ROOT).contains("active")){
                    it.remove();
                }
            }
        }
        int total = available.size();
        int possibleBadGuesses = total-unique.size();
        maxGuesses = possibleBadGuesses/2;
    }
    @Override
    public boolean start(MessageChannel channel){
        if(basis==null){
            channel.sendMessage("No applicable multiblocks found!").queue();
            return false;
        }
        for(Block b : ((Multiblock<Block>)basis).getAvailableBlocks()){
            if(b.getName().contains("Casing")||b.getName().contains("Glass")||b.getName().contains("Controller")||b.getName().contains("Vent")||b.getName().contains("Conductor"))silentGuess(channel, b);
        }
        channel.sendMessage("Hangman has started!\nCurrent Multiblock: "+basis.getDimensionsStr()+" "+basis.getDefinitionName()+"\nYou have "+(maxGuesses-1)+" Incorrect guesses left."+(usesActive?"\nThis reactor has active coolers":"")).queue();
        if(!blind)exportPng(generateNCPF(current), channel);
        return true;
    }
    @Override
    public void stop(MessageChannel channel, StopReason reason){
        if(reason==StopReason.TIMEOUT)channel.sendMessage("Hangman timed out!").queue();
    }
    @Override
    public void onMessage(Message message){
        String content = message.getContentDisplay().trim().replace(":", "");
        ArrayList<Block> blocks = new ArrayList<>();
        basis.getAvailableBlocks(blocks);
        ArrayList<Block> searched = new ArrayList<>();
        for(Block b : blocks){
            if(Searchable.isValidForSimpleSearch(b, content))searched.add(b);
            if(b.roughMatch(content)){
                guess(message.getChannel(), b);
                return;
            }
        }
        if(searched.size()==1){
            message.getChannel().sendMessage("Found one searchable result; guessing "+searched.get(0).getName()).queue();
            guess(message.getChannel(), searched.get(0));
        }
    }
    private void guess(MessageChannel channel, IBlockTemplate b){
        guess(channel, findBlock(b), false);
    }
    private void silentGuess(MessageChannel channel, IBlockTemplate b){
        guess(channel, findBlock(b), true);
    }
    private Block findBlock(IBlockTemplate template){
        if(template==null)return null;
        ArrayList<Block> blocks = new ArrayList<>();
        basis.getAvailableBlocks(blocks);
        for(Block b : blocks){
            if(b instanceof ITemplateAccess){
                if(((ITemplateAccess)b).getTemplate()==template)return b;
            }
        }
        return null;
    }
    private void guess(MessageChannel channel, Block b){
        guess(channel, b, false);
    }
    private void silentGuess(MessageChannel channel, Block b){
        guess(channel, b, true);
    }
    private void guess(MessageChannel channel, Block b, boolean silent){
        if(b==null)return;//ignore null blocks
        if(!silent){
            if(b instanceof multiblock.overhaul.fissionsfr.Block){
                silentGuess(channel, ((multiblock.overhaul.fissionsfr.Block)b).template.port);
                silentGuess(channel, ((multiblock.overhaul.fissionsfr.Block)b).template.parent);
            }
            if(b instanceof multiblock.overhaul.fissionmsr.Block){
                silentGuess(channel, ((multiblock.overhaul.fissionmsr.Block)b).template.port);
                silentGuess(channel, ((multiblock.overhaul.fissionmsr.Block)b).template.parent);
            }
        }
        update();
        if(b.getName().toLowerCase(Locale.ROOT).contains("active")&&!usesActive){
            if(!silent)channel.sendMessage("This reactor contains no active coolers!").queue();
            return;
        }
        for(Block bl : guesses){
            if(bl.isEqual(b)){
                if(!silent)channel.sendMessage("You already guessed "+b.getName()+"!").queue();
                return;
            }
        }
        guesses.add(b);
        ArrayList<Block> blocks = basis.getBlocks();
        boolean valid = false;
        for(Block block : blocks){
            if(block.isEqual(b)){
                current.setBlock(block.x, block.y, block.z, block);
                valid = true;
            }
        }
        if(!valid){
            badGuesses++;
            if(badGuesses>=maxGuesses){
                ArrayList<Block> missed = new ArrayList<>();
                FOR:for(Block bl : blocks){
                    if(current.getBlock(bl.x, bl.y, bl.z)!=null)continue;//got that one right
                    for(Block miss : missed){
                        if(miss.isEqual(bl))continue FOR;
                    }
                    missed.add(bl);
                }
                String missing = "Missed Blocks:";
                for(Block miss : missed){
                    missing+="\n`"+miss.getName()+"`";
                }
                channel.sendMessage("Game Over!\n"+missing).queue();
                exportPng(generateNCPF(basis), channel);
                running = false;
                return;
            }
            if(!silent)channel.sendMessage("Nope; Try again!\nRemaining guesses: "+(maxGuesses-badGuesses)).queue();
        }else{
            if(!silent)channel.sendMessage("Correct!").queue();
            if(current.getBlocks(true).size()==basis.getBlocks().size()){
                channel.sendMessage("You win!\nIncorrect Guesses: "+badGuesses+"/"+(maxGuesses-1)).queue();
                exportPng(generateNCPF(basis), channel);
                running = false;
                return;
            }
            if(!blind&&!silent)exportPng(generateNCPF(current), channel);
        }
    }
    private void exportPng(NCPFFile ncpf, MessageChannel channel){
        for(Multiblock m : ncpf.multiblocks){
            m.recalculate();
        }
        FormatWriter format = FileWriter.PNG;
        CircularStream stream = new CircularStream(1024*1024);//1MB
        CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), "hangman."+format.getFileFormat().extensions[0]).submit();
        try{
            format.write(ncpf, stream);
        }catch(Exception ex){
            Bot.printErrorMessage(channel, "Failed to write file", ex);
            submit.cancel(true);
            stream.close();
        }
    }
    private NCPFFile generateNCPF(Multiblock multi){
        NCPFFile file = new NCPFFile();
        multi.showDetails = false;
        file.configuration = config;
        file.multiblocks.add(multi);
        return file;
    }
    @Override
    public boolean canAnyoneStop(){
        return true;
    }
}