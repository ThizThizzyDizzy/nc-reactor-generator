package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Edge;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Vertex;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
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
            rules = ncpf.getDefinedNCPFList("rules", NCPFPlacementRule::new);
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
    public boolean containsTarget(NCPFElementDefinition definition){
        if(rule.hasSubRules){
            for(NCPFPlacementRule rule : rules){
                if(rule.containsTarget(definition))return true;
            }
            return false;
        }else{
            return target.definition.matches(definition);
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
        public final boolean hasSubRules;
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
    private String getTargetName(){
        return target.definition.getName();
    }
    public String toTooltipString(){
        switch (rule) {
            case BETWEEN:
                if (max == 6) return "At least " + min + " " + getTargetName();
                if (min == max) return "Exactly " + min + " " + getTargetName();
                return "Between " + min + " and " + max + " " + getTargetName();
            case AXIAL:
                if (max == 3) return "At least " + min + " Axial pairs of " + getTargetName();
                if (min == max) return "Exactly " + min + " Axial pairs of " + getTargetName();
                return "Between " + min + " and " + max + " Axial pairs of " + getTargetName();
            case VERTEX:
                return "Three " + getTargetName() + " at the same vertex";
            case EDGE:
                return "Two " + getTargetName() + " at the same edge";
            case AND:
                StringBuilder s = new StringBuilder();
                for (NCPFPlacementRule rule : rules) {
                    s.append(" AND ").append(rule.toString());
                }
                return (s.length() == 0) ? s.toString() : StringUtil.substring(s, 5);
            case OR:
                s = new StringBuilder();
                for (NCPFPlacementRule rule : rules) {
                    s.append(" OR ").append(rule.toString());
                }
                return (s.length() == 0) ? s.toString() : StringUtil.substring(s, 4);
        }
        return "Unknown Rule";
    }
    private boolean isAirMatch() {
        return target!=null&&target.definition.matches(new NCPFModuleReference(AirModule::new).definition);
    }
    private <T extends Block> boolean blockMatches(Block block, Multiblock<T> reactor) {
        if(target.definition instanceof NCPFModuleElement){
            NCPFModuleReference moduleReference = target.copyTo(NCPFModuleReference::new);
            moduleReference.setReferences(null);
            if(moduleReference.definition.matches(new NCPFModuleReference(AirModule::new).definition))return block==null;
            return block!=null&&block.getTemplate().asElement().hasModule(moduleReference.module);
        }else{
            return block!=null&&block.getTemplate().asElement().definition.matches(target.definition);
        }
    }
    public <T extends Block> boolean isValid(Block block, Multiblock<T> reactor) {
        int num = 0;
        boolean isAirMatch = isAirMatch();
        switch (rule) {
            case BETWEEN:
                if (isAirMatch) {
                    num = 6 - block.getAdjacent(reactor).size();
                } else {
                    for (Block b : block.getActiveAdjacent(reactor)) {
                        if (blockMatches(b, reactor)) num++;
                    }
                }
                return num >= min && num <= max;
            case AXIAL:
                for(Axis axis : Axis.axes){
                    if(!reactor.contains(block.x - axis.x, block.y - axis.y, block.z - axis.z))continue;
                    if(!reactor.contains(block.x + axis.x, block.y + axis.y, block.z + axis.z))continue;
                    Block b1 = reactor.getBlock(block.x - axis.x, block.y - axis.y, block.z - axis.z);
                    Block b2 = reactor.getBlock(block.x + axis.x, block.y + axis.y, block.z + axis.z);
                    if (isAirMatch) {
                        if (b1 == null && b2 == null) num++;
                    } else {
                        if (b1 == null || b2 == null) continue;
                        if (!b1.isActive() || !b2.isActive()) continue;
                        if (blockMatches(b1, reactor) && blockMatches(b2, reactor)) num++;
                    }
                }
                return num >= min && num <= max;
            case VERTEX:
            case EDGE:
                boolean[] dirs = new boolean[Direction.values().length];
                for (Direction d : Direction.values()) {
                    if(!reactor.contains(block.x + d.x, block.y + d.y, block.z + d.z))continue;
                    Block b = reactor.getBlock(block.x + d.x, block.y + d.y, block.z + d.z);
                    if (isAirMatch) {
                        if (b == null) dirs[d.ordinal()] = true;
                    } else {
                        if(b==null)continue;
                        if (b.isActive() && blockMatches(b, reactor)) dirs[d.ordinal()] = true;
                    }
                }
                if (rule == RuleType.VERTEX) {
                    outer: for (Vertex e : Vertex.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                } else if (rule == RuleType.EDGE) {
                    outer: for (Edge e : Edge.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                }
                return false;
            case AND:
                for (NCPFPlacementRule rule : rules) {
                    if (!rule.isValid(block, reactor)) return false;
                }
                return true;
            case OR:
                for (NCPFPlacementRule rule : rules) {
                    if (rule.isValid(block, reactor)) return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: " + rule);
    }
}