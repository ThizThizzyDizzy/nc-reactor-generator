package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class HeaterModule extends BlockFunctionModule{
    public List<PlacementRule> rules = new ArrayList<>();
    public HeaterModule(){
        super("nuclearcraft:overhaul_msr:heater");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        rules = ncpf.getDefinedNCPFList("rules", rules, PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("rules", rules);
    }
}