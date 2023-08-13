package net.ncplanner.plannerator.planner.ncpf.design;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.ncpf.design.UnknownNCPFDesign;
import net.ncplanner.plannerator.planner.ncpf.Design;
public class UnknownDesign extends Design<UnknownNCPFDesign>{
    public UnknownDesign(NCPFFile file){
        super(file);
        definition = new UnknownNCPFDesign();
    }
}