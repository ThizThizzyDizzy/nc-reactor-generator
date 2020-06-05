package overhaul;
import common.Setting;
import common.SettingBoolean;
import common.SettingDouble;
import common.ThingWithSettings;
import java.util.ArrayList;
import java.util.Random;
public abstract class GenerationModel extends ThingWithSettings{
    public static final ArrayList<GenerationModel> models = new ArrayList<>();
    public static final GenerationModel DEFAULT;
    static{
        models.add(new GenerationModel("Random", "Generates completely random reactors") {
            @Override
            public Reactor generate(Reactor last, int x, int y, int z, Random rand){
                return Reactor.random(x,y,z,rand);
            }
        });
        models.add(new GenerationModel("Standard", "Generates random reactors until a valid reactor is found, then changes some random parts of the reactor to random other parts- if the result is better, keep the changes. if not, discard.", new SettingDouble("Change Chance", 1, 0.1, 100, .1), new SettingBoolean("Variable Rate", true), new SettingBoolean("Lock Core", false), new SettingBoolean("Fill Air", false)){
            @Override
            public Reactor generate(Reactor last, int x, int y, int z, Random rand){
                if(last!=null&&last.isValid()){
                    if(getBoolean("Variable")){
                        return new Reactor(x, y, z){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                if(getBoolean("Lock Core")){
                                    if(ReactorPart.GROUP_CORE.contains(last.parts[X][Y][Z])){
                                        return last.parts[X][Y][Z];
                                    }
                                }
                                if(rand.nextDouble()<((getBoolean("Fill Air")&&last.parts[X][Y][Z]==ReactorPart.AIR)?1:(getDouble("Change Chance")/100d))){
                                    return ReactorPart.random(rand);
                                }else{
                                    return last.parts[X][Y][Z];
                                }
                            }
                            @Override
                            protected Fuel.Group buildFuel(int X, int Y, int Z){
                                return Main.instance.randomFuel();
                            }
                        };
                    }else{
                        int changes = (int) Math.max(1, Math.round((double)getDouble("Change Chance")*x*y*z));
                        ArrayList<int[]> pool = new ArrayList<>();
                        ReactorPart[][][] prts = new ReactorPart[x][y][z];
                        Fuel[][][] feuls = new Fuel[x][y][z];
                        Fuel.Type[][][] types = new Fuel.Type[x][y][z];
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
                            Fuel.Group fuel = Main.instance.randomFuel();
                            feuls[pos[0]][pos[1]][pos[2]] = fuel.fuel;
                            types[pos[0]][pos[1]][pos[2]] = fuel.type;
                        }
                        return new Reactor(x, y, z){
                            @Override
                            protected ReactorPart build(int X, int Y, int Z){
                                if(prts[X][Y][Z]!=null)return prts[X][Y][Z];
                                return last.parts[X][Y][Z];
                            }
                            @Override
                            protected Fuel.Group buildFuel(int X, int Y, int Z){
                                if(prts[X][Y][Z]!=null)return new Fuel.Group(feuls[X][Y][Z], types[X][Y][Z]);
                                return new Fuel.Group(last.fuel[X][Y][Z], last.fuelType[X][Y][Z]);
                            }
                        };
                    }
                }
                return Reactor.random(x,y,z,rand);
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
                public Reactor generate(Reactor last, int x, int y, int z, Random rand){
                    if(last==null)return new Reactor(x, y, z){
                        @Override
                        protected ReactorPart build(int X, int Y, int Z){
                            return ReactorPart.AIR;
                        }
                        @Override
                        protected Fuel.Group buildFuel(int X, int Y, int Z){
                            return null;
                        }
                    };
                    return last;
                }
            };
        }
        return null;
    }
    public final String name;
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
    public abstract Reactor generate(Reactor last, int x, int y, int z, Random rand);
}