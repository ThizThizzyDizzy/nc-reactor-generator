package overhaul;
import common.Setting;
import common.SettingInt;
import common.ThingWithSettings;
import java.util.ArrayList;
import java.util.Random;
public abstract class GenerationPlan extends ThingWithSettings{
    public static final ArrayList<GenerationPlan> plans = new ArrayList<>();
    public static final GenerationPlan DEFAULT;
    static{
        plans.add(new GenerationPlan("Mono", "Generates reactors following the selected model."){
            private final Object synchronizer = new Object();
            private Reactor imported;
            private Reactor reactor;
            private Reactor lastGuess;
            @Override
            public void run(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand){
                Reactor r;
                synchronized(synchronizer){
                    r = reactor;
                }
                Reactor newReactor = Main.genModel.generate(r, fuel, type, x, y, z, rand);
                if(newReactor==null)return;
                if(Reactor.isbetter(newReactor,r)){
                    synchronized(synchronizer){
                        reactor = newReactor;
                    }
                }
                lastGuess = newReactor;
            }
            @Override
            public ArrayList<Reactor> getReactors(){
                ArrayList<Reactor> reactors = new ArrayList<>();
                synchronized(synchronizer){
                    reactors.add(reactor==null?imported:reactor);
                }
                reactors.add(lastGuess);
                return reactors;
            }
            @Override
            public void reset(Fuel fuel, Fuel.Type type, int x, int y, int z){
                if(imported!=null&&(imported.fuel!=fuel||imported.fuelType!=type||imported.x!=x||imported.y!=y||imported.z!=z))imported = null;
                reactor = imported;
                imported = null;
            }
            @Override
            public void importReactor(Reactor reactor, boolean running){
                if(running){
                    synchronized(synchronizer){
                        if(Reactor.isbetter(reactor, this.reactor))this.reactor = reactor;
                    }
                }else imported = reactor;
            }
        });
        plans.add(new GenerationPlan("Multi", "Generates many reactors in parallel. If one stops improving, it is scrapped and reset.\nCreates a new thread for each reactor", new SettingInt("Reactors", 2, 2, 64), new SettingInt("Merge Timeout (Seconds)",2,1)) {
            private final Object synchronizer = new Object();
            private int index = 0;
            private Reactor[] reactors;
            private Long[] lastUpdateTimes;
            private Reactor imported;
            @Override
            public void run(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand){
                synchronized(synchronizer){
                    if(reactors==null){
                        int num = (int)getSetting("Reactors");
                        reactors = new Reactor[num];
                        if(imported!=null){
                            reactors[0] = imported;
                            imported = null;
                        }
                        lastUpdateTimes = new Long[num];
                    }
                }
                int idx;
                synchronized(synchronizer){
                    idx = index;//grab ours
                    index++;//give the next thread the next one
                    if(index>=reactors.length){
                        index = 0;
                    }
                }
                long diff;
                Reactor reactor;
                synchronized(synchronizer){
                    reactor = reactors[idx];
                    long time = System.nanoTime();
                    diff = time-(lastUpdateTimes[idx]==null?time:lastUpdateTimes[idx]);
                }
                if(idx>0&&diff/1_000_000_000>(int)getSetting("Merge Timeout")){
                    synchronized(synchronizer){
                        if(Reactor.isbetter(reactor, reactors[0]))reactors[0] = reactor;
                    }
                    reactor = null;
                    synchronized(synchronizer){
                        reactors[idx] = null;
                    }
                }
                Reactor r = Main.genModel.generate(reactor, fuel, type, x, y, z, rand);
                synchronized(synchronizer){
                    if(Reactor.isbetter(r, reactors[idx])){
                        reactors[idx] = r;
                        lastUpdateTimes[idx] = System.nanoTime();
                    }
                }
            }
            @Override
            public ArrayList<Reactor> getReactors(){
                ArrayList<Reactor> reactors = new ArrayList<>();
                synchronized(synchronizer){
                    if(this.reactors==null){
                        reactors.add(imported);
                    }else{
                        for(Reactor r : this.reactors){
                            reactors.add(r);
                        }
                    }
                }
                return reactors;
            }
            @Override
            public String getDetails(ArrayList<Reactor> reactors){
                String details = "";
                if(reactors==null)return details;
                if(lastUpdateTimes==null)return details;
                synchronized(synchronizer){
                    for(int i = 0; i<reactors.size(); i++){
                        if(reactors.get(i)==null)continue;
                        if(lastUpdateTimes[i]==null)continue;
                        details+="Time since last update: "+Main.toTime(System.nanoTime()-lastUpdateTimes[i])+"\n"+reactors.get(i).getDetails()+"\n\n";
                    }
                }
                return details;
            }
            @Override
            public void reset(Fuel fuel, Fuel.Type type, int x, int y, int z){
                if(imported!=null&&(imported.fuel!=fuel||imported.fuelType!=type||imported.x!=x||imported.y!=y||imported.z!=z))imported = null;
                reactors = null;
                lastUpdateTimes = null;
            }
            @Override
            public void importReactor(Reactor reactor, boolean running){
                if(running){
                    synchronized(synchronizer){
                        if(reactors==null)return;
                        for(int i = 0; i<this.reactors.length; i++){
                            if(Reactor.isbetter(reactor, this.reactors[i])){
                                this.reactors[i] = reactor;
                                break;
                            }
                        }
                    }
                }else imported = reactor;
            }
        });
        DEFAULT = get("Multi");
    }
    private static GenerationPlan get(String name){
        for(GenerationPlan plan : plans){
            if(plan.name.equalsIgnoreCase(name)){
                return plan;
            }
        }
        return null;
    }
    public final String name;
    public final String description;
    public GenerationPlan(String name, String description, Setting... settings){
        super(settings);
        this.name = name;
        this.description = description;
    }
    @Override
    public String toString(){
        return "Generation plan: "+name;
    }
    public String getDetails(ArrayList<Reactor> reactors){
        String details = "";
        for(Reactor reactor : reactors){
            if(reactor==null)continue;
            details+=reactor.getDetails()+"\n\n";
        }
        return details;
    }
    public abstract void run(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand);
    public abstract ArrayList<Reactor> getReactors();
    public abstract void reset(Fuel fuel, Fuel.Type type, int x, int y, int z);
    public abstract void importReactor(Reactor reactor, boolean running);
}