package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class CoilModule extends BlockRulesModule{
    public float efficiency;
    public CoilModule(){
        super("nuclearcraft:overhaul_turbine:coil");
        addFloat("efficiency", () -> efficiency, (v) -> efficiency = v, "Efficiency");
    }
    @Override
    public String getFunctionName(){
        return "Coil";
    }
    @Override
    public String getStatsTooltip(){
        return "Efficiency: "+efficiency;
    }
}
