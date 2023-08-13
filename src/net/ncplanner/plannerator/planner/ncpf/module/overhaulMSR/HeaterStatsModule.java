package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipeStatsModule;
public class HeaterStatsModule extends NCPFRecipeStatsModule{
    public int cooling;
    public HeaterStatsModule(){
        super("nuclearcraft:overhaul_msr:heater_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        cooling = ncpf.getInteger("cooling");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
    }
    @Override
    public String getTooltip(){
        return "Heater Cooling: "+cooling+"\n";
    }
    @Override
    public void conglomerate(NCPFModule addon){
        HeaterStatsModule stats = (HeaterStatsModule)addon;
        cooling = stats.cooling;
    }
}