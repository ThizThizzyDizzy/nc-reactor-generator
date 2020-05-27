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
        models.add(new GenerationModel("Standard", "Generates random reactors until a valid reactor is found, then changes some random parts of the reactor to random other parts- if the result is better, keep the changes. if not, discard.", new SettingDouble("Change Chance", 1, 0.1, 100, .1), new SettingBoolean("Lock Core", false), new SettingBoolean("Fill Air", false)){
            @Override
            public Reactor generate(Reactor last, int x, int y, int z, Random rand){
                if(last!=null&&last.isValid()){
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