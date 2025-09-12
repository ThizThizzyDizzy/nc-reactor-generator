package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
@RegisterWith(module = OverhaulModule.class)
public class PowerPortModule extends BlockFunctionModule{
    public PowerPortModule(){
        super("nuclearcraft:overhaul_distiller:power_port");
    }
    @Override
    public String getFunctionName(){
        return "Power Port";
    }
}
