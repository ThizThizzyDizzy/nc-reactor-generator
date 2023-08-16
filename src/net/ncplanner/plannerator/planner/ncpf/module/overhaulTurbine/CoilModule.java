package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class CoilModule extends BlockRulesModule{
    public float efficiency;
    public CoilModule(){
        super("nuclearcraft:overhaul_turbine:coil");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        efficiency = ncpf.getFloat("efficiency");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        super.convertToObject(ncpf);
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