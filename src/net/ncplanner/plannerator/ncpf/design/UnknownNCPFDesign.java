package net.ncplanner.plannerator.ncpf.design;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class UnknownNCPFDesign extends NCPFDesignDefinition{
    public NCPFList<Integer> design = new NCPFList<>();
    public NCPFObject ncpf;
    public UnknownNCPFDesign(){
        super(null);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        this.design.addAll(ncpf.getNCPFList("design"));
        this.ncpf = new NCPFObject();
        this.ncpf.putAll(ncpf);
        this.ncpf.remove("modules");//don't load module data
        this.ncpf.remove("design");//don't load design data
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        NCPFList<Integer> design = new NCPFList<>();
        design.addAll(this.design);
        ncpf.put("design", design);
        ncpf.putAll(this.ncpf);
    }
    @Override
    public Multiblock toMultiblock(Design d){
        return null;
    }
}