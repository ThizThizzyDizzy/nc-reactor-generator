package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipeStatsModule;
public class FuelStatsModule extends NCPFRecipeStatsModule{
    public float efficiency;
    public int heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public FuelStatsModule(){
        super("nuclearcraft:overhaul_msr:fuel_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        heat = ncpf.getInteger("heat");
        time = ncpf.getInteger("time");
        criticality = ncpf.getInteger("criticality");
        selfPriming = ncpf.getBoolean("self_priming");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setInteger("heat", heat);
        ncpf.setInteger("time", time);
        ncpf.setInteger("criticality", criticality);
        ncpf.setBoolean("self_priming", selfPriming);
    }
    @Override
    public String getTooltip(){
        String ttp = "";
        ttp+="Efficiency: "+efficiency+"\n";
        ttp+="Heat: "+heat+"\n";
        ttp+="Time: "+time+"\n";
        ttp+="Criticality: "+criticality+"\n";
        if(selfPriming)ttp+="Self-Priming\n";
        return ttp;
    }
    @Override
    public void conglomerate(NCPFModule addon){
        FuelStatsModule stats = (FuelStatsModule)addon;
        efficiency = stats.efficiency;
        heat = stats.heat;
        time = stats.time;
        criticality = stats.criticality;
        selfPriming = stats.selfPriming;
    }
}