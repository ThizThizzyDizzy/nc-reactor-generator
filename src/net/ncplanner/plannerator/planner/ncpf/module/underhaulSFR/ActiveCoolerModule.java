package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ActiveCoolerModule extends BlockFunctionModule{
    public List<PlacementRule> rules = new ArrayList<>();
    public ActiveCoolerModule(){
        super("nuclearcraft:underhaul_sfr:active_cooler");
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