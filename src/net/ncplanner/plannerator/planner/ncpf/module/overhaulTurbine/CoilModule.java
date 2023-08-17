package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class CoilModule extends BlockRulesModule{
    public float efficiency;
    public CoilModule(){
        super("nuclearcraft:overhaul_turbine:coil");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
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