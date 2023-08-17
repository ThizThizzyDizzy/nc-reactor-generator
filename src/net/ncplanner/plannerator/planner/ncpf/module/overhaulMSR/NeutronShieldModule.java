package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class NeutronShieldModule extends BlockFunctionModule implements ElementStatsModule{
    public int heatPerFlux;
    public float efficiency;
    public BlockReference closed;
    public NeutronShieldModule(){
        super("nuclearcraft:overhaul_msr:neutron_shield");
        addInteger("heat_per_flux", ()->heatPerFlux, (v)->heatPerFlux = v, "Heat Per Flux");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        closed = ncpf.getDefinedNCPFObject("closed", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("closed", closed);
        super.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        closed.setReferences(lst);
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(closed.block!=null){
            ((BlockElement)parentObject).toggled = closed.block;
            closed.block.unToggled = (BlockElement)parentObject;
        }
    }
    @Override
    public String getFunctionName(){
        return "Neutron Shield";
    }
}