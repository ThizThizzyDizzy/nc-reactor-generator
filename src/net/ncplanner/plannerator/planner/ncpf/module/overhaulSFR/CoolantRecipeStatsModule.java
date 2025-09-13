package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFStatsModule;
@RegisterWith(module = OverhaulModule.class)
public class CoolantRecipeStatsModule extends NCPFStatsModule{
    public int heat;
    public CoolantRecipeStatsModule(){
        super("nuclearcraft:overhaul_sfr:coolant_recipe_stats");
        addInteger("heat", () -> heat, (v) -> heat = v, "Heat");
    }
    @Override
    public String getFriendlyName(){
        return "Coolant Recipe Stats";
    }
}
