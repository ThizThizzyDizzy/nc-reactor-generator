package multiblock.configuration.overhaul.fissionsfr;
import simplelibrary.config2.Config;
public class IrradiatorRecipe{
    public String name;
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
        config.set("efficiency", efficiency);
        config.set("heat", heat);
        return config;
    }
}