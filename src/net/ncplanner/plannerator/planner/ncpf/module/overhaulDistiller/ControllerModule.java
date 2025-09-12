package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ControllerModule extends BlockFunctionModule{
    public ControllerModule(){
        super("nuclearcraft:overhaul_distiller:controller");
    }
    @Override
    public String getFunctionName(){
        return "Controller";
    }
}
