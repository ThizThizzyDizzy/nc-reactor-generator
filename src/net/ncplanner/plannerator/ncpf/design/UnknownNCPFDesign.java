package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFFile;
public class UnknownNCPFDesign extends NCPFDesignDefinition{
    public NCPFList<Integer> design = new NCPFList<>();
    public NCPFObject ncpf;
    public UnknownNCPFDesign(){
        super(null);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf, NCPFFile file){
        this.design.addAll(ncpf.getNCPFList("design"));
        this.ncpf = new NCPFObject();
        this.ncpf.putAll(ncpf);
        this.ncpf.remove("modules");//don't load module data
        this.ncpf.remove("design");//don't load module data
    }
    @Override
    public void convertToObject(NCPFObject ncpf, NCPFFile file){
        NCPFList<Integer> design = new NCPFList<>();
        design.addAll(this.design);
        ncpf.put("design", design);
        ncpf.putAll(this.ncpf);
    }
}