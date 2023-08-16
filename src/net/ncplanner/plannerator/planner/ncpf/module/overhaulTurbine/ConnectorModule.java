package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
public class ConnectorModule extends BlockRulesModule{
    public ConnectorModule(){
        super("nuclearcraft:overhaul_turbine:connector");
    }
    @Override
    public String getFunctionName(){
        return "Connector";
    }
}