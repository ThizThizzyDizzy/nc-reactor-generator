package planner.configuration.underhaul.fissionsfr;
import simplelibrary.config2.Config;
public class Fuel{
    public String name;
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
        config.set("power", power);
        config.set("heat", heat);
        config.set("time", time);
        return config;
    }
}