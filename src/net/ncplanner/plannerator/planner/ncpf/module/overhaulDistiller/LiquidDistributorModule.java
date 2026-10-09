package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class LiquidDistributorModule extends BlockFunctionModule{
    public LiquidDistributorModule(){
        super("nuclearcraft:overhaul_distiller:liquid_distributor");
    }
    @Override
    public String getFunctionName(){
        return "Liquid Distributor";
    }
}
