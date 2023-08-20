package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Edge;
import net.ncplanner.plannerator.multiblock.Vertex;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule.RuleType;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
public class CompiledPlacementRule{
    private static final String air = new AirModule().name;
    private final String casing;
    public final RuleType ruleType;
    public final String blockType;
    public final int block;
    public final int min;
    public final int max;
    public CompiledPlacementRule[] rules;
    public CompiledPlacementRule(RuleType ruleType, String blockType, int block, int min, int max, Supplier<NCPFModule> casing){
        this.ruleType = ruleType;
        this.blockType = blockType;
        this.block = block;
        this.min = min;
        this.max = max;
        this.casing = casing.get().name;
    }
    public static CompiledPlacementRule compile(NCPFPlacementRule rule, ArrayList<NCPFElement> blocks, Supplier<NCPFModule> casing){
        String blockType = null;
        int block = -1;
        if(!rule.rule.hasSubRules){
            if(rule.target.definition instanceof NCPFModuleElement){
                NCPFModuleElement module = (NCPFModuleElement)rule.target.definition;
                blockType = module.name;
            }else{
                int idx = -1;
                NCPFElementReference target = rule.target;
                for(int i = 0; i<blocks.size(); i++){
                    if(target.definition.matches(blocks.get(i).definition))idx = i;
                }
                block = idx;
            }
        }
        CompiledPlacementRule compiled = new CompiledPlacementRule(rule.rule, blockType, block, rule.min, rule.max, casing);
        compiled.rules = new CompiledPlacementRule[rule.rules.size()];
        for(int i = 0; i<rule.rules.size(); i++){
            NCPFPlacementRule rul = rule.rules.get(i);
            compiled.rules[i] = compile(rul, blocks, casing);
        }
        return compiled;
    }
    public boolean isAirMatch() {
        return blockType != null && blockType.equals(air);
    }
    private boolean blockMatches(int b, String type){
        if(blockType==null)return b==block;
        else if(blockType.equals(air))return b==-1||air.equals(type);
        else return blockType.equals(type);
    }
    public boolean isValid(int[] adjacents, int[] active, String[] types){
        int num = 0;
        boolean isAirMatch = isAirMatch();
        switch (ruleType) {
            case BETWEEN:
                if (isAirMatch) {
                    for(int i : adjacents){
                        if(i==-1)num++;
                    }
                } else {
                    for(int i = 0; i<6; i++){
                        int b = adjacents[i];
                        if(b>=0&&active[i]<1)continue;
                        if (blockMatches(b, type(b, types))) num++;
                    }
                }
                return num >= min && num <= max;
            case AXIAL:
                for(int axis = 0; axis<3; axis++){
                    int b1 = adjacents[axis];
                    if(b1>=0&&active[axis]<1)continue;
                    int b2 = adjacents[axis+3];
                    if(b2>=0&&active[axis+3]<1)continue;
                    if (isAirMatch) {
                        if (b1 == -1 && b2 == -1) num++;
                    } else {
                        if (b1 == -1 || b2 == -1) continue;
                        if (blockMatches(b1, type(b1, types)) && blockMatches(b2, type(b2, types))) num++;
                    }
                }
                return num >= min && num <= max;
            case VERTEX:
            case EDGE:
                boolean[] dirs = new boolean[6];
                for(int d = 0; d<adjacents.length; d++){
                    int b = adjacents[d];
                    if(b>=0&&active[d]<1)continue;;
                    if (isAirMatch) {
                        if (b == -1) dirs[d] = true;
                    } else {
                        if(b==-1)continue;
                        if (blockMatches(b, type(b, types))) dirs[d] = true;
                    }
                }
                if (ruleType == RuleType.VERTEX) {
                    outer: for (Vertex e : Vertex.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                } else if (ruleType == RuleType.EDGE) {
                    outer: for (Edge e : Edge.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                }
                return false;
            case AND:
                for (CompiledPlacementRule rule : rules) {
                    if (!rule.isValid(adjacents, active, types)) return false;
                }
                return true;
            case OR:
                for (CompiledPlacementRule rule : rules) {
                    if (rule.isValid(adjacents, active, types)) return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: " + ruleType);
    }
    private String type(int b, String[] types){
        if(b==-1)return air;
        if(b==-2)return casing;
        return types[b];
    }
}