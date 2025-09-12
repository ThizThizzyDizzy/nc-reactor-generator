package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class ReflectorModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public ReflectorModule(){
        super("plannerator:fusion_test:reflector");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
    }
    @Override
    public String getFunctionName(){
        return "Reflector";
    }
}
