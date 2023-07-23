package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.ElementStatsModule;
public class CoolerModule extends BlockFunctionModule implements ElementStatsModule{
    public int cooling;
    public List<PlacementRule> rules = new ArrayList<>();
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        cooling = ncpf.getInteger("cooling");
        rules = ncpf.getDefinedNCPFList("rules", PlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
        ncpf.setDefinedNCPFList("rules", rules);
    }
    @Override
    public String getFunctionName(){
        return "Cooler";
    }
    @Override
    public String getTooltip(){
        String tip = "Cooling: "+cooling;
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