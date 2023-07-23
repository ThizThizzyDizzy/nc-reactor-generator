package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class BladeModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public float expansion;
    public BladeModule(){
        super("nuclearcraft:overhaul_turbine:blade");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        expansion = ncpf.getFloat("expansion");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setFloat("expansion", expansion);
    }
    @Override
    public String getFunctionName(){
        return "Blade";
    }
    @Override
    public String getTooltip(){
        return "Efficiency: "+efficiency+
                "\nExpansion: "+expansion;
    }
}