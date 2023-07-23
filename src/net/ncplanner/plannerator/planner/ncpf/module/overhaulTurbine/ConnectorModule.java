package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class ConnectorModule extends BlockFunctionModule implements ElementStatsModule{
    public List<PlacementRule> rules = new ArrayList<>();
    public ConnectorModule(){
        super("nuclearcraft:overhaul_turbine:connector");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        rules = ncpf.getDefinedNCPFList("rules", PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("rules", rules);
    }
    @Override
    public String getFunctionName(){
        return "Connector";
    }
    @Override
    public String getTooltip(){
        String tip = "";
        for(PlacementRule rule : rules){
            tip+="\nRequires "+rule.toTooltipString();
        }
        return tip;
    }
}