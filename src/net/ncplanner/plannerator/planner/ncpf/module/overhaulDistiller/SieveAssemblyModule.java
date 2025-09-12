package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
@RegisterWith(module = OverhaulModule.class)
public class SieveAssemblyModule extends BlockFunctionModule implements ElementStatsModule{
    public double efficiency;
    public SieveAssemblyModule(){
        super("nuclearcraft:overhaul_distiller:sieve_assembly");
        addDouble("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
    }
    @Override
    public String getFunctionName(){
        return "Sieve Assembly";
    }
}
