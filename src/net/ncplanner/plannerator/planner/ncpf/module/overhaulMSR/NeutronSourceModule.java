package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class NeutronSourceModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public NeutronSourceModule(){
        super("nuclearcraft:overhaul_msr:neutron_source");
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
        return "Neutron Source";
    }
    @Override
    public String getTooltip(){
        return "Efficiency: "+efficiency;
    }
}