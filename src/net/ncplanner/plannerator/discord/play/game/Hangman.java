package net.ncplanner.plannerator.discord.play.game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.discord.play.Game;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.ITemplateAccess;
import net.ncplanner.plannerator.multiblock.editor.ppe.ClearInvalid;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.CircularStream;
import net.ncplanner.plannerator.planner.Searchable;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
public class Hangman extends Game{
    public Multiblock basis;
    public Multiblock current;
    public ArrayList<AbstractBlock> guesses = new ArrayList<>();
    public int badGuesses;
    private final Configuration config;
    private final int maxGuesses;
    private boolean usesActive = false;
    private final boolean blind;
    public HashSet<AbstractBlock> silent = new HashSet<>();
    public Hangman(boolean blind, ArrayList<Multiblock> allowedMultiblocks){
        super("Hangman");
        this.blind = blind;
        ArrayList<Multiblock> multis = new ArrayList<>();
        synchronized(Bot.storedMultiblocks){
            for(LegacyNCPFFile ncpf : Bot.storedMultiblocks){
                multis.addAll(ncpf.multiblocks);
            }
        }
        for(Iterator<Multiblock> it = multis.iterator(); it.hasNext();){
            Multiblock next = it.next();
            boolean isAllowed = false;
            for(Multiblock m : allowedMultiblocks){
                if(m.getDefinitionName().equals(next.getDefinitionName()))isAllowed = true;
                m.recalculate();
            }
            if(next instanceof UnderhaulSFR&&((UnderhaulSFR)next).netHeat>0)isAllowed = false;
            if(next instanceof OverhaulSFR&&((OverhaulSFR)next).netHeat>0)isAllowed = false;
            if(next instanceof OverhaulMSR&&((OverhaulMSR)next).netHeat>0)isAllowed = false;
            if(!isAllowed)it.remove();
        }
        if(multis.isEmpty()){
            config = null;
            maxGuesses = 0;
            return;
        }
        basis = multis.get(new Random().nextInt(multis.size())).copy();
        this.config = basis.configuration;
        basis.recalculate();
        new ClearInvalid().apply(basis, null);
        if(basis instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)basis).buildDefaultCasing();
        }
        basis.recalculate();
        basis.metadata.clear();
        current = basis.blankCopy();
        ArrayList<AbstractBlock> blocks = basis.getBlocks(true);
        ArrayList<AbstractBlock> unique = new ArrayList<>();
        for(AbstractBlock b : blocks){
            if(b.getName().toLowerCase(Locale.ROOT).contains("active")){
                usesActive = true;
            }
        }
        FOR:for(AbstractBlock b : blocks){
            for(AbstractBlock un : unique){
                if(un.isEqual(b))continue FOR;
            }
            unique.add(b);
        }
        ArrayList<AbstractBlock> available = new ArrayList<>(basis.getAvailableBlocks());
        if(!usesActive){
            for(Iterator<AbstractBlock> it = available.iterator(); it.hasNext();){
                AbstractBlock next = it.next();
                if(next.getName().toLowerCase(Locale.ROOT).contains("active")){
                    it.remove();
                }
            }
        }
        for(Iterator<AbstractBlock> it = available.iterator(); it.hasNext();){
            AbstractBlock b = it.next();
            if(b.getName().toLowerCase(Locale.ROOT).contains("port")||b.getName().contains("Casing")||b.getName().contains("Glass")||b.getName().contains("Controller")||b.getName().contains("Vent")||b.getName().contains("Conductor")){
                silent.add(b);
                it.remove();
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
        for(AbstractBlock b : ((Multiblock<AbstractBlock>)basis).getAvailableBlocks()){
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
        if(message.getAuthor().getIdLong()==210445638532333569l&&message.getContentRaw().equals("hangman_dump")){
            message.getChannel().sendMessage("Basis configuration: "+ots(basis.getConfiguration())+" "+basis.configuration.toString()+"\n"+
                    "Current configuration: "+ots(current.getConfiguration())+" "+current.configuration.toString()+"\n"+
                    "This configuration: "+ots(config)+" "+config.toString()).queue();
            String dump = "";
            ArrayList<AbstractBlock> blocks = new ArrayList<>();
            basis.getAvailableBlocks(blocks);
            for(AbstractBlock b : blocks)dump+="\n"+b.getName();
            message.getChannel().sendMessage("Block Library: "+dump).queue();
            return;
        }
        String content = message.getContentDisplay().trim().replace(":", "");
        ArrayList<AbstractBlock> blocks = new ArrayList<>();
        basis.getAvailableBlocks(blocks);
        ArrayList<AbstractBlock> searched = new ArrayList<>();
        for(AbstractBlock b : blocks){
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
    private AbstractBlock findBlock(IBlockTemplate template){
        if(template==null)return null;
        ArrayList<AbstractBlock> blocks = new ArrayList<>();
        basis.getAvailableBlocks(blocks);
        for(AbstractBlock b : blocks){
            if(b instanceof ITemplateAccess){
                if(((ITemplateAccess)b).getTemplate()==template)return b;
            }
        }
        return null;
    }
    private void guess(MessageChannel channel, AbstractBlock b){
        guess(channel, b, false);
    }
    private void silentGuess(MessageChannel channel, AbstractBlock b){
        guess(channel, b, true);
    }
    private void guess(MessageChannel channel, AbstractBlock b, boolean silent){
        if(b==null)return;//ignore null blocks
        if(!silent){
            if(b instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block){
                silentGuess(channel, ((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)b).template.port);
                silentGuess(channel, ((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)b).template.parent);
            }
            if(b instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block){
                silentGuess(channel, ((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)b).template.port);
                silentGuess(channel, ((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)b).template.parent);
            }
        }
        update();
        if(b.getName().toLowerCase(Locale.ROOT).contains("active")&&!usesActive){
            if(!silent)channel.sendMessage("This reactor contains no active coolers!").queue();
            return;
        }
        for(AbstractBlock bl : guesses){
            if(bl.isEqual(b)){
                if(!silent)channel.sendMessage("You already guessed "+b.getName()+"!").queue();
                return;
            }
        }
        guesses.add(b);
        ArrayList<AbstractBlock> blocks = basis.getBlocks();
        boolean valid = false;
        for(AbstractBlock block : blocks){
            if(block.isEqual(b)){
                current.setBlock(block.x, block.y, block.z, block);
                valid = true;
            }
        }
        if(!valid){
            if(!this.silent.contains(b))badGuesses++;
            if(badGuesses>=maxGuesses){
                ArrayList<AbstractBlock> missed = new ArrayList<>();
                FOR:for(AbstractBlock bl : blocks){
                    if(current.getBlock(bl.x, bl.y, bl.z)!=null)continue;//got that one right
                    for(AbstractBlock miss : missed){
                        if(miss.isEqual(bl))continue FOR;
                    }
                    missed.add(bl);
                }
                String missing = "Missed Blocks:";
                for(AbstractBlock miss : missed){
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
    private void exportPng(LegacyNCPFFile ncpf, MessageChannel channel){
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
    private LegacyNCPFFile generateNCPF(Multiblock multi){
        LegacyNCPFFile file = new LegacyNCPFFile();
        multi.showDetails = false;
        file.configuration = config;
        file.multiblocks.add(multi);
        return file;
    }
    @Override
    public boolean canAnyoneStop(){
        return true;
    }
    private String ots(Configuration config){
        return config.getClass().getName()+"#"+config.hashCode();
    }
}