package multiblock.configuration.underhaul;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import multiblock.Multiblock;
import multiblock.configuration.AddonConfiguration;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import simplelibrary.config2.Config;
public class UnderhaulConfiguration{
    public FissionSFRConfiguration fissionSFR;
    public Config save(Configuration parent, boolean partial){
        Config config = Config.newConfig();
        if(fissionSFR!=null){
            config.set("fissionSFR", fissionSFR.save(parent, partial));
        }
        return config;
    }
    public void apply(UnderhaulConfiguration partial, ArrayList<Multiblock> multiblocks, PartialConfiguration parent){
        if(fissionSFR!=null){
            partial.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.apply(partial.fissionSFR, multiblocks, parent);
        }
    }
    public void apply(AddonConfiguration addon, Configuration parent){
        if(fissionSFR!=null){
            addon.underhaul.fissionSFR = new FissionSFRConfiguration();
            addon.self.underhaul.fissionSFR = new FissionSFRConfiguration();
            fissionSFR.apply(addon, parent);
        }
    }
    @Override
    public boolean equals(Object obj){
        if(obj!=null&&obj instanceof UnderhaulConfiguration){
            return Objects.equals(fissionSFR, ((UnderhaulConfiguration)obj).fissionSFR);
        }
        return super.equals(obj);
    }
}