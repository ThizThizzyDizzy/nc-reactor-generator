package multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.Objects;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class Recipe{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
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
        if(displayName!=null)config.set("displayName", displayName);
        if(!legacyNames.isEmpty()){
            ConfigList lst = new ConfigList();
            for(String s : legacyNames)lst.add(s);
            config.set("legacyNames", lst);
        }
        config.set("input", input);
        config.set("output", output);
        config.set("power", power);
        config.set("coefficient", coefficient);
        return config;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Recipe){
            Recipe r = (Recipe)obj;
            return Objects.equals(r.name, name)
                    &&Objects.equals(r.input, input)
                    &&Objects.equals(r.output, output)
                    &&r.power==power
                    &&r.coefficient==coefficient;
        }
        return false;
    }
}