package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
@Deprecated
public class UnknownNCPFDesign extends NCPFDesignDefinition{
    public NCPFList<Integer> design = new NCPFList<>();
    public NCPFObject ncpf;
    public UnknownNCPFDesign(){
        super(null);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        this.design.clear();
        if(ncpf.getNCPFList("design")!=null)this.design.addAll(ncpf.getNCPFList("design"));
        this.ncpf = new NCPFObject();
        this.ncpf.putAll(ncpf);
        this.ncpf.remove("modules");//don't load module data
        // Retain whether the opaque document originally had a design array.
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFList<Integer> design = new NCPFList<>();
        design.addAll(this.design);
        ncpf.putAll(this.ncpf);
        if(this.ncpf.containsKey("design")||!design.isEmpty())ncpf.put("design", design);
    }
}