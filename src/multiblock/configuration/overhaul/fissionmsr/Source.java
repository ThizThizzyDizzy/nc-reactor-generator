package multiblock.configuration.overhaul.fissionmsr;
import java.util.Objects;
import simplelibrary.config2.Config;
public class Source{
    public String name;
    public float efficiency;
    public Source(String name, float efficiency){
        this.name = name;
        this.efficiency = efficiency;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        config.set("efficiency", efficiency);
        return config;
    }
    @Override
    public String toString(){
        return name;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Source){
            Source s = (Source)obj;
            return Objects.equals(name, s.name)
                    &&efficiency==s.efficiency;
        }
        return false;
    }
}