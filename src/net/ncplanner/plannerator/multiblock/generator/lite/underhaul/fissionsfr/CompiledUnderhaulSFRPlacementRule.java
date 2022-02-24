package net.ncplanner.plannerator.multiblock.generator.lite.underhaul.fissionsfr;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Direction;
import net.ncplanner.plannerator.multiblock.Edge;
import net.ncplanner.plannerator.multiblock.Vertex;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import net.ncplanner.plannerator.multiblock.generator.lite.CompiledPlacementRule;
public class CompiledUnderhaulSFRPlacementRule extends CompiledPlacementRule<PlacementRule.BlockType, Block>{
    public CompiledUnderhaulSFRPlacementRule(AbstractPlacementRule.RuleType ruleType, boolean isSpecificBlock, PlacementRule.BlockType blockType, int block, byte min, byte max){
        super(ruleType, isSpecificBlock, blockType, block, min, max);
    }
    public static CompiledUnderhaulSFRPlacementRule compileUnderhaulSFR(AbstractPlacementRule<PlacementRule.BlockType, Block> rule, ArrayList<Block> blocks){
        CompiledUnderhaulSFRPlacementRule compiled = new CompiledUnderhaulSFRPlacementRule(rule.ruleType, rule.isSpecificBlock, rule.blockType, blocks.indexOf(rule.block), rule.min, rule.max);
        compiled.rules = new CompiledPlacementRule[rule.rules.size()];
        for(int i = 0; i<rule.rules.size(); i++){
            AbstractPlacementRule<PlacementRule.BlockType, Block> rul = rule.rules.get(i);
            compiled.rules[i] = compileUnderhaulSFR(rul, blocks);
        }
        return compiled;
    }
    private boolean blockMatches(int b, CompiledUnderhaulSFRConfiguration config){
        if (isSpecificBlock) return b==this.block;
        else if (blockType.isAir()) return b == -1;
        else return blockType.blockMatches(b, config);
    }
    public boolean isValid(int[] adjacents, int[] active, CompiledUnderhaulSFRConfiguration config){
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
                        if (blockMatches(b, config)) num++;
                    }
                }
                return num >= min && num <= max;
            case AXIAL:
                for(int axis = 0; axis<3; axis++){
                    int b1 = adjacents[axis];
                    if(b1>=0)b1-=active[axis]*10;
                    int b2 = adjacents[axis+3];
                    if(b2>=0)b2-=active[axis+3]*10;
                    if (isAirMatch) {
                        if (b1 == -1 && b2 == -1) num++;
                    } else {
                        if (b1 == -1 || b2 == -1) continue;
                        if (blockMatches(b1, config) && blockMatches(b2, config)) num++;
                    }
                }
                return num >= min && num <= max;
            case VERTEX:
            case EDGE:
                boolean[] dirs = new boolean[6];
                for(int d = 0; d<adjacents.length; d++){
                    int b = adjacents[d];
                    if(b>=0)b-=active[d]*10;
                    if (isAirMatch) {
                        if (b == -1) dirs[d] = true;
                    } else {
                        if(b==-1)continue;
                        if (blockMatches(b, config)) dirs[d] = true;
                    }
                }
                if (ruleType == AbstractPlacementRule.RuleType.VERTEX) {
                    outer: for (Vertex e : Vertex.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                } else if (ruleType == AbstractPlacementRule.RuleType.EDGE) {
                    outer: for (Edge e : Edge.values()) {
                        for (Direction d : e.directions) {
                            if (!dirs[d.ordinal()]) continue outer;
                        }
                        return true;
                    }
                }
                return false;
            case AND:
                for (CompiledPlacementRule<PlacementRule.BlockType, Block> rule : rules) {
                    CompiledUnderhaulSFRPlacementRule sfrrule = (CompiledUnderhaulSFRPlacementRule)rule;
                    if (!sfrrule.isValid(adjacents, active, config)) return false;
                }
                return true;
            case OR:
                for (CompiledPlacementRule<PlacementRule.BlockType, Block> rule : rules) {
                    CompiledUnderhaulSFRPlacementRule sfrrule = (CompiledUnderhaulSFRPlacementRule)rule;
                    if (sfrrule.isValid(adjacents, active, config)) return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: " + ruleType);
    }
}