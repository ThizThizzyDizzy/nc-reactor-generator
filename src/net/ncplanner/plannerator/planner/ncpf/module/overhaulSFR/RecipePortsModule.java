package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class RecipePortsModule extends BlockFunctionModule{
    public BlockReference input;
    public BlockReference output;
    public RecipePortsModule(){
        super("nuclearcraft:overhaul_sfr:recipe_ports");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        input = ncpf.getDefinedNCPFObject("input", BlockReference::new);
        output = ncpf.getDefinedNCPFObject("output", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("input", input);
        ncpf.setDefinedNCPFObject("output", output);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        input.setReferences(lst);
        output.setReferences(lst);
    }
}