package net.ncplanner.plannerator.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class UnknownNCPFConfiguration extends NCPFConfiguration{
    public NCPFObject ncpf;
    public UnknownNCPFConfiguration(){
        super(null);
    }
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
    @Override
    public List<NCPFElement>[] getElements(){
        return new List[0];
    }
    @Override
    public void makePartial(List<Design> designs){}
}