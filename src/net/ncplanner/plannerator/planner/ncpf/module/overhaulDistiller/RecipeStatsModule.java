package net.ncplanner.plannerator.planner.ncpf.module.overhaulDistiller;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
@RegisterWith(module = OverhaulModule.class)
public class RecipeStatsModule extends NCPFStatsModule{
    public double timeMult = 1;
    public double powerMult = 1;
    public int sieveTrayCount;
    public RecipeStatsModule(){
        super("nuclearcraft:overhaul_distiller:recipe_stats");
        addDouble("time", () -> timeMult, (v) -> timeMult = v, "Time Multiplier");
        addDouble("power", () -> powerMult, (v) -> powerMult = v, "Power Multiplier");
        addInteger("sieve_trays", () -> sieveTrayCount, (v) -> sieveTrayCount = v==null?0:v, "Required Sieve Trays", "Minimum number of sieve tray layers needed for this recipe");
    }
    @Override
    public String getFriendlyName(){
        return "Recipe Stats";
    }
}
