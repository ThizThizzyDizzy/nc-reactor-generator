package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class HeatsinkModule extends BlockFunctionModule{
    public int cooling;
    public List<PlacementRule> rules = new ArrayList<>();
    public HeatsinkModule(){
        super("plannerator:fusion_test:heatsink");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        cooling = ncpf.getInteger("cooling");
        rules = ncpf.getDefinedNCPFList("rules", rules, PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
    }
}