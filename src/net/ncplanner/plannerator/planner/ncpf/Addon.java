package net.ncplanner.plannerator.planner.ncpf;
import net.ncplanner.plannerator.ncpf.NCPFAddon;
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
}