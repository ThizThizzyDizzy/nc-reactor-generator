package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ConnectorModule extends BlockFunctionModule{
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
}