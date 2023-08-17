package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class BladeModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public float expansion;
    public BladeModule(){
        super("nuclearcraft:overhaul_turbine:blade");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addFloat("expansion", ()->expansion, (v)->expansion = v, "Expansion");
    }
    @Override
    public String getFunctionName(){
        return "Blade";
    }
}