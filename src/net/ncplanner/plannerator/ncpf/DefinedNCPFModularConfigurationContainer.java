package net.ncplanner.plannerator.ncpf;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class DefinedNCPFModularConfigurationContainer extends DefinedNCPFModularObject{
    public NCPFConfigurationContainer configuration = new NCPFConfigurationContainer();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        configuration = ncpf.getDefinedNCPFObject("configuration", NCPFConfigurationContainer::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("configuration", configuration);
        super.convertToObject(ncpf);
    }
    public <T extends NCPFConfiguration> T getConfiguration(Supplier<T> config){
        return configuration.getConfiguration(config);
    }
    public void setConfiguration(NCPFConfiguration config){
        configuration.setConfiguration(config);
    }
    public void setConfigurations(NCPFConfiguration... configs){
        for(NCPFConfiguration config : configs)setConfiguration(config);
    }
}