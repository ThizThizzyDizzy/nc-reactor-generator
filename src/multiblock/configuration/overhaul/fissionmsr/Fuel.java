package multiblock.configuration.overhaul.fissionmsr;
import java.util.ArrayList;
import java.util.Objects;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class Fuel{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public float efficiency;
    public int heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public Fuel(String name, float efficiency, int heat, int time, int criticality, boolean selfPriming){
        this.name = name;
        this.efficiency = efficiency;
        this.heat = heat;
        this.time = time;
        this.criticality = criticality;
        this.selfPriming = selfPriming;
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
        config.set("time", time);
        config.set("criticality", criticality);
        config.set("selfPriming", selfPriming);
        return config;
    }
    @Override
    public String toString(){
        return name;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Fuel){
            Fuel f = (Fuel)obj;
            return Objects.equals(name, f.name)
                    &&efficiency==f.efficiency
                    &&heat==f.heat
                    &&time==f.time
                    &&criticality==f.criticality
                    &&selfPriming==f.selfPriming;
        }
        return false;
    }
}