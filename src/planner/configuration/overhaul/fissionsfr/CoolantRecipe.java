package planner.configuration.overhaul.fissionsfr;
import simplelibrary.config2.Config;
public class CoolantRecipe{
    public String name;
    public String input;
    public String output;
    public int heat;
    public int outputRatio;
    public CoolantRecipe(String name, String input, String output, int heat, int outputRatio){
        this.name = name;
        this.input = input;
        this.output = output;
        this.heat = heat;
        this.outputRatio = outputRatio;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        config.set("input", input);
        config.set("output", output);
        config.set("heat", heat);
        config.set("outputRatio", outputRatio);
        return config;
    }
}