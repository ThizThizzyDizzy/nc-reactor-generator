package multiblock.configuration.overhaul;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration;
import multiblock.configuration.overhaul.turbine.TurbineConfiguration;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
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