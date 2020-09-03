package multiblock.configuration.overhaul.fissionsfr;
import java.util.Objects;
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