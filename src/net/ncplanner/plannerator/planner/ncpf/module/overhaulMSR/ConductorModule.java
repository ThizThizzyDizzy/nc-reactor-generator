package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ConductorModule extends BlockFunctionModule{
    public ConductorModule(){
        super("nuclearcraft:overhaul_msr:conductor");
    }
    @Override
    public String getFunctionName(){
        return "Conductor";
    }
}
