package net.ncplanner.plannerator.planner.file.reader;

import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;

public class LegacyNCPF10Reader extends LegacyNCPF11Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 10;
    }

    protected enum LegacyRuleType {
        BETWEEN(NCPFPlacementRule.RuleType.BETWEEN),
        AXIAL(NCPFPlacementRule.RuleType.AXIAL),
        EDGE(NCPFPlacementRule.RuleType.EDGE),
        VERTEX(NCPFPlacementRule.RuleType.VERTEX),
        BETWEEN_GROUP(NCPFPlacementRule.RuleType.BETWEEN),
        AXIAL_GROUP(NCPFPlacementRule.RuleType.AXIAL),
        EDGE_GROUP(NCPFPlacementRule.RuleType.EDGE),
        VERTEX_GROUP(NCPFPlacementRule.RuleType.VERTEX),
        OR(NCPFPlacementRule.RuleType.OR),
        AND(NCPFPlacementRule.RuleType.AND);

        public final NCPFPlacementRule.RuleType currentRule;

        LegacyRuleType(NCPFPlacementRule.RuleType currentRule) {
            this.currentRule = currentRule;
        }
    }

    protected LegacyRuleType mapRuleTypeNcpf10(NCPFPlacementRule rule, byte type) {
        switch (type) {
            case 0:
                return LegacyRuleType.BETWEEN;
            case 1:
                return LegacyRuleType.AXIAL;
            case 2:
                return LegacyRuleType.VERTEX;
            case 3:
                return LegacyRuleType.BETWEEN_GROUP;
            case 4:
                return LegacyRuleType.AXIAL_GROUP;
            case 5:
                return LegacyRuleType.VERTEX_GROUP;
            case 6:
                return LegacyRuleType.OR;
            case 7:
                return LegacyRuleType.AND;
            default:
                throw new RuntimeException("Found rule with invalid type: "+type);
        }
    }
    protected LegacyRuleType mapTurbineRuleTypeNcpf10(NCPFPlacementRule rule, byte type) {
        switch (type) {
            case 0:
                return LegacyRuleType.BETWEEN;
            case 1:
                return LegacyRuleType.AXIAL;
            case 2:
                return LegacyRuleType.EDGE;
            case 3:
                return LegacyRuleType.BETWEEN_GROUP;
            case 4:
                return LegacyRuleType.AXIAL_GROUP;
            case 5:
                return LegacyRuleType.EDGE_GROUP;
            case 6:
                return LegacyRuleType.OR;
            case 7:
                return LegacyRuleType.AND;
            default:
                throw new RuntimeException("Found rule with invalid type: "+type);
        }
    }
    @Override
    protected <Rule extends NCPFPlacementRule> void readRuleBlock(HashMap<Rule, Integer> postMap, Rule rule, Config ruleCfg, String blockName){
        postMap.put(rule, (int)ruleCfg.getByte("block"));
        postNames.put(rule, blockName);
    }

    protected <Rule extends NCPFPlacementRule> void readRuleBlockType(Rule rule, Supplier<NCPFModule>[] blockTypes, Config ruleCfg) {
        rule.target = new NCPFModuleReference(blockTypes[ruleCfg.getByte("block")]);;
    }
    @Override
    protected <Rule extends NCPFPlacementRule> Rule readGenericRule(HashMap<Rule, Integer> postMap, Supplier<Rule> newRule, Supplier<NCPFModule>[] blockTypes, Config ruleCfg, String blockName){
        Rule rule = newRule.get();
        byte type = ruleCfg.get("type");
        LegacyRuleType ruleType = blockTypes==overhaulTurbineBlockTypes?mapTurbineRuleTypeNcpf10(rule, type):mapRuleTypeNcpf10(rule, type);
        rule.rule = ruleType.currentRule;
        switch(ruleType){
            case BETWEEN:
            case AXIAL:
                readRuleBlock(postMap, rule, ruleCfg, blockName);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case VERTEX:
            case EDGE:
                readRuleBlock(postMap, rule, ruleCfg, blockName);
                break;
            case BETWEEN_GROUP:
            case AXIAL_GROUP:
                readRuleBlockType(rule, blockTypes, ruleCfg);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case EDGE_GROUP:
            case VERTEX_GROUP:
                readRuleBlockType(rule, blockTypes, ruleCfg);
                break;
            case OR:
                ConfigList rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, newRule, blockTypes, rules.getConfig(i), blockName));
                }
                break;
            case AND:
                rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, newRule, blockTypes, rules.getConfig(i), blockName));
                }
                break;
        }
        rule.setReferences(null);
        return rule;
    }
}