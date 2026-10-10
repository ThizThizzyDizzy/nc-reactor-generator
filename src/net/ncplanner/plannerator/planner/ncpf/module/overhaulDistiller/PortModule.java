package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
@RegisterWith(module = OverhaulModule.class)
public class PortModule extends BlockFunctionModule{
    public PortModule(){
        super("nuclearcraft:overhaul_distiller:port");
    }
    @Override
    public String getFunctionName(){
        return "Port";
    }
}
