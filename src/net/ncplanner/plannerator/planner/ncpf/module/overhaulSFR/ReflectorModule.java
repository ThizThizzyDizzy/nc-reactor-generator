package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ReflectorModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public float reflectivity;
    public ReflectorModule(){
        super("nuclearcraft:overhaul_sfr:reflector");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        reflectivity = ncpf.getFloat("reflectivity");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setFloat("reflectivity", reflectivity);
    }
    @Override
    public String getFunctionName(){
        return "Reflector";
    }
    @Override
    public String getTooltip(){
        return "Efficiency: "+efficiency+"\n"
                + "Reflectivity: "+reflectivity;
    }
}