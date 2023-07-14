package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFPlacementRule extends DefinedNCPFObject{
    public RuleType rule;
    public NCPFElementReference target;//block or module reference
    public int min;
    public int max;
    public List<NCPFPlacementRule> rules = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        rule = RuleType.match(ncpf.getString("type"));
        if(rule.hasQuantity){
            min = ncpf.getInteger("min");
            max = ncpf.getInteger("max");
        }
        if(rule.hasSubRules){
            ncpf.getDefinedNCPFList("rules", NCPFPlacementRule::new);
        }else{
            target = ncpf.getDefinedNCPFObject("block", NCPFElementReference::new);
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("type", rule.name);
        if(rule.hasQuantity){
            ncpf.setInteger("min", min);
            ncpf.setInteger("max", max);
        }
        if(rule.hasSubRules){
            ncpf.setDefinedNCPFList("rules", rules);
        }else{
            ncpf.setDefinedNCPFObject("block", target);
        }
    }
    public enum RuleType{
        BETWEEN("between", true, false),
        AXIAL("axial", true, false),
        VERTEX("vertex", false, false),
        EDGE("edge", false, false),
        OR("or", false, true),
        AND("and", false, true);
        private static RuleType match(String string){
            for(RuleType rule : values())if(rule.name.equals(string))return rule;
            return null;
        }
        private final String name;
        private final boolean hasQuantity;
        private final boolean hasSubRules;
        private RuleType(String name, boolean hasQuantity, boolean hasSubRules){
            this.name = name;
            this.hasQuantity = hasQuantity;
            this.hasSubRules = hasSubRules;
        }
    }
}