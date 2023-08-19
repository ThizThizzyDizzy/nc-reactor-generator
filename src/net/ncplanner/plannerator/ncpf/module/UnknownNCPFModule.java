package net.ncplanner.plannerator.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class UnknownNCPFModule extends NCPFModule{
    public NCPFObject ncpf;
    public UnknownNCPFModule(){
        super(null);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        this.ncpf = ncpf;
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.putAll(this.ncpf);
    }
    @Override
    public void conglomerate(NCPFModule addon){
        ncpf.putAll(((UnknownNCPFModule)addon).ncpf);
    }
    @Override
    public String getFriendlyName(){
        return "Unknown Module";
    }
}