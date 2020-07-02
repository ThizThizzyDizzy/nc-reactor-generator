package planner.configuration.overhaul;
import java.util.ArrayList;
import planner.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import planner.configuration.overhaul.fissionmsr.FissionMSRConfiguration;
import planner.multiblock.Multiblock;
import simplelibrary.config2.Config;
public class OverhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public FissionMSRConfiguration fissionMSR;
    public Config save(boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null)config.set("fissionSFR", fissionSFR.save(partial));
        if(fissionSFR!=null)config.set("fissionMSR", fissionMSR.save(partial));
        return config;
    }
    public void applyPartial(OverhaulConfiguration partial, ArrayList<Multiblock> multiblocks){
        if(fissionSFR!=null){
            partial.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.applyPartial(partial.fissionSFR, multiblocks);
        }
        if(fissionMSR!=null){
            partial.fissionMSR = new FissionMSRConfiguration();
            fissionMSR.applyPartial(partial.fissionMSR, multiblocks);
        }
    }
}