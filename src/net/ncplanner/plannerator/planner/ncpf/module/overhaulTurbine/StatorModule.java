package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class StatorModule extends BlockFunctionModule implements ElementStatsModule{
    public float expansion;
    public StatorModule(){
        super("nuclearcraft:overhaul_turbine:stator");
        addFloat("expansion", ()->expansion, (v)->expansion = v, "Expansion");
    }
    @Override
    public String getFunctionName(){
        return "Stator";
    }
}