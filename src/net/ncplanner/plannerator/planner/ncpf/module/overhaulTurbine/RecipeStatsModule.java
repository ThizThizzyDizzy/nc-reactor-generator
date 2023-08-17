package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
public class RecipeStatsModule extends NCPFStatsModule{
    public double power;
    public double coefficient;
    public RecipeStatsModule(){
        super("nuclearcraft:overhaul_turbine:recipe_stats");
        addDouble("coefficient", ()->coefficient, (v)->coefficient = v, "Expansion Coefficient");
        addDouble("power", ()->power, (v)->power = v, "Energy Density (RF/mb)");
    }
}