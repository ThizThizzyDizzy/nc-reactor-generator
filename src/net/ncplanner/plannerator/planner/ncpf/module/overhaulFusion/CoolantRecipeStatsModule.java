package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
@RegisterWith(module = FusionTestModule.class)
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public CoolantRecipeStatsModule(){
        super("plannerator:fusion_test:coolant_recipe_stats");
        addInteger("heat", () -> heat, (v) -> heat = v, "Heat");
    }
    @Override
    public String getFriendlyName(){
        return "Coolant Recipe Stats";
    }
}
