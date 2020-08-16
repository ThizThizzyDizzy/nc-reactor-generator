package generator;
import generator.challenger.Challenger;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.Range;
import planner.menu.component.MenuComponentMinimaList;
public abstract class MultiblockGenerator{
    public int iterations = 0;
    public final Object iterationSynchronizer = new Object();
    protected Random rand = new Random();
    public static final ArrayList<MultiblockGenerator> generators = new ArrayList<>();
    static{
        generators.add(new StandardGenerator(null));
        Challenger.init();
    }
    protected ArrayList<Priority> priorities;
    private Object threadronyzer = new Object();
    public final Multiblock multiblock;
    private ArrayList<UUID> threads = new ArrayList<>();
    public MultiblockGenerator(Multiblock multiblock){
        this.multiblock = multiblock;
        if(multiblock!=null)priorities = multiblock.getGenerationPriorities();
    }
    public abstract MultiblockGenerator newInstance(Multiblock multi);
    public abstract ArrayList<Multiblock>[] getMultiblockLists();
    public abstract Multiblock[] getValidMultiblocks();
    public abstract String getName();
    public static ArrayList<MultiblockGenerator> getGenerators(Multiblock m){
        ArrayList<MultiblockGenerator> valid = new ArrayList<>();
        for(MultiblockGenerator gen : generators){
            for(Multiblock mb : gen.getValidMultiblocks()){
                if(mb.getDefinitionName().equals(m.getDefinitionName()))valid.add(gen);
            }
        }
        return valid;
    }
    public abstract void addSettings(MenuComponentMinimaList generatorSettings, Multiblock multi);
    public abstract void refreshSettingsFromGUI(ArrayList<Range<Block>> allowedBlocks);
    public abstract void refreshSettings(Settings settings);
    public int getActiveThreads(){
        synchronized(threadronyzer){
            return threads.size();
        }
    }
    /**
     * MULTITHREADED
     */
    public abstract void tick();
    public void startThread(){
        synchronized(threadronyzer){
            UUID uid = UUID.randomUUID();
            threads.add(uid);
            Thread thread = new Thread(() -> {
                try{
                    while(threads.contains(uid)){
                        tick();
                    }
                }catch(Exception ex){
                    threads.remove(uid);
                    throw new RuntimeException(ex);
                }
            });
            thread.setName("Generation Thread "+uid.toString());
            thread.setDaemon(true);
            thread.start();
        }
    }
    public void stopThread(){
        synchronized(threadronyzer){
            threads.remove(0);
        }
    }
    public void stopAllThreads(){
        synchronized(threadronyzer){
            threads.clear();
        }
        System.out.println(iterations+" iterations");
    }
    public abstract void importMultiblock(Multiblock<Block> multiblock);
    public Multiblock getMainMultiblock(){
        ArrayList<Multiblock>[] lists = getMultiblockLists();
        if(lists.length==0)return null;
        ArrayList<Multiblock> list = lists[0];
        if(list.isEmpty())return null;
        return list.get(0);
    }
    public String getMainMultiblockTooltip(){
        Multiblock main = getMainMultiblock();
        if(main==null)return "";
        main.metadata.put("Author", "S'plodo-bot");
        return main.getTooltip();
    }
    public String getMainMultiblockBotTooltip(){
        Multiblock main = getMainMultiblock();
        if(main==null)return "";
        main.metadata.put("Author", "S'plodo-bot");
        return main.getBotTooltip();
    }
    public boolean isRunning(){
        return !threads.isEmpty();
    }
    public void countIteration(){
        synchronized(iterationSynchronizer){
            iterations++;
        }
    }
}