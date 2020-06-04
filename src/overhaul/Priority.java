package overhaul;
import java.util.ArrayList;
public abstract class Priority{
    public static final ArrayList<Priority> priorities = new ArrayList<>();
    public static final ArrayList<Preset> presets = new ArrayList<>();
    static{
        Priority valid = add(new Priority("Valid (>0 output)"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                if(!main.isValid()&&!other.isValid())return 0;
                return 0;
            }
        });
        Priority shutdown = add(new Priority("Shutdownable"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.getShutdownFactor()-other.getShutdownFactor();
            }
        });
        Priority stable = add(new Priority("Stability"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                if(main.netHeat>0&&other.netHeat<0)return -1;
                if(main.netHeat<0&&other.netHeat>0)return -1;
                if(main.netHeat<0&&other.netHeat<0)return 0;
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        Priority efficiency = add(new Priority("Efficiency"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        Priority output = add(new Priority("Output"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.totalOutput-other.totalOutput;
            }
        });
        presets.add(new Preset("Efficiency", valid, shutdown, stable, efficiency, output).addAlternative("Efficient"));
        presets.add(new Preset("Output", valid, shutdown, stable, output, efficiency));
        presets.get(0).set();
    }
    private static Priority add(Priority p){
        priorities.add(p);
        return p;
    }
    private final String name;
    public Priority(String name){
        this.name = name;
    }
    @Override
    public String toString(){
        return name;
    }
    public final double compare(Reactor main, Reactor other){
        if(main==null&&other==null)return 0;
        if(main==null&&other!=null)return -1;
        if(main!=null&&other==null)return 1;
        return doCompare(main, other);
    }
    protected abstract double doCompare(Reactor main, Reactor other);
    public static Priority get(String name){
        synchronized(priorities){
            for(Priority p : priorities){
                if(p.name.equalsIgnoreCase(name))return p;
            }
        }
        return null;
    }
    public static class Preset{
        public final String name;
        private final ArrayList<Priority> prior = new ArrayList<>();
        public final ArrayList<String> alternatives = new ArrayList<>();
        public Preset(String name, Priority... priorities){
            this.name = name;
            for(Priority p : priorities)prior.add(p);
            alternatives.add(name);
        }
        public Preset(String name, String... priorities){
            this.name = name;
            for(String s : priorities){
                Priority p = get(s);
                if(p!=null)prior.add(p);
            }
        }
        public void set(){
            ArrayList<Priority> irrelevant = new ArrayList<>();
            synchronized(priorities){
                irrelevant.addAll(priorities);
                irrelevant.removeAll(prior);
                priorities.clear();
                priorities.addAll(prior);
                priorities.addAll(irrelevant);
            }
        }
        private Preset addAlternative(String alt){
            alternatives.add(alt);
            return this;
        }
    }
}