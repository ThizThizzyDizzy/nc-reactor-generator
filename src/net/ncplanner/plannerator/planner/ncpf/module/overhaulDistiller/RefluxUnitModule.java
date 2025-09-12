package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RefluxUnitModule extends BlockFunctionModule{
    public RefluxUnitModule(){
        super("nuclearcraft:overhaul_distiller:reflux_unit");
    }
    @Override
    public String getFunctionName(){
        return "Reflux Unit";
    }
}
