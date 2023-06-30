package net.ncplanner.plannerator.planner.ncpf.configuration;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
public class Configuration extends NCPFConfiguration{
    public ConfigurationMetadataModule metadata;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        metadata = getModule(ConfigurationMetadataModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(metadata);
        super.convertToObject(ncpf);
    }
}