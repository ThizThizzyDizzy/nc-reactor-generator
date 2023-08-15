package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.ConglomerationError;
import net.ncplanner.plannerator.ncpf.NCPFAddon;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFDesign;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
public class Project extends NCPFFile{
    public MetadataModule metadata = new MetadataModule();
    public List<Addon> addons = new ArrayList<>();
    public List<Design> designs = new ArrayList<>();
    @Override
    public void postConvertFromObject(NCPFObject ncpf){
        addons = copyList(super.addons, Addon::new);
        super.postConvertFromObject(ncpf);
        designs = new ArrayList<>();
        for(NCPFDesign d : super.designs){
            designs.add(d.copyTo(()->Design.registeredDesigns.getOrDefault(d.definition.type, Design::new).apply(isConfigEmpty()?Core.project:this)));
        }
        metadata = getModule(MetadataModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(metadata);
        super.addons = copyList(addons, NCPFAddon::new);
        super.convertToObject(ncpf);
        super.designs = copyList(designs, ()->new NCPFDesign(isConfigEmpty()?Core.project:this));
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(){
        configuration.setReferences();
        conglomeration = configuration.copyTo(NCPFConfigurationContainer::new);
        for(Addon addon : addons){
            try{
                conglomeration.conglomerate(addon.configuration);
            }catch(ConglomerationError err){
                throw new ConglomerationError("Failed to conglomerate addon "+addon.getName()+"!", err);
            }
        }
        conglomeration.setReferences();
    }
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        return conglomeration.getConfiguration(config);
    }
    public <T extends NCPFConfiguration> void withConfiguration(Supplier<T> config, Consumer<T> func){
        conglomeration.withConfiguration(config, func);
    }
    public String getCrashReportData(){
        String s = "Configurations: "+configuration.configurations.size()+"\n";
        for(NCPFConfiguration config : configuration.configurations.values()){
            s+=config.name+" "+config.getModule(ConfigurationMetadataModule::new).version+"\n";
        }
        s+="Addons: "+addons.size()+"\n";
        for(Addon addon : addons){
            s+="- "+addon.configuration.configurations.size()+" configurations\n";
            for(NCPFConfiguration config : addon.configuration.configurations.values()){
                s+="- "+config.name+" "+config.getModule(ConfigurationMetadataModule::new).version+"\n";
            }
        }
        for(Addon a : addons)s+="- "+a.toString()+"\n";
        return s;
    }
    public void makePartial(){
        FOR:for(Iterator<String> it = conglomeration.configurations.keySet().iterator(); it.hasNext();){
            String key = it.next();
            for(Design d : designs){
                if(d.definition.type.equals(key))continue FOR;
            }
            it.remove();
        }
        conglomeration.makePartial(designs);
        configuration = conglomeration;
        addons.clear();
    }
    public String getConfigName(){
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
}