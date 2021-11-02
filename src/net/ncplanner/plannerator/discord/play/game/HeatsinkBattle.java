package net.ncplanner.plannerator.discord.play.game;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.discord.play.Game;
import net.ncplanner.plannerator.discord.play.SmoreBot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.ITemplateAccess;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.editor.ppe.ClearInvalid;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.ncplanner.plannerator.planner.Searchable;
import net.ncplanner.plannerator.planner.file.FileWriter;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import simplelibrary.CircularStream;
public class HeatsinkBattle extends Game{
    public Multiblock basis;
    public Multiblock current;
    private final Configuration config;
    public boolean started = false;
    private ArrayList<Long> players = new ArrayList<>();
    private ArrayList<String> playernames = new ArrayList<>();
    private ArrayList<Integer> scores = new ArrayList<>();
    private HashMap<BlockPos, Integer> credit = new HashMap<>();
    private int turn;
    private ArrayList<Block> blocks = new ArrayList<>();
    private ArrayList<Integer> skips = new ArrayList<>();
    private int entryFee;
    private long smorePool = 0;
    private boolean splodoHasDonated = false;
    private int initialHeat;
    public HeatsinkBattle(ArrayList<Multiblock> allowedMultiblocks){
        super("Heatsink Battle");
        timeout = 600_000;//10 minutes
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
        current = basis.copy();
        if(current instanceof CuboidalMultiblock){
            ((CuboidalMultiblock)current).forEachInternalPosition((x, y, z) -> {
                Block b = current.getBlock(x, y, z);
                if(b!=null&&!b.isCore())current.setBlock(x, y, z, null);//remove all non core blocks
            });
        }else{
            current.forEachPosition((x, y, z) -> {
                Block b = current.getBlock(x, y, z);
                if(b!=null&&!b.isCore())current.setBlock(x, y, z, null);//remove all non core blocks
            });
        }
        for(Block b : ((Multiblock<Block>)current).getAvailableBlocks()){
            if(isHeatsink(b))blocks.add(b);
        }
    }
    public void addPlayer(User user){
        if(started)channel.sendMessage("Battle has already started!").queue();
        else{
            if(players.contains(user.getIdLong()))return;//just ignore
            if(entryFee>0){
                long smores = SmoreBot.getSmoreCount(user.getIdLong());
                if(smores<entryFee){
                    channel.sendMessage("You don't have enough s'mores to join this battle!").queue();
                    return;
                }
                SmoreBot.removeSmores(user, entryFee);
                SmoreBot.addSmores(user.getJDA().getSelfUser(), entryFee);//give them to s'plodo
                smorePool+=entryFee;
            }
            players.add(user.getIdLong());
            scores.add(0);
            playernames.add(user.getName());
            if(players.size()>1)channel.sendMessage(strip(user.getName())+" Joined the Battle!").queue();
        }
    }
    @Override
    protected boolean start(MessageChannel channel){
        if(basis==null){
            channel.sendMessage("No applicable multiblocks found!").queue();
            return false;
        }
        String message = "Battle lobby created! Players can join with `-battle`; When all players are in, say `start` to start the game!\nHow to play:\n"
                + "Each turn, choose a block to place in the reactor. Your goal is to cool it down as much as possible.\nAt the end of the game, the player who provided the most cooling wins!\n"
                + "To make a change, type `<x> <y> <z> <block name>`. (ex. `4 2 13 water`)\n"
                + "You can replace invalid blocks and Coolers/Heatsinks/Heaters";
        if(entryFee>0)message+="\n**Entry fee is <:smore:493612965195677706>"+entryFee+" each (Winner takes all!)** *S'mores will be lost if the battle is stopped*";
        message+="\nChosen core:";
        initialHeat = getHeat();
        channel.sendMessage(message).queue();
        exportPng(generateNCPF(current), channel);
        return true;
    }
    @Override
    public void stop(MessageChannel channel, StopReason reason){
        if(!started){
            if(smorePool>0){
                long smoresPerPlayer = smorePool/players.size();
                for(long u : players){
                    SmoreBot.addSmores(channel.getJDA().retrieveUserById(u).complete(), smoresPerPlayer);
                    SmoreBot.removeSmores(channel.getJDA().getSelfUser(), smoresPerPlayer);
                }
                channel.sendMessage("Players have been refunded <:smore:493612965195677706>"+smoresPerPlayer).queue();
            }
            return;
        }
        String s = "Battle has concluded!";
        if(reason==StopReason.TIMEOUT)s = "Battle timed out!";
        if(reason==StopReason.TIMEOUT)reason = StopReason.GAME_FINISHED;
        if(getHeat()<=0)s+="\nReactor was cooled!";
        s+="\nScores: ";
        HashMap<Long, Integer> scoresMap = new HashMap<>();
        HashMap<Long, Double> bonusMap = new HashMap<>();
        HashMap<Long, String> namesMap = new HashMap<>();
        for(int i = 0; i<players.size(); i++){
            int num = countRainbowCredit(credit, i);
            double bonus = num/(double)blocks.size();
            bonusMap.put(players.get(i), bonus);
            scoresMap.put(players.get(i), (int)(scores.get(i)*(1+bonus)));
            namesMap.put(players.get(i), playernames.get(i));
        }
        long winner = -1;
        long winnerScore = -1;
        for(int i = 0; i<players.size(); i++){
            long u = players.get(i);
            int score = scoresMap.get(u);
            s+="\n"+(i+1)+" - "+strip(namesMap.get(u))+" ("+scoresMap.get(u)+")";
            if(bonusMap.get(u)!=0)s+=" "+Math.round(bonusMap.get(u)*100)+"% Rainbow Bonus!";
            if(!skips.contains(i)){
                if(winner==-1||score>winnerScore){
                    winner = u;
                    winnerScore = score;
                }
            }
        }
        Collections.sort(players, (o1, o2) -> {
            return scoresMap.get(o2)-scoresMap.get(o1);
        });
        if(smorePool>0){
            if(reason==StopReason.GAME_FINISHED&&winner!=-1){
                User winnerUser = channel.getJDA().retrieveUserById(winner).complete();
                SmoreBot.removeSmores(channel.getJDA().getSelfUser(), smorePool);
                SmoreBot.addSmores(winnerUser, smorePool);
                channel.sendMessage(strip(winnerUser.getName())+" won <:smore:493612965195677706>"+smorePool+"!").queue();
            }else channel.sendMessage("S'more pool of <:smore:493612965195677706>"+smorePool+" has been sent to "+strip(channel.getJDA().getSelfUser().getName())).queue();
        }
        channel.sendMessage(s).queue();
    }
    @Override
    public void onMessage(Message message){
        if(!started){
            if(message.getAuthor().getIdLong()==players.get(0)&&message.getContentRaw().trim().equalsIgnoreCase("start")){
                started = true;
                update();
                turn = new Random().nextInt(players.size());
                channel.sendMessage("Battle has begun!").queue();
                nextTurn(message.getChannel());
            }
            return;
        }
        if(message.getAuthor().getIdLong()==players.get(turn)){
            String content = message.getContentStripped();
            if(content.equalsIgnoreCase("forfeit")||content.equalsIgnoreCase("surrender")||content.equalsIgnoreCase("give up")||content.equalsIgnoreCase("quit")||content.equalsIgnoreCase("leave")){
                message.getChannel().sendMessage(strip(message.getAuthor().getName())+" couldn't handle the heat!").queue();
                skips.add(turn);
                if(skips.size()==players.size()-1){
                    stop(channel, StopReason.GAME_FINISHED);//one player left, end the game
                    running = false;
                    return;
                }
                nextTurn(channel);
                return;
            }
            String[] strs = message.getContentStripped().split(" ", 4);
            if(strs.length<4)return;//ignore
            try{
                int x = Integer.parseInt(strs[0]);
                int y = Integer.parseInt(strs[1]);
                int z = Integer.parseInt(strs[2]);
                String blockName = strs[3];
                Block block = null;
                ArrayList<Block> searched = new ArrayList<>();
                for(Block b : blocks){
                    if(Searchable.isValidForSimpleSearch(b, blockName))searched.add(b);
                    if(b.roughMatch(blockName.trim().replace(":", ""))){
                        block = b;
                        break;
                    }
                }
                if(block==null&&searched.size()==1){
                    message.getChannel().sendMessage("Found one searchable result; using "+searched.get(0).getName()).queue();
                    block = searched.get(0);
                }
                if(!current.contains(x, y, z)){
                    message.getChannel().sendMessage(strip(message.getAuthor().getName())+" tried to steal a block by placing it outside the reactor! (-100 points)").queue();
                    scores.set(turn, scores.get(turn)-100);
                }else{
                    if(block==null){
                        if(blockName.toLowerCase().contains("active")){
                            message.getChannel().sendMessage(strip(message.getAuthor().getName())+" tried to use active cooling! (-200 points)").queue();
                            scores.set(turn, scores.get(turn)-200);
                        }else message.getChannel().sendMessage("Invalid block! You may only use Coolers/Heatsinks/Heaters!").queue();
                    }else{
                        Block currentBlock = current.getBlock(x, y, z);
                        if(currentBlock==null||!currentBlock.isValid()||isHeatsink(currentBlock)){
                            int oldHeat = getHeat();
                            current.setBlock(x, y, z, block.newInstance(x, y, z));
                            current.getBlocks(true);
                            current.recalculate();
                            int diff = oldHeat-getHeat();
                            scores.set(turn, scores.get(turn)+diff);
                            credit.put(new BlockPos(x, y, z), turn);
                            String mess = "No net change!";
                            if(diff>0)mess = "Cooled down! (+"+diff+" points)";
                            if(diff<0)mess = "Temperature Increased! ("+diff+" points)";
                            message.getChannel().sendMessage(mess).queue();
                            exportPng(generateNCPF(current), channel);
                        }else{
                            message.getChannel().sendMessage("There's already a "+currentBlock.getName()+" there!").queue();
                        }
                    }
                }
                nextTurn(channel);
            }catch(NumberFormatException ex){}
        }
    }
    private void exportPng(NCPFFile ncpf, MessageChannel channel){
        for(Multiblock m : ncpf.multiblocks){
            m.recalculate();
        }
        FormatWriter format = FileWriter.PNG;
        CircularStream stream = new CircularStream(1024*1024);//1MB
        CompletableFuture<Message> submit = channel.sendFile(stream.getInput(), "battle."+format.getFileFormat().extensions[0]).submit();
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
    private void nextTurn(MessageChannel channel){
//        update();
        if(skips.size()==players.size()||getHeat()<=0){
            stop(channel, StopReason.GAME_FINISHED);//used for printing end-of-game stuff
            running = false;
            return;
        }
        do{
            turn++;
            if(turn>=players.size())turn = 0;
        }while(skips.contains(turn));
        int threshold = 5000;
        boolean met = false;
        for(int i : scores)if(i>=threshold)met = true;
        if(met&&!splodoHasDonated){
            splodoHasDonated = true;
            long splodoSmores = SmoreBot.getSmoreCount(channel.getJDA().getSelfUser().getIdLong())-smorePool;
            long toDonate = Math.min(splodoSmores, smorePool*(players.size()-1)/2);
            System.out.println("Donating "+toDonate+" smores");
            if(toDonate>0){
                smorePool+=toDonate;
                channel.sendMessage(strip(channel.getJDA().getSelfUser().getName())+" has donated <:smore:493612965195677706>"+toDonate+" to the pool! (total: <:smore:493612965195677706>"+smorePool+")").queue();
            }
        }
        channel.sendMessage(strip(playernames.get(turn))+", Your turn!").queue();
    }
    private String strip(String name){
        return "`"+name.replace("`", "\\`")+"`";
    }
    private boolean isHeatsink(Block b){
        if(b instanceof net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block){
            if(((net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block)b).template.active!=null)return false;//no active allowed here
            if(((net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block)b).isCooler())return true;
        }
        if(b instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block){
            if(((net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)b).isHeatsink())return true;
        }
        if(b instanceof net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block){
            if(((net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)b).isHeater())return true;
        }
        return false;
    }
    private int getHeat(){
        if(current instanceof UnderhaulSFR){
            return ((UnderhaulSFR)current).netHeat;
        }
        if(current instanceof OverhaulSFR){
            return ((OverhaulSFR)current).netHeat;
        }
        if(current instanceof OverhaulMSR){
            return ((OverhaulMSR)current).netHeat;
        }
        return -1;
    }
    private int countRainbowCredit(HashMap<BlockPos, Integer> credit, int i){
        HashSet<IBlockTemplate> templates = new HashSet<>();
        for(BlockPos pos : credit.keySet()){
            if(credit.get(pos)!=i)continue;//that's for a different player
            if(!current.contains(pos.x, pos.y, pos.z))continue;//not in the reactor
            Block b = current.getBlock(pos.x, pos.y, pos.z);
            if(b==null)continue;//air
            if(!b.isValid())continue;//not valid
            templates.add(((ITemplateAccess)b).getTemplate());
        }
        return templates.size();
    }
    public HeatsinkBattle setSmores(int smores){
        entryFee = smores;
        return this;
    }
    @Override
    public boolean canAnyoneStop(){
        return smorePool==0||!running;
    }
}