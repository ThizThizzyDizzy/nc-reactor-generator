package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class HeatsinkModule extends BlockRulesModule{
    public int cooling;
    public HeatsinkModule(){
        super("plannerator:fusion_test:heatsink");
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