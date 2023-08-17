package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public float outputRatio;
    public CoolantRecipeStatsModule(){
        super("plannerator:fusion_test:coolant_recipe_stats");
        addInteger("heat", ()->heat, (v)->heat = v, "Heat");
        addFloat("output_ratio", ()->outputRatio, (v)->outputRatio = v, "Output Ratio");
    }
}