package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class StatorModule extends BlockFunctionModule implements ElementStatsModule{
    public float expansion;
    public StatorModule(){
        super("nuclearcraft:overhaul_turbine:stator");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        expansion = ncpf.getFloat("expansion");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("expansion", expansion);
    }
    @Override
    public String getFunctionName(){
        return "Stator";
    }
    @Override
    public String getTooltip(){
        return "Expansion: "+expansion;
    }
}