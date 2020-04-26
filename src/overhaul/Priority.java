package overhaul;
import java.util.ArrayList;
public abstract class Priority{
    public static final ArrayList<Priority> priorities = new ArrayList<>();
    static{
        priorities.add(new Priority("Valid (>0 output)"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                if(!main.isValid()&&!other.isValid())return 0;
                return 0;
            }
        });
        priorities.add(new Priority("Stability"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                if(main.netHeat>0&&other.netHeat<0)return -1;
                if(main.netHeat<0&&other.netHeat>0)return -1;
                if(main.netHeat<0&&other.netHeat<0)return 0;
                return Math.max(0, other.netHeat)-Math.max(0, main.netHeat);
            }
        });
        priorities.add(new Priority("Efficiency"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
        });
        priorities.add(new Priority("Output"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.totalOutput-other.totalOutput;
            }
        });
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
        for(Priority p : priorities){
            if(p.name.equalsIgnoreCase(name))return p;
        }
        return null;
    }
    public static void moveToEnd(String name){
        Priority p = get(name);
        if(p==null)return;
        priorities.remove(p);
        priorities.add(p);
    }
}