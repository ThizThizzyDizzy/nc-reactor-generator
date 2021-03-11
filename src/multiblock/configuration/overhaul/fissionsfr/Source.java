package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Objects;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class Source{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public float efficiency;
    public Source(String name, float efficiency){
        this.name = name;
        this.efficiency = efficiency;
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