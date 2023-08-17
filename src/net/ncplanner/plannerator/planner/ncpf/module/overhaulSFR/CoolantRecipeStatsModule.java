package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public float outputRatio;
    public CoolantRecipeStatsModule(){
        super("nuclearcraft:overhaul_sfr:coolant_recipe_stats");
        addInteger("heat", ()->heat, (v)->heat = v, "Heat");
        addFloat("output_ratio", ()->outputRatio, (v)->outputRatio = v, "Output Ratio");
    }
}