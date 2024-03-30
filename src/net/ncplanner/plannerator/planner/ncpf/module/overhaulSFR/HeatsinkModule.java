package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class HeatsinkModule extends BlockRulesModule{
    public int cooling;
    public HeatsinkModule(){
        super("nuclearcraft:overhaul_sfr:heat_sink");
        addInteger("cooling", ()->cooling, (v)->cooling = v, "Cooling");
    }
    @Override
    public String getFunctionName(){
        return "Heatsink";
    }
    @Override
    public String getStatsTooltip(){
        return "Cooling: "+cooling+"H/t";
    }
}