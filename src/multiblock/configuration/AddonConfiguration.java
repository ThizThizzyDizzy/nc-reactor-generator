package multiblock.configuration;
import simplelibrary.config2.Config;
public class AddonConfiguration extends Configuration{
    public static AddonConfiguration generate(Configuration parent, Configuration addon){
        AddonConfiguration add = new AddonConfiguration(addon.name, addon.overhaulVersion, addon.underhaulVersion);
        addon.apply(add, parent);
        return add;
    }
    public static AddonConfiguration convert(Configuration configuration){
        if(!configuration.addon)throw new IllegalArgumentException("This is not an addon!");
        AddonConfiguration addon = new AddonConfiguration(configuration.name, configuration.overhaulVersion, configuration.underhaulVersion);
        addon.underhaul = configuration.underhaul;
        addon.overhaul = configuration.overhaul;
        addon.addons.addAll(configuration.addons);
        int found = 0;
        for(Configuration config : configuration.addons){
            if(config.nameAndVersionMatches(configuration)){
                addon.self = config;
                found++;
            }
        }
        if(found<1)throw new IllegalArgumentException("Addon configuration contains no self addon!");
        if(found>1)throw new IllegalArgumentException("Addon configuration contains multiple self addons!");
        return addon;
    }
    public Configuration self;
    private AddonConfiguration(String name, String overhaulVersion, String underhaulVersion){
        super(name, overhaulVersion, underhaulVersion);
        self = new Configuration(name, overhaulVersion, underhaulVersion);
        self.addon = true;
        addons.add(self);
        addon = true;
    }
    @Override
    public Config save(Configuration parent, Config config){
        if(self.underhaul!=null&&self.underhaul.fissionSFR!=null){
            underhaul.fissionSFR.allBlocks.removeAll(self.underhaul.fissionSFR.blocks);
            underhaul.fissionSFR.allBlocks.addAll(self.underhaul.fissionSFR.blocks);
            underhaul.fissionSFR.allFuels.removeAll(self.underhaul.fissionSFR.fuels);
            underhaul.fissionSFR.allFuels.addAll(self.underhaul.fissionSFR.fuels);
        }
        if(self.overhaul!=null&&self.overhaul.fissionSFR!=null){
            overhaul.fissionSFR.allBlocks.removeAll(self.overhaul.fissionSFR.blocks);
            overhaul.fissionSFR.allBlocks.addAll(self.overhaul.fissionSFR.blocks);
            overhaul.fissionSFR.allCoolantRecipes.removeAll(self.overhaul.fissionSFR.coolantRecipes);
            overhaul.fissionSFR.allCoolantRecipes.addAll(self.overhaul.fissionSFR.coolantRecipes);
            overhaul.fissionSFR.allFuels.removeAll(self.overhaul.fissionSFR.fuels);
            overhaul.fissionSFR.allFuels.addAll(self.overhaul.fissionSFR.fuels);
            overhaul.fissionSFR.allIrradiatorRecipes.removeAll(self.overhaul.fissionSFR.irradiatorRecipes);
            overhaul.fissionSFR.allIrradiatorRecipes.addAll(self.overhaul.fissionSFR.irradiatorRecipes);
            overhaul.fissionSFR.allSources.removeAll(self.overhaul.fissionSFR.sources);
            overhaul.fissionSFR.allSources.addAll(self.overhaul.fissionSFR.sources);
        }
        if(self.overhaul!=null&&self.overhaul.fissionMSR!=null){
            overhaul.fissionMSR.allBlocks.removeAll(self.overhaul.fissionMSR.blocks);
            overhaul.fissionMSR.allBlocks.addAll(self.overhaul.fissionMSR.blocks);
            overhaul.fissionMSR.allFuels.removeAll(self.overhaul.fissionMSR.fuels);
            overhaul.fissionMSR.allFuels.addAll(self.overhaul.fissionMSR.fuels);
            overhaul.fissionMSR.allIrradiatorRecipes.removeAll(self.overhaul.fissionMSR.irradiatorRecipes);
            overhaul.fissionMSR.allIrradiatorRecipes.addAll(self.overhaul.fissionMSR.irradiatorRecipes);
            overhaul.fissionMSR.allSources.removeAll(self.overhaul.fissionMSR.sources);
            overhaul.fissionMSR.allSources.addAll(self.overhaul.fissionMSR.sources);
        }
        if(self.overhaul!=null&&self.overhaul.turbine!=null){
            overhaul.turbine.allCoils.removeAll(self.overhaul.turbine.coils);
            overhaul.turbine.allCoils.addAll(self.overhaul.turbine.coils);
            overhaul.turbine.allBlades.removeAll(self.overhaul.turbine.blades);
            overhaul.turbine.allBlades.addAll(self.overhaul.turbine.blades);
            overhaul.turbine.allRecipes.removeAll(self.overhaul.turbine.recipes);
            overhaul.turbine.allRecipes.addAll(self.overhaul.turbine.recipes);
        }
        addons.remove(self);
        addons.add(self);//Make sure it's always at the end of the list
        return super.save(parent, config);
    }
    @Override
    public boolean isPartial(){
        return true;
    }
}