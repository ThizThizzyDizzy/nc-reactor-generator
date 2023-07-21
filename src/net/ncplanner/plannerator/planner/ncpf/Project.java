package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFAddon;
import net.ncplanner.plannerator.ncpf.NCPFDesign;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
public class Project extends NCPFFile{
    public MetadataModule metadata = new MetadataModule();
    public List<Addon> addons = new ArrayList<>();
    public List<Design> designs = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        addons = copyList(super.addons, Addon::new);
        designs = new ArrayList<>();
        for(NCPFDesign d : super.designs){
            designs.add(d.copyTo(()->Design.registeredDesigns.getOrDefault(d.definition.type, Design::new).apply(this)));
        }
        metadata = getModule(MetadataModule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        setModule(metadata);
        super.addons = copyList(addons, NCPFAddon::new);
        super.convertToObject(ncpf);
        copyList(designs, super.designs, ()->new NCPFDesign(this));
        super.convertToObject(ncpf);
    }
    @Override
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        return conglomeration.getConfiguration(config);
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
}