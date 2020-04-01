package pre_overhaul;
import java.util.ArrayList;
public abstract class Priority{
    public static final ArrayList<Priority> priorities = new ArrayList<>();
    static{
        priorities.add(new Priority("Stable (Non-positive heat)"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                if(main.isValid()&&!other.isValid())return 1;
                if(!main.isValid()&&other.isValid())return -1;
                if(!main.isValid()&&!other.isValid())return 0;
                if(main.heat>0&&other.heat<=0)return -1;
                if(main.heat<=0&&other.heat>0)return 1;
                if(main.heat<=0&&other.heat<=0)return 0;
                return other.heat-main.heat;
            }
            @Override
            public double score(Reactor reactor){
                if(!reactor.isValid())return 0;
                if(reactor.heat<=0)return 0;
                return -reactor.heat;
            }
        });
        priorities.add(new Priority("Efficiency"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return (int) Math.round(main.totalEfficiency*100-other.totalEfficiency*100);
            }
            @Override
            public double score(Reactor reactor){
                return reactor.totalEfficiency;
            }
        });
        priorities.add(new Priority("Output"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.power-other.power;
            }
            @Override
            public double score(Reactor reactor){
                return reactor.power;
            }
        });
        priorities.add(new Priority("Minimize Heat"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return other.heat-main.heat;
            }
            @Override
            public double score(Reactor reactor){
                return -reactor.heat;
            }
        });
        priorities.add(new Priority("Fuel usage"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.getFuelSpeed()-other.getFuelSpeed();
            }
            @Override
            public double score(Reactor reactor){
                return reactor.getFuelSpeed();
            }
        });
        priorities.add(new Priority("Cell count"){
            @Override
            protected double doCompare(Reactor main, Reactor other){
                return main.getFuelCells()-other.getFuelCells();
            }
            @Override
            public double score(Reactor reactor){
                return reactor.getFuelCells();
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
        return doCompare(main, other);//TODO replace this with a comparison of reactor scores
    }
    protected abstract double doCompare(Reactor main, Reactor other);
    public abstract double score(Reactor reactor);
}