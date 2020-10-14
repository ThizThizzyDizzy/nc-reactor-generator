package multiblock.configuration.overhaul.fusion;
import java.util.Objects;
import simplelibrary.config2.Config;
public class BreedingBlanketRecipe{
    public String name;
    public float efficiency;
    public int heat;
    public BreedingBlanketRecipe(String name, float efficiency, int heat){
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
        if(obj!=null&&obj instanceof BreedingBlanketRecipe){
            BreedingBlanketRecipe r = (BreedingBlanketRecipe)obj;
            return Objects.equals(name, r.name)
                    &&efficiency==r.efficiency
                    &&heat==r.heat;
        }
        return false;
    }
}