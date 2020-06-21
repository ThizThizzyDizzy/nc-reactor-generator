package planner.configuration.overhaul;
import planner.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import simplelibrary.config2.Config;
public class OverhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public Config save(){
        Config config = Config.newConfig();
        if(fissionSFR!=null)config.set("fissionSFR", fissionSFR.save());
        return config;
    }
}