package net.ncplanner.plannerator.planner.ncpf;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFAddon;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
public class Addon extends NCPFAddon{
    public MetadataModule metadata = new MetadataModule();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(MetadataModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(metadata);
        super.convertToObject(ncpf);
    }
    public String getName(){//TODO name
        for(NCPFConfiguration cfg : configuration.configurations.values()){
            ConfigurationMetadataModule module = cfg.getModule(ConfigurationMetadataModule::new);
            if(module!=null)return module.name;
        }
        return metadata.metadata.getOrDefault("name", "Unknown Configuration");
    }
    public void setReferences(NCPFConfigurationContainer config){
        for(String key : this.configuration.configurations.keySet()){
            NCPFConfiguration other = config.configurations.get(key);
            if(other!=null){
                NCPFConfiguration thisOne = configuration.configurations.get(key);
                for(List<NCPFElement> otherElements : other.getElements()){
                    for(List<NCPFElement> elems : thisOne.getElements()){
                        for(NCPFElement elem : elems)elem.setReferences(otherElements);
                    }
                }
            }
        }
    }
}