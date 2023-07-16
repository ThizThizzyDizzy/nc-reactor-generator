package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.StringUtil;
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
    public <T extends NCPFPlacementRule> T parseNc(String str, Supplier<T> newRule, Function<String, Supplier<NCPFModule>> blockTypes, Function<String, NCPFElementReference> blocks){
        if (str.contains("||")) {
            this.rule = RuleType.OR;
            for (String sub : StringUtil.split(str, "\\|\\|")){
                this.rules.add(newRule.get().parseNc(sub.trim(), newRule, blockTypes, blocks));
            }
        } else if (str.contains("&&")) {
            this.rule = RuleType.AND;
            for (String sub : StringUtil.split(str, "&&")) {
                this.rules.add(newRule.get().parseNc(sub.trim(), newRule, blockTypes, blocks));
            }
        } else {
            if (str.startsWith("at least ")) str = str.substring("at least ".length());
            boolean exactly = str.startsWith("exactly");
            if (exactly) str = str.substring(7).trim();

            int amount = 0;
            if (str.startsWith("zero")) {
                amount = 0;
                str = str.substring(4).trim();
            } else if (str.startsWith("one")) {
                amount = 1;
                str = str.substring(3).trim();
            } else if (str.startsWith("two")) {
                amount = 2;
                str = str.substring(3).trim();
            } else if (str.startsWith("three")) {
                amount = 3;
                str = str.substring(5).trim();
            } else if (str.startsWith("four")) {
                amount = 4;
                str = str.substring(4).trim();
            } else if (str.startsWith("five")) {
                amount = 5;
                str = str.substring(4).trim();
            } else if (str.startsWith("six")) {
                amount = 6;
                str = str.substring(3).trim();
            }

            boolean axial = str.startsWith("axial");
            if (axial) str = str.substring(5).trim();

            if(str.startsWith("of any "))str = str.substring("of any ".length());
            Supplier<NCPFModule> type = blockTypes.apply(str);
            NCPFElementReference block = type==null?blocks.apply(str):null;

            if (type == null && block == null)
                throw new IllegalArgumentException("Failed to parse rule " + str + ": block is null!");
            if (exactly && axial) {
                this.rule = RuleType.AND;
                T rul1 = newRule.get();
                T rul2 = newRule.get();
                if (type != null) {
                    rul1.rule = RuleType.BETWEEN;
                    rul2.rule = RuleType.AXIAL;
                    rul1.target = new NCPFModuleReference(type);
                    rul2.target = new NCPFModuleReference(type);
                } else {
                    rul1.rule = RuleType.BETWEEN;
                    rul2.rule = RuleType.AXIAL;
                    rul1.target = rul2.target = block;
                }
                rul1.min = rul1.max = (byte) amount;
                rul2.min = rul2.max = (byte) (amount / 2);
                this.rules.add(rul1);
                this.rules.add(rul2);
            } else {
                int min = amount;
                int max = 6;
                if (exactly) max = min;

                if (type != null) {
                    this.rule = axial ? RuleType.AXIAL : RuleType.BETWEEN;
                    this.target = new NCPFModuleReference(type);
                } else {
                    this.rule = axial ? RuleType.AXIAL : RuleType.BETWEEN;
                    this.target = block;
                }
                if (axial) {
                    min /= 2;
                    max /= 2;
                }
                this.min = (byte) min;
                this.max = (byte) max;
            }
        }
        return (T)this;
    }
}