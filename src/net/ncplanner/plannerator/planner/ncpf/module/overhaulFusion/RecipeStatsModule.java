package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class RecipeStatsModule extends NCPFStatsModule{
    public float efficiency;
    public int heat;
    public int time;
    public float fluxiness;
    public RecipeStatsModule(){
        super("plannerator:fusion_test:recipe_stats");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addInteger("heat", ()->heat, (v)->heat = v, "Base Heat");
        addFloat("fluxiness", ()->fluxiness, (v)->fluxiness = v, "Fluxiness");
        addInteger("time", ()->time, (v)->time = v, "Base Time");
    }
    @Override
    public String getFriendlyName(){
        return "Recipe Stats";
    }
}