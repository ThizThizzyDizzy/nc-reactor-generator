package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
@RegisterWith(module = FusionTestModule.class)
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public float outputRatio = 1;
    public CoolantRecipeStatsModule(){
        super("plannerator:fusion_test:coolant_recipe_stats");
        addInteger("heat", () -> heat, (v) -> heat = v, "Heat");
        addFloat("output_ratio", () -> outputRatio, (v) -> outputRatio = v, "Output Ratio");
    }
    @Override
    public String getFriendlyName(){
        return "Coolant Recipe Stats";
    }
}
