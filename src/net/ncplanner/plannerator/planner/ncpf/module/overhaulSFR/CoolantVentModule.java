package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolantVentModule extends BlockFunctionModule{
    public BlockReference output;
    public CoolantVentModule(){
        super("nuclearcraft:overhaul_sfr:coolant_vent");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        output = ncpf.getDefinedNCPFObject("output", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("output", output);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        output.setReferences(lst);
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(output.block!=null){
            ((Block)parentObject).toggled = output.block;
            output.block.unToggled = (Block)parentObject;
        }
    }
    @Override
    public String getFunctionName(){
        return "Coolant Vent";
    }
}