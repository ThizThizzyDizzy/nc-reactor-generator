package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class CoolantRecipe{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public String input;
    public String output;
    public int heat;
    public float outputRatio;
    public CoolantRecipe(String name, String input, String output, int heat, float outputRatio){
        this.name = name;
        this.input = input;
        this.output = output;
        this.heat = heat;
        this.outputRatio = outputRatio;
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
        config.set("heat", heat);
        config.set("outputRatio", outputRatio);
        return config;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof CoolantRecipe){
            CoolantRecipe r = (CoolantRecipe)obj;
            return Objects.equals(name, r.name)
                    &&Objects.equals(input, r.input)
                    &&Objects.equals(output, r.output)
                    &&heat==r.heat
                    &&outputRatio==r.outputRatio;
        }
        return false;
    }
}