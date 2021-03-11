package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class Fuel{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();//TODO texture;
    public float power;
    public float heat;
    public int time;
    public Fuel(String name, float power, float heat, int time){
        this.name = name;
        this.power = power;
        this.heat = heat;
        this.time = time;
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
        config.set("power", power);
        config.set("heat", heat);
        config.set("time", time);
        return config;
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof Fuel){
            return name.equals(((Fuel)obj).name)
                    &&power==((Fuel)obj).power
                    &&heat==((Fuel)obj).heat
                    &&time==((Fuel)obj).time;
        }
        return super.equals(obj);
    }
}