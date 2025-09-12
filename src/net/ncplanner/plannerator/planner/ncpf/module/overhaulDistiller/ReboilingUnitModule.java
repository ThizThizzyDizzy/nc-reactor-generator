package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ReboilingUnitModule extends BlockFunctionModule{
    public ReboilingUnitModule(){
        super("nuclearcraft:overhaul_distiller:reboiling_unit");
    }
    @Override
    public String getFunctionName(){
        return "Reboiling Unit";
    }
}
