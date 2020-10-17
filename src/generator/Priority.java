package generator;
import java.util.ArrayList;
import multiblock.Multiblock;
public abstract class Priority<T extends Multiblock>{
    public final String name;
    private final boolean core, isFinal;
    /**
     * @param name The priority name
     * @param core if this priority is available for multiblock cores
     * @param isFinal if this priority is available for final multiblocks
     */
    public Priority(String name, boolean core, boolean isFinal){
        this.name = name;
        this.core = core;
        this.isFinal = isFinal;
    }
    @Override
    public String toString(){
        return name;
    }
    public final double compare(T main, T other){
        if(main==null&&other==null)return 0;
        if(main==null&&other!=null)return -1;
        if(main!=null&&other==null)return 1;
        return doCompare(main, other);
    }
    protected abstract double doCompare(T main, T other);
    public boolean isCore(){
        return core;
    }
    public boolean isFinal(){
        return isFinal;
    }
    public static class Preset{
        public final String name;
        private final ArrayList<Priority> prior = new ArrayList<>();
        public ArrayList<String> alternatives = new ArrayList<>();
        public Preset(String name, Priority... priorities){
            this.name = name;
            for(Priority p : priorities)prior.add(p);
            alternatives.add(name);
        }
        public void set(ArrayList<Priority> priorities){
            ArrayList<Priority> irrelevant = new ArrayList<>();
            irrelevant.addAll(priorities);
            irrelevant.removeAll(prior);
            priorities.clear();
            priorities.addAll(prior);
            priorities.addAll(irrelevant);
        }
        public Preset addAlternative(String alt){
            alternatives.add(alt);
            return this;
        }
    }
}