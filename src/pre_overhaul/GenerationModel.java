package pre_overhaul;
import java.util.ArrayList;
import java.util.Random;
public abstract class GenerationModel extends ThingWithSettings{
    public static final ArrayList<GenerationModel> models = new ArrayList<>();
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
                            if(rand.nextDouble()<(double)getSetting("Change Chance")/100d){
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
        setDefault("Standard");
    }
    private static void setDefault(String name){
        for(GenerationModel model : models){
            if(model.name.equalsIgnoreCase(name)){
                Main.genModel = model;
            }
        }
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
}