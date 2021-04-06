package multiblock.configuration;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import planner.Core;
import planner.file.FileReader;
import planner.file.NCPFFile;
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
        addon.addons.clear();
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
        }
        if(self.overhaul!=null&&self.overhaul.fissionMSR!=null){
            overhaul.fissionMSR.allBlocks.removeAll(self.overhaul.fissionMSR.blocks);
            overhaul.fissionMSR.allBlocks.addAll(self.overhaul.fissionMSR.blocks);
        }
        if(self.overhaul!=null&&self.overhaul.turbine!=null){
            overhaul.turbine.allBlocks.removeAll(self.overhaul.turbine.blocks);
            overhaul.turbine.allBlocks.addAll(self.overhaul.turbine.blocks);
            overhaul.turbine.allRecipes.removeAll(self.overhaul.turbine.recipes);
            overhaul.turbine.allRecipes.addAll(self.overhaul.turbine.recipes);
        }
        if(self.overhaul!=null&&self.overhaul.fusion!=null){
            overhaul.fusion.allBlocks.removeAll(self.overhaul.fusion.blocks);
            overhaul.fusion.allBlocks.addAll(self.overhaul.fusion.blocks);
            overhaul.fusion.allRecipes.removeAll(self.overhaul.fusion.recipes);
            overhaul.fusion.allRecipes.addAll(self.overhaul.fusion.recipes);
            overhaul.fusion.allCoolantRecipes.removeAll(self.overhaul.fusion.coolantRecipes);
            overhaul.fusion.allCoolantRecipes.addAll(self.overhaul.fusion.coolantRecipes);
        }
        addons.remove(self);
        addons.add(self);//Make sure it's always at the end of the list
        return super.save(parent, config);
    }
    @Override
    public boolean isPartial(){
        return true;
    }
    public AddonConfiguration copy(){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Config header = Config.newConfig();
        header.set("version", NCPFFile.SAVE_VERSION);
        header.set("count", 0);
        header.save(out);
        /*AddonConfiguration.generate(Core.configuration, this).*/save(Core.configuration, Config.newConfig()).save(out);
        return AddonConfiguration.convert(FileReader.read(() -> {
            return new ByteArrayInputStream(out.toByteArray());
        }).configuration);
    }
}