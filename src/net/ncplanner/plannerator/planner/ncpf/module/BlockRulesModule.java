package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class BlockRulesModule extends BlockFunctionModule implements ElementStatsModule{
    public List<NCPFPlacementRule> rules = new ArrayList<>();
    public BlockRulesModule(String name){
        super(name);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        rules = ncpf.getDefinedNCPFList("rules", NCPFPlacementRule::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("rules", rules);
        super.convertToObject(ncpf);
    }
    public String getStatsTooltip(){
        return "";
    }
    @Override
    public String getTooltip(){
        String tip = getStatsTooltip();
        for(NCPFPlacementRule rule : rules){
            tip+="\nRequires "+rule.toTooltipString();
        }
        return tip;
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        rules.forEach((rule) -> rule.setReferences(lst));
    }
}