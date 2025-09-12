package net.ncplanner.plannerator.planner.ncpf.module.configuration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class ConfigurationMetadataModule extends NCPFModule{
    public String name;
    public String version;
    public ConfigurationMetadataModule(){
        super("plannerator:configuration_metadata");
    }
    @Override
    public void conglomerate(NCPFModule addon){
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        version = ncpf.getString("version");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        ncpf.setString("version", version);
    }
}
