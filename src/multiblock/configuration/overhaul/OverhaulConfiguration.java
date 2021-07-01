package multiblock.configuration.overhaul;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration;
import multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.configuration.overhaul.fusion.FusionConfiguration;
import multiblock.configuration.overhaul.turbine.TurbineConfiguration;
import planner.exception.MissingConfigurationEntryException;
import simplelibrary.config2.Config;
public class OverhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public FissionMSRConfiguration fissionMSR;
    public TurbineConfiguration turbine;
    public FusionConfiguration fusion;
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null)config.set("fissionSFR", fissionSFR.save(parent, partial));
        if(fissionMSR!=null)config.set("fissionMSR", fissionMSR.save(parent, partial));
        if(turbine!=null)config.set("turbine", turbine.save(parent, partial));
        if(fusion!=null)config.set("fusion", fusion.save(parent, partial));
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
        if(fusion!=null){
            partial.fusion = new FusionConfiguration();
            fusion.apply(partial.fusion, multiblocks, parent);
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
        if(fusion!=null){
            addon.overhaul.fusion = new FusionConfiguration();
            addon.self.overhaul.fusion = new FusionConfiguration();
            fusion.apply(addon, parent);
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof OverhaulConfiguration){
            return Objects.equals(fissionSFR, ((OverhaulConfiguration)obj).fissionSFR)
                    &&Objects.equals(fissionMSR, ((OverhaulConfiguration)obj).fissionMSR)
                    &&Objects.equals(turbine, ((OverhaulConfiguration)obj).turbine)
                    &&Objects.equals(fusion, ((OverhaulConfiguration)obj).fusion);
        }
        return super.equals(obj);
    }
    public void convertAddon(AddonConfiguration parent, Configuration convertTo) throws MissingConfigurationEntryException{
        if(fissionSFR!=null){
            fissionSFR.convertAddon(parent, convertTo);
        }
        if(fissionMSR!=null){
            fissionMSR.convertAddon(parent, convertTo);
        }
        if(turbine!=null){
            turbine.convertAddon(parent, convertTo);
        }
        if(fusion!=null){
            fusion.convertAddon(parent, convertTo);
        }
    }
    public void makeAddon(OverhaulConfiguration parent, OverhaulConfiguration addon){
        if(fissionSFR!=null){
            addon.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.makeAddon(parent.fissionSFR, addon.fissionSFR);
        }
        if(fissionMSR!=null){
            addon.fissionMSR = new FissionMSRConfiguration();
            fissionMSR.makeAddon(parent.fissionMSR, addon.fissionMSR);
        }
        if(turbine!=null){
            addon.turbine = new TurbineConfiguration();
            turbine.makeAddon(parent.turbine, addon.turbine);
        }
        if(fusion!=null){
            addon.fusion = new FusionConfiguration();
            fusion.makeAddon(parent.fusion, addon.fusion);
        }
    }
}