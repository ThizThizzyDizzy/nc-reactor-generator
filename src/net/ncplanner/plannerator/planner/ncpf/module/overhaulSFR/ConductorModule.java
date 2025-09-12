package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ConductorModule extends BlockFunctionModule{
    public ConductorModule(){
        super("nuclearcraft:overhaul_sfr:conductor");
    }
    @Override
    public String getFunctionName(){
        return "Conductor";
    }
}
