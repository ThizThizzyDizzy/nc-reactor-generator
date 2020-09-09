package multiblock.configuration;
import simplelibrary.config2.Config;
public class AddonConfiguration extends Configuration{
    public static AddonConfiguration generate(Configuration parent, Configuration addon){
        AddonConfiguration add = new AddonConfiguration(addon.name, addon.overhaulVersion, addon.underhaulVersion);
        addon.apply(add, parent);
        return add;
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
    public boolean isPartial(){
        return true;
    }
}