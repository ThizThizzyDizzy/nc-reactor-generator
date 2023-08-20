package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FileReader;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
public class Configuration{
    public static final ArrayList<Configuration> configurations = new ArrayList<>();
    public static final ArrayList<Addon> internalAddons = new ArrayList<>();
    public static final HashMap<Addon, String> internalAddonLinks = new HashMap<>();
    public static Configuration NUCLEARCRAFT;
    public static void addInternalAddon(Addon addon, String link){
        internalAddons.add(addon);
        internalAddonLinks.put(addon, link);
    }
    public static void initNuclearcraftConfiguration(){
        if(NUCLEARCRAFT!=null)return;//already done m8
        NUCLEARCRAFT = new Configuration(FileReader.read(() -> {
            return Core.getInputStream("configurations/nuclearcraft.ncpf");
        }), "default").addAlternative("").addAlternative("SF4");
        configurations.add(0, NUCLEARCRAFT);
    }
    public static void clearConfigurations(){
        configurations.clear();
        if(NUCLEARCRAFT!=null)configurations.add(NUCLEARCRAFT);
        internalAddons.clear();
    }
    public Configuration(Project project, String path){
        project = project.copyTo(Project::new);
        this.configuration = project.configuration;
        this.addons = project.addons;
        this.path = path;
    }
    public Configuration(Project project){
        this(project, null);
    }
    public NCPFConfigurationContainer configuration;
    public List<Addon> addons = new ArrayList<>();
    public String path;
    public ArrayList<String> alternatives = new ArrayList<>();
    public String getName(){
        for(String key : NCPFConfigurationContainer.configOrder){
            if(configuration.configurations.containsKey(key)){
                NCPFConfiguration cfg = configuration.configurations.get(key);
                ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
                if(module!=null&&module.name!=null)return module.name;
            }
        }
        for(NCPFConfiguration cfg : configuration.configurations.values()){
            ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
            if(module!=null&&module.name!=null)return module.name;
        }
        return "Unknown Configuration";
    }
    @Override
    public String toString(){
        return getName();
    }
    public Configuration addAlternative(String alt){
        alternatives.add(alt);
        return this;
    }
    public void addAddon(Addon addon){
        addon.setReferences(configuration);
        addons.add(addon);
        for(Addon a : addons)addon.setReferences(a.configuration);
    }
    public List<NCPFConfiguration> getConfigurations(String name){
        List<NCPFConfiguration> lst = new ArrayList<>();
        if(configuration.configurations.containsKey(name))lst.add(configuration.configurations.get(name));
        for(Addon a : addons){
            if(a.configuration.configurations.containsKey(name))lst.add(a.configuration.configurations.get(name));
        }
        return lst;
    }
}