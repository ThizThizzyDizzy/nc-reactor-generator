package pre_overhaul;
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
                if(main.heat>0&&other.heat<0)return -1;
                if(main.heat<0&&other.heat>0)return 1;
                if(main.heat<0&&other.heat<0)return 0;
                return Math.max(0, other.heat)-Math.max(0, main.heat);
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
                return main.power-other.power;
            }
        });
        priorities.add(new Priority("Minimize Heat"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return other.heat-main.heat;
            }
        });
        priorities.add(new Priority("Fuel usage"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.getFuelSpeed()-other.getFuelSpeed();
            }
        });
        priorities.add(new Priority("Cell count"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.getFuelCells()-other.getFuelCells();
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