package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class FuelStatsModule extends BlockFunctionModule implements ElementStatsModule{
    public float efficiency;
    public int heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public FuelStatsModule(){
        super("nuclearcraft:overhaul_sfr:fuel_stats");
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
}