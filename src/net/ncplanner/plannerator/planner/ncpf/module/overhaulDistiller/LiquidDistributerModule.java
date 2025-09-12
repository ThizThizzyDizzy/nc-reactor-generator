package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class LiquidDistributerModule extends BlockFunctionModule{
    public LiquidDistributerModule(){
        super("nuclearcraft:overhaul_distiller:liquid_distributer");
    }
    @Override
    public String getFunctionName(){
        return "Liquid Distributer";
    }
}
