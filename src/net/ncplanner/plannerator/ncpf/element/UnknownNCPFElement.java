package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class UnknownNCPFElement extends NCPFElementDefinition{
    public NCPFObject ncpf;
    public UnknownNCPFElement(){
        super(null);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        this.ncpf = new NCPFObject();
        this.ncpf.putAll(ncpf);
        this.ncpf.remove("modules");//don't load module data
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.putAll(this.ncpf);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        return false;//always make duplicates
    }
    @Override
    public String getName(){
        return null;
    }
}