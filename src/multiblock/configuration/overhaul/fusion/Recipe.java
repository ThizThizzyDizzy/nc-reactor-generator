package multiblock.configuration.overhaul.fusion;
import java.util.Objects;
import simplelibrary.config2.Config;
public class Recipe{
    public String name;
    public float efficiency;
    public int heat;
    public int time;
    public float fluxiness;
    public Recipe(String name, float efficiency, int heat, int time, float fluxiness){
        this.name = name;
        this.efficiency = efficiency;
        this.heat = heat;
        this.time = time;
        this.fluxiness = fluxiness;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        config.set("efficiency", efficiency);
        config.set("heat", heat);
        config.set("time", time);
        config.set("fluxiness", fluxiness);
        return config;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Recipe){
            Recipe r = (Recipe)obj;
            return Objects.equals(name, r.name)
                    &&efficiency==r.efficiency
                    &&heat==r.heat
                    &&time==r.time
                    &&fluxiness==r.fluxiness;
        }
        return false;
    }
}