package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class ConnectorModule extends BlockRulesModule{
    public ConnectorModule(){
        super("nuclearcraft:overhaul_turbine:connector");
    }
    @Override
    public String getFunctionName(){
        return "Connector";
    }
}
