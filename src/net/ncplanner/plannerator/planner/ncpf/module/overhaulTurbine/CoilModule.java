package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoilModule extends BlockFunctionModule{
    public float efficiency;
    public List<PlacementRule> rules = new ArrayList<>();
    public CoilModule(){
        super("nuclearcraft:overhaul_turbine:coil");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        rules = ncpf.getDefinedNCPFList("rules", PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setDefinedNCPFList("rules", rules);
    }
}