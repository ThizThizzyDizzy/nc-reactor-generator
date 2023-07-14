package net.ncplanner.plannerator.planner.ncpf;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.MetadataModule;
public class Project extends NCPFFile{
    public MetadataModule metadata = new MetadataModule();
    public ArrayList<Design> designs = new ArrayList<>();
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
    @Override
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        return conglomeration.getConfiguration(config);
    }
}