package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class HeaterModule extends BlockRulesModule{
    public HeaterModule(){
        super("nuclearcraft:overhaul_msr:heater");
    }
    @Override
    public String getFunctionName(){
        return "Heater";
    }
}