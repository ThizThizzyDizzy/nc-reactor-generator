package planner.configuration.underhaul;
import planner.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import simplelibrary.config2.Config;
public class UnderhaulConfiguration{
    public FissionSFRConfiguration fissionSFR = new FissionSFRConfiguration();
    public Config save(){
        Config config = Config.newConfig();
        if(fissionSFR!=null){
            config.set("fissionSFR", fissionSFR.save());
        }
        return config;
    }
}