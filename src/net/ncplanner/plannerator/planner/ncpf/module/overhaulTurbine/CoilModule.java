package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class CoilModule extends BlockFunctionModule implements ElementStatsModule{
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
    @Override
    public String getFunctionName(){
        return "Coil";
    }
    @Override
    public String getTooltip(){
        String tip = "Efficiency: "+efficiency;
        for(PlacementRule rule : rules){
            tip+="\nRequires "+rule.toTooltipString();
        }
        return tip;
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        rules.forEach((rule) -> rule.setReferences(lst));
    }
}