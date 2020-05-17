package underhaul;
import common.Scorable;
import common.Setting;
import common.SettingBoolean;
import common.SettingInt;
import common.ThingWithSettings;
import common.SettingDouble;
import common.WeightedRandom;
import java.util.ArrayList;
import java.util.Collections;
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
        models.add(new GenerationModel("Standard", "Generates random reactors until a valid reactor is found, then changes some random parts of the reactor to random other parts- if the result is better, keep the changes. if not, discard.\nIf rate is variable, each block has an x% chance of changing\nIf rate is fixed, exactly x% of the blocks will be changed (minimum 1)", new SettingDouble("Change Chance", 1, 0.1f, 100, .1f), new SettingBoolean("Variable Rate", true), new SettingBoolean("Fill Air", true)){
            @Override
            public Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand){
                if(last!=null&&last.isValid()){
                    if(getBoolean("Variable")){
                        return new Reactor(fuel, x, y, z){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                if(rand.nextDouble()<((getBoolean("Fill Air")&&last.parts[X][Y][Z]==ReactorPart.AIR)?1:(getDouble("Change Chance")/100d))){
                                    return ReactorPart.random(rand);
                                }else{
                                    return last.parts[X][Y][Z];
                                }
                            }
                        };
                    }else{
                        long changes = Math.max(1, Math.round((double)getDouble("Change Chance")*x*y*z));
                        ArrayList<int[]> pool = new ArrayList<>();
                        ReactorPart[][][] prts = new ReactorPart[x][y][z];
                        for(int X = 0; X<prts.length; X++){
                            for(int Y = 0; Y<prts[X].length; Y++){
                                for(int Z = 0; Z<prts[Y].length; Z++){
                                    pool.add(new int[]{X,Y,Z});
                                }
                            }
                        }
                        for(int i = 0; i<changes; i++){//so it can't change the same cell twice
                            if(pool.isEmpty())break;
                            int[] pos = pool.remove(rand.nextInt(pool.size()));
                            prts[pos[0]][pos[1]][pos[2]] = ReactorPart.random(rand);
                        }
                        return new Reactor(fuel, x, y, z){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                if(prts[X][Y][Z]!=null)return prts[X][Y][Z];
                                return last.parts[X][Y][Z];
                            }
                        };
                    }
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
                double weight = 1;
                ArrayList<Scorable<Reactor>> scorableList = new ArrayList<>();
                while(!storedReactors.isEmpty()){
                    Reactor best = null;
                    for(Reactor r : storedReactors){
                        if(best==null||Reactor.isbetter(r, best)){
                            best = r;
                        }
                    }
                    storedReactors.remove(best);
                    scorableList.add(new Scorable<>(best, weight));
                    weight/=2;
                }
                Reactor r = WeightedRandom.random(scorableList).get();
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
    public static GenerationModel get(String name){
        for(GenerationModel model : models){
            if(model.name.equalsIgnoreCase(name)){
                return model;
            }
        }
        if(name.equalsIgnoreCase("none")){
            return new GenerationModel("None", "Does nothing"){
                @Override
                public Reactor generate(Reactor last, Fuel fuel, int x, int y, int z, Random rand){
                    if(last==null)return new Reactor(fuel, x, y, z){
                        @Override
                        protected ReactorPart build(int X, int Y, int Z){
                            return ReactorPart.AIR;
                        }
                    };
                    return last;
                }
            };
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