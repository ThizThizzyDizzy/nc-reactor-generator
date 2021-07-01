package planner.file.reader;

import multiblock.configuration.AbstractPlacementRule;
import simplelibrary.config2.Config;

public class NCPF10Reader extends NCPF11Reader {
    protected byte getTargetVersion() {
        return (byte) 10;
    }
    protected int readRuleBlockIndex(Config config, String name) {
        return (byte) config.get(name);
    }
    protected AbstractPlacementRule.RuleType mapRuleType(AbstractPlacementRule<?, ?> rule, byte type) {
        switch (type) {
            case 0:
                return AbstractPlacementRule.RuleType.BETWEEN;
            case 1:
                return AbstractPlacementRule.RuleType.AXIAL;
            case 2:
                if (rule instanceof multiblock.configuration.overhaul.turbine.PlacementRule) return AbstractPlacementRule.RuleType.EDGE;
                else return AbstractPlacementRule.RuleType.VERTEX;
            case 3:
                return AbstractPlacementRule.RuleType.BETWEEN_GROUP;
            case 4:
                return AbstractPlacementRule.RuleType.AXIAL_GROUP;
            case 5:
                if (rule instanceof multiblock.configuration.overhaul.turbine.PlacementRule) return AbstractPlacementRule.RuleType.EDGE_GROUP;
                else return AbstractPlacementRule.RuleType.VERTEX_GROUP;
            case 6:
                return AbstractPlacementRule.RuleType.OR;
            case 7:
                return AbstractPlacementRule.RuleType.AND;
            default:
                throw new RuntimeException("Found rule with invalid type: "+type);
        }
    }
}