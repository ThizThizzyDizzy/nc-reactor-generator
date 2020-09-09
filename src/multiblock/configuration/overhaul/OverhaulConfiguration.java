package multiblock.configuration.overhaul;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration;
import multiblock.configuration.overhaul.turbine.TurbineConfiguration;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import simplelibrary.config2.Config;
public class OverhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public FissionMSRConfiguration fissionMSR;
    public TurbineConfiguration turbine;
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null)config.set("fissionSFR", fissionSFR.save(parent, partial));
        if(fissionMSR!=null)config.set("fissionMSR", fissionMSR.save(parent, partial));
        if(turbine!=null)config.set("turbine", turbine.save(parent, partial));
        return config;
    }
    public void apply(OverhaulConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        if(fissionSFR!=null){
            partial.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.apply(partial.fissionSFR, multiblocks, parent);
        }
        if(fissionMSR!=null){
            partial.fissionMSR = new FissionMSRConfiguration();
            fissionMSR.apply(partial.fissionMSR, multiblocks, parent);
        }
        if(turbine!=null){
            partial.turbine = new TurbineConfiguration();
            turbine.apply(partial.turbine, multiblocks, parent);
        }
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        if(fissionSFR!=null){
            addon.overhaul.fissionSFR = new FissionSFRConfiguration();
            addon.self.overhaul.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.apply(addon, parent);
        }
        if(fissionMSR!=null){
            addon.overhaul.fissionMSR = new FissionMSRConfiguration();
            addon.self.overhaul.fissionMSR = new FissionMSRConfiguration();
            fissionMSR.apply(addon, parent);
        }
        if(turbine!=null){
            addon.overhaul.turbine = new TurbineConfiguration();
            addon.self.overhaul.turbine = new TurbineConfiguration();
            turbine.apply(addon, parent);
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof OverhaulConfiguration){
            return Objects.equals(fissionSFR, ((OverhaulConfiguration)obj).fissionSFR)
                    &&Objects.equals(fissionMSR, ((OverhaulConfiguration)obj).fissionMSR)
                    &&Objects.equals(turbine, ((OverhaulConfiguration)obj).turbine);
        }
        return super.equals(obj);
    }
}