package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class NeutronShieldModule extends BlockFunctionModule implements ElementStatsModule{
    public int heatPerFlux;
    public float efficiency;
    public BlockReference<BlockElement> closed;
    public NeutronShieldModule(){
        super("nuclearcraft:overhaul_msr:neutron_shield");
        addInteger("heat_per_flux", () -> heatPerFlux, (v) -> heatPerFlux = v, "Heat Per Flux");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
        addReference("closed", () -> closed, (v) -> closed = BlockReference.create((BlockElement)v), BlockReference<BlockElement>::new, (r) -> closed = r, "Closed");
    }
    @Override
    public void setLocalReferences(DefinedNCPFModularObject parentObject){
        if(closed!=null&&closed.block!=null){
            ((BlockElement)parentObject).toggled = closed.block;
            closed.block.unToggled = (BlockElement)parentObject;
        }
    }
    @Override
    public String getFunctionName(){
        return "Neutron Shield";
    }
}
