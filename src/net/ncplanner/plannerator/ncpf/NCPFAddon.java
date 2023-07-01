package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFAddon extends DefinedNCPFModularObject{
    public NCPFConfigurationContainer configuration;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        configuration = ncpf.getDefinedNCPFObject("configuration", NCPFConfigurationContainer::new);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("configuration", configuration);
        super.convertToObject(ncpf);
    }
}