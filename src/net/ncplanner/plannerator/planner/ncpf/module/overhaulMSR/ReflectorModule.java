package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ReflectorModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public float reflectivity;
    public ReflectorModule(){
        super("nuclearcraft:overhaul_msr:reflector");
        addFloat("efficiency", ()->efficiency, (v)->efficiency = v, "Efficiency");
        addFloat("reflectivity", ()->reflectivity, (v)->reflectivity = v, "Reflectivity");
    }
    @Override
    public String getFunctionName(){
        return "Reflector";
    }
}