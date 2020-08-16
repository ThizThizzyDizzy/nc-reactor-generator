package discord.play.game;
import discord.Bot;
import discord.play.Game;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.ppe.ClearInvalid;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
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
    public Hangman(boolean blind){
        super("Hangman");
        ArrayList<NCPFFile> ncpfs = new ArrayList<>(Bot.storedMultiblocks.keySet());
        NCPFFile ncpf = ncpfs.get(new Random().nextInt(ncpfs.size()));
        ArrayList<Multiblock> multis = new ArrayList<>(ncpf.multiblocks);
        basis = multis.get(new Random().nextInt(multis.size())).copy();
        basis.recalculate();
        new ClearInvalid().apply(basis, null);
        basis.recalculate();
        this.config = ncpf.configuration;
        basis.metadata.clear();
        current = basis.blankCopy();
        ArrayList<Block> blocks = basis.getBlocks();
        ArrayList<Block> unique = new ArrayList<>();
        for(Block b : blocks){
            if(b.getName().toLowerCase().contains("active")){
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
                if(next.getName().toLowerCase().contains("active")){
                    it.remove();
                }
            }
        }
        int total = available.size();
        int possibleBadGuesses = total-unique.size();
        maxGuesses = possibleBadGuesses/2;
        this.blind = blind;
    }
    @Override
    public void start(MessageChannel channel){
        channel.sendMessage("Hangman has started!\nCurrent Multiblock: "+basis.getX()+"x"+basis.getY()+"x"+basis.getZ()+" "+basis.getDefinitionName()+"\nYou have "+(maxGuesses-1)+" Incorrect guesses left."+(usesActive?"\nThis reactor has active coolers":"")).queue();
        if(!blind)exportPng(generateNCPF(current), channel);
    }
    @Override
    protected void stop(MessageChannel channel){
        channel.sendMessage("Hangman timed out!").queue();
    }
    @Override
    public void onMessage(Message message){
        String content = message.getContentDisplay().trim().replace(":", "");
        ArrayList<Block> blocks = new ArrayList<>();
        basis.getAvailableBlocks(blocks);
        for(Block b : blocks){
            if(b.roughMatch(content)){
                guess(message.getChannel(), b);
                return;
            }
        }
    }
    private void guess(MessageChannel channel, Block b){
        update();
        if(b.getName().toLowerCase().contains("active")&&!usesActive){
            channel.sendMessage("This reactor contains no active coolers!").queue();
            return;
        }
        for(Block bl : guesses){
            if(bl.isEqual(b)){
                channel.sendMessage("You already guessed "+b.getName()+"!").queue();
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
            channel.sendMessage("Nope; Try again!\nRemaining guesses: "+(maxGuesses-badGuesses)).queue();
        }else{
            channel.sendMessage("Correct!").queue();
            if(current.getBlocks(true).size()==basis.getBlocks().size()){
                channel.sendMessage("You win!\nIncorrect Guesses: "+badGuesses+"/"+(maxGuesses-1)).queue();
                exportPng(generateNCPF(basis), channel);
                running = false;
                return;
            }
            if(!blind)exportPng(generateNCPF(current), channel);
        }
    }
    private void exportPng(NCPFFile ncpf, MessageChannel channel){
        for(Multiblock m : ncpf.multiblocks){
            m.recalculate();
        }
        FormatWriter format = FileWriter.formats.get(1);
        CircularStream stream = new CircularStream(1024*1024);//1MB
        CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), "hangman."+format.getExtensions()[0]).submit();
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
}