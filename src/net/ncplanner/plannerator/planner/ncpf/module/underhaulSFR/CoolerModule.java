package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolerModule extends BlockFunctionModule{
    public int cooling;
    public List<PlacementRule> rules = new ArrayList<>();
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
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