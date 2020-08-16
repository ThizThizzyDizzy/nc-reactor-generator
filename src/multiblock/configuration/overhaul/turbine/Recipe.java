package multiblock.configuration.overhaul.turbine;
import simplelibrary.config2.Config;
public class Recipe{
    public String name;
    public String input;
    public String output;
    public double power;
    public double coefficient;
    public Recipe(String name, String input, String output, double power, double coefficient){
        this.name = name;
        this.input = input;
        this.output = output;
        this.power = power;
        this.coefficient = coefficient;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        config.set("input", input);
        config.set("output", output);
        config.set("power", power);
        config.set("coefficient", coefficient);
        return config;
    }
}