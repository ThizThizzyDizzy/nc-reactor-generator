package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class RecipeStatsModule extends NCPFStatsModule{
    public double power;
    public double coefficient;
    public NCPFElementReference output;
    public RecipeStatsModule(){
        super("nuclearcraft:overhaul_turbine:recipe_stats");
        addDouble("coefficient", () -> coefficient, (v) -> coefficient = v, "Expansion Coefficient");
        addDouble("power", () -> power, (v) -> power = v, "Energy Density (RF/mb)");
        addReference("output", () -> output, (elem) -> output = new NCPFElementReference(elem), NCPFElementReference::new, (r) -> output = r, "Output Fluid");
    }
    @Override
    public String getFriendlyName(){
        return "Recipe Stats";
    }
}
