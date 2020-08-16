package multiblock.configuration.overhaul;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration;
import multiblock.configuration.overhaul.turbine.TurbineConfiguration;
import multiblock.Multiblock;
import simplelibrary.config2.Config;
public class OverhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public FissionMSRConfiguration fissionMSR;
    public TurbineConfiguration turbine;
    public Config save(boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null)config.set("fissionSFR", fissionSFR.save(partial));
        if(fissionMSR!=null)config.set("fissionMSR", fissionMSR.save(partial));
        if(turbine!=null)config.set("turbine", turbine.save(partial));
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
        if(turbine!=null){
            partial.turbine = new TurbineConfiguration();
            turbine.applyPartial(partial.turbine, multiblocks);
        }
    }
}