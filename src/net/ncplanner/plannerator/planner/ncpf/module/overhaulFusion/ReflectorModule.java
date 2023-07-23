package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ReflectorModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public ReflectorModule(){
        super("plannerator:fusion_test:reflector");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
    }
    @Override
    public String getFunctionName(){
        return "Reflector";
    }
    @Override
    public String getTooltip(){
        return "Efficiency: "+efficiency;
    }
}