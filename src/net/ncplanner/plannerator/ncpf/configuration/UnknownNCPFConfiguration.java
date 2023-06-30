package net.ncplanner.plannerator.ncpf.configuration;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class UnknownNCPFConfiguration extends NCPFConfiguration{
    public NCPFObject ncpf;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        this.ncpf = ncpf;
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.putAll(this.ncpf);
        super.convertToObject(ncpf);
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject addon){
        ncpf.putAll(((UnknownNCPFConfiguration)addon).ncpf);
    }
}