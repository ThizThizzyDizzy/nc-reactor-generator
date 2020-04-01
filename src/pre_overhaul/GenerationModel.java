package pre_overhaul;
import common.Setting;
import common.SettingInt;
import common.ThingWithSettings;
import common.SettingDouble;
import common.WeightedRandom;
import java.util.ArrayList;
import java.util.Random;
public abstract class GenerationModel extends ThingWithSettings{
    public static final ArrayList<GenerationModel> models = new ArrayList<>();
    public static final GenerationModel DEFAULT;
    static{
        models.add(new GenerationModel("Random", "Generates completely random reactors") {
            @Override
            public Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand){
                return Reactor.random(fuel,x,y,z,rand);
            }
        });
        models.add(new GenerationModel("Standard", "Generates random reactors until a valid reactor is found, then changes some random parts of the reactor to random other parts- if the result is better, keep the changes. if not, discard.", new SettingDouble("Change Chance", 1, 0.1f, 100, .1f)) {
            @Override
            public Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand){
                if(last!=null&&last.isValid()){
                    return new Reactor(fuel, x, y, z){
                        @Override
                        protected ReactorPart build(int X, int Y, int Z){
                            if(rand.nextDouble()<getDouble("Change Chance")/100d){
                                return ReactorPart.random(rand);
                            }else{
                                return last.parts[X][Y][Z];
                            }
                        }
                    };
                }
                return Reactor.random(fuel,x,y,z,rand);
            }
        });
        models.add(new GenerationModel("bzy-xyz", "Generates reactors loosely following the pattern of bzy-xyz's reactor generator\nhttps://github.com/bzy-xyz/nuclearcraft-fission\nNote: The actual script generally runs faster and better than this model",
                new SettingInt("Difference Reactors", 100, 1),
                new SettingInt("Max Differences", 2, 1),
                new SettingInt("Core Difference Reactors", 50, 1),
                new SettingInt("Max Core Differences", 4, 1),
                new SettingInt("Iterations with symmetry", 500, 0),
                new SettingDouble("Revert Chance", 1/2.5d, 0, 100, .1)){
            private final Object synchronizer = new Object();
            private int symmetries = -1;
            private Reactor best = null;
            @Override
            public Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand){
                if(last==null)last = Reactor.empty(fuel,x,y,z);
                final Reactor basis = last;
                ArrayList<Reactor> storedReactors = new ArrayList<>();
                ArrayList<Difference> sensibleDifferences = new ArrayList<>();
                //<editor-fold defaultstate="collapsed" desc="Generate sensible differences">
                for(int X = 0; X<x; X++){
                    for(int Y = 0; Y<y; Y++){
                        for(int Z = 0; Z<z; Z++){
                            int xx = X;
                            int yy = Y;
                            int zz = Z;
                            for(ReactorPart part : ReactorPart.parts){
                                if(part==ReactorPart.AIR)continue;
                                if(part==ReactorPart.FUEL_CELL)continue;
                                Reactor test = new Reactor(fuel, x, y, z){
                                    @Override
                                    protected ReactorPart build(int X, int Y, int Z){
                                        if(X==xx&&Y==yy&&Z==zz)return part;
                                        return basis.parts[X][Y][Z];
                                    }
                                };
                                if(test.parts[X][Y][Z]==part)sensibleDifferences.add(new Difference(X, Y, Z, part));
                            }
                        }
                    }
                }
//</editor-fold>
                int symm;
                synchronized(synchronizer){
                    if(symmetries==-1)symmetries = getInteger("symmetry");
                    symm = symmetries;
                }
                if(!sensibleDifferences.isEmpty()){
                    for(int i = 0; i<getInteger("Difference Reactors"); i++){
                        int count = rand.nextInt(getInteger("Max Differences"))+1;
                        ArrayList<Difference> chosenDifferences = new ArrayList<>();
                        for(int j = 0; j<count; j++){
                            chosenDifferences.add(sensibleDifferences.get(rand.nextInt(sensibleDifferences.size())));
                        }
                        storedReactors.add(new Reactor(fuel, x, y, z, symm>0, symm>0, symm>0){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                for(Difference d : chosenDifferences){
                                    if(d.x==X&&d.y==Y&&d.z==Z)return d.part;
                                }
                                return basis.parts[X][Y][Z];
                            }
                        });
                    }
                }
                for(int i = 0; i<getInteger("Core Difference Reactors"); i++){
                    int count = rand.nextInt(getInteger("Max Core Differences"))+1;
                    ArrayList<Difference> chosenDifferences = new ArrayList<>();
                    for(int j = 0; j<count; j++){
                        int p = rand.nextInt(3);
                        chosenDifferences.add(new Difference(rand.nextInt(x), rand.nextInt(y), rand.nextInt(z), p==0?ReactorPart.FUEL_CELL:(p==1?ReactorPart.GRAPHITE:ReactorPart.AIR)));
                    }
                    storedReactors.add(new Reactor(fuel, x, y, z, symm>0, symm>0, symm>0){
                        @Override
                        protected ReactorPart build(int X, int Y, int Z){
                            for(Difference d : chosenDifferences){
                                if(d.x==X&&d.y==Y&&d.z==Z)return d.part;
                            }
                            return basis.parts[X][Y][Z];
                        }
                    });
                }
                Reactor r = WeightedRandom.random(storedReactors);
                synchronized(synchronizer){
                    if(symmetries>0){
                        symmetries--;
                    }
                    if(Reactor.isbetter(r, best))best = r;
                }
                if(rand.nextDouble()<(getDouble("Revert Chance")/100))return best;
                return r;
            }
        });
        DEFAULT = get("Standard");
    }
    private static GenerationModel get(String name){
        for(GenerationModel model : models){
            if(model.name.equalsIgnoreCase(name)){
                return model;
            }
        }
        return null;
    }
    private final String name;
    public final String description;
    public GenerationModel(String name, String description, Setting... settings){
        super(settings);
        this.name = name;
        this.description = description;
    }
    @Override
    public String toString(){
        return "Generation model: "+name;
    }
    public abstract Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand);
    private static class Difference{//used for bzy-xyz model
        public final int x;
        public final int y;
        public final int z;
        public final ReactorPart part;
        public Difference(int x, int y, int z, ReactorPart part){
            this.x = x;
            this.y = y;
            this.z = z;
            this.part = part;
        }
    }
}