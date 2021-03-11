package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class IrradiatorRecipe{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public float efficiency;
    public float heat;
    public IrradiatorRecipe(String name, float efficiency, float heat){
        this.name = name;
        this.efficiency = efficiency;
        this.heat = heat;
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
        config.set("efficiency", efficiency);
        config.set("heat", heat);
        return config;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof IrradiatorRecipe){
            IrradiatorRecipe r = (IrradiatorRecipe)obj;
            return Objects.equals(name, r.name)
                    &&efficiency==r.efficiency
                    &&heat==r.heat;
        }
        return false;
    }
}