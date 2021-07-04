package planner.file.reader;

import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;

import java.util.HashMap;
import java.util.Iterator;

public class NCPF10Reader extends NCPF11Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 10;
    }

    protected enum LegacyRuleType {
        BETWEEN(AbstractPlacementRule.RuleType.BETWEEN),
        AXIAL(AbstractPlacementRule.RuleType.AXIAL),
        EDGE(AbstractPlacementRule.RuleType.EDGE),
        VERTEX(AbstractPlacementRule.RuleType.VERTEX),
        BETWEEN_GROUP(AbstractPlacementRule.RuleType.BETWEEN),
        AXIAL_GROUP(AbstractPlacementRule.RuleType.AXIAL),
        EDGE_GROUP(AbstractPlacementRule.RuleType.EDGE),
        VERTEX_GROUP(AbstractPlacementRule.RuleType.VERTEX),
        OR(AbstractPlacementRule.RuleType.OR),
        AND(AbstractPlacementRule.RuleType.AND);

        public final AbstractPlacementRule.RuleType currentRule;

        LegacyRuleType(AbstractPlacementRule.RuleType currentRule) {
            this.currentRule = currentRule;
        }
    }

    protected LegacyRuleType mapRuleTypeNcpf10(AbstractPlacementRule<?, ?> rule, byte type) {
        switch (type) {
            case 0:
                return LegacyRuleType.BETWEEN;
            case 1:
                return LegacyRuleType.AXIAL;
            case 2:
                if (rule instanceof multiblock.configuration.overhaul.turbine.PlacementRule) return LegacyRuleType.EDGE;
                else return LegacyRuleType.VERTEX;
            case 3:
                return LegacyRuleType.BETWEEN_GROUP;
            case 4:
                return LegacyRuleType.AXIAL_GROUP;
            case 5:
                if (rule instanceof multiblock.configuration.overhaul.turbine.PlacementRule) return LegacyRuleType.EDGE_GROUP;
                else return LegacyRuleType.VERTEX_GROUP;
            case 6:
                return LegacyRuleType.OR;
            case 7:
                return LegacyRuleType.AND;
            default:
                throw new RuntimeException("Found rule with invalid type: "+type);
        }
    }

    protected <Rule extends AbstractPlacementRule<BlockType, Template>,
            BlockType extends IBlockType,
            Template extends IBlockTemplate> Rule readGenericRule(HashMap<Rule, Integer> postMap, Rule rule, Config ruleCfg){
        byte type = ruleCfg.get("type");
        LegacyRuleType ruleType = mapRuleTypeNcpf10(rule, type);
        rule.ruleType = ruleType.currentRule;
        switch(ruleType){
            case BETWEEN:
            case AXIAL:
                rule.isSpecificBlock = true;
                postMap.put(rule, (int) ruleCfg.getByte("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case VERTEX:
            case EDGE:
                rule.isSpecificBlock = true;
                postMap.put(rule, (int) ruleCfg.getByte("block"));
                break;
            case BETWEEN_GROUP:
            case AXIAL_GROUP:
                rule.isSpecificBlock = false;
                rule.blockType = rule.loadBlockType(ruleCfg.getByte("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case EDGE_GROUP:
            case VERTEX_GROUP:
                rule.isSpecificBlock = false;
                rule.blockType = rule.loadBlockType(ruleCfg.getByte("block"));
                break;
            case OR:
                ConfigList rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rulC));
                }
                break;
            case AND:
                rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rulC));
                }
                break;
        }
        return rule;
    }
}