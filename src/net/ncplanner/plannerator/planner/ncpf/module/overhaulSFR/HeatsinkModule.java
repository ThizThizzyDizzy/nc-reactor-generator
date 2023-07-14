package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class HeatsinkModule extends BlockFunctionModule{
    public int cooling;
    public List<PlacementRule> rules = new ArrayList<>();
    public HeatsinkModule(){
        super("nuclearcraft:overhaul_sfr:heat_sink");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        cooling = ncpf.getInteger("cooling");
        rules = ncpf.getDefinedNCPFList("rules", rules, PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
        ncpf.setDefinedNCPFList("rules", rules);
    }
}