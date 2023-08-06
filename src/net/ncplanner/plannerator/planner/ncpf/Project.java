package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFAddon;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
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
    public Project(){}
    public Project(Project parentConfig){//used for hellrage loading
        configuration = parentConfig.configuration;
        addons = parentConfig.addons;
        conglomeration = parentConfig.conglomeration;
    }
    @Override
    public void postConvertFromObject(NCPFObject ncpf){
        super.postConvertFromObject(ncpf);
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
        super.designs = copyList(designs, ()->new NCPFDesign(this));
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(){
        conglomeration = new NCPFConfigurationContainer();
        conglomeration.conglomerate(configuration);
        for(Addon addon : addons){
            conglomeration.conglomerate(addon.configuration);
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
}