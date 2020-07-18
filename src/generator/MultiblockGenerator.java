package generator;
import java.util.ArrayList;
import java.util.UUID;
import multiblock.Multiblock;
import planner.menu.component.MenuComponentMinimaList;
public abstract class MultiblockGenerator{
    public static final ArrayList<MultiblockGenerator> generators = new ArrayList<>();
    static{
        generators.add(new StandardGenerator(null));
    }
    ArrayList<Priority> priorities;
    private Object threadronyzer = new Object();
    protected final Multiblock multiblock;
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
    public abstract void refreshSettings();
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
                while(threads.contains(uid)){
                    tick();
                }
            });
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
    }
}