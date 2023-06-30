package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.AbstractPlacementRule;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.IBlockTemplate;
import net.ncplanner.plannerator.multiblock.configuration.IBlockType;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class LegacyNCPF2Reader extends LegacyNCPF3Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 2;
    }

    @Override
    protected synchronized Multiblock readMultiblock(LegacyNCPFFile ncpf, InputStream in, RecoveryHandler recovery) {
        Config data = Config.newConfig();
        data.load(in);
        Multiblock multiblock;
        int id = data.get("id");
        switch(id){
            case 0:
                multiblock = readMultiblockUnderhaulSFR(ncpf, data, recovery);
                break;
            case 1:
                multiblock = readMultiblockOverhaulSFR(ncpf, data, recovery);
                break;
            case 2:
                multiblock = readMultiblockOverhaulMSR(ncpf, data, recovery);
                break;
            default:
                throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
        }
        if(data.hasProperty("metadata")){
            Config metadata = data.get("metadata");
            for(String key : metadata.properties()){
                multiblock.metadata.put(key, metadata.get(key));
            }
        }
        return multiblock;
    }

    @Override
    protected void loadOverhaulTurbineBlocks(Config overhaul, Configuration parent, Configuration configuration, boolean loadSettings) {
        // turbines did not exist in NCPF 2
    }

    protected <Rule extends AbstractPlacementRule<BlockType, Template>,
            BlockType extends IBlockType,
            Template extends IBlockTemplate> Rule readGenericRuleNcpf2(HashMap<Rule, Integer> postMap,
                                                                       Rule rule, Config ruleCfg,
                                                                       BlockType casing){
        byte type = ruleCfg.get("type");
        switch(type){
            case 0:
                rule.ruleType = AbstractPlacementRule.RuleType.BETWEEN;
                rule.isSpecificBlock = true;
                postMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 1:
                rule.ruleType = AbstractPlacementRule.RuleType.AXIAL;
                rule.isSpecificBlock = true;
                postMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 2:
                rule.ruleType = AbstractPlacementRule.RuleType.BETWEEN;
                rule.isSpecificBlock = false;
                rule.blockType = rule.loadBlockType(ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 3:
                rule.ruleType = AbstractPlacementRule.RuleType.AXIAL;
                rule.isSpecificBlock = false;
                rule.blockType = rule.loadBlockType(ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 4:
                rule.ruleType = AbstractPlacementRule.RuleType.AND;
                Rule vert = (Rule) rule.newRule();
                vert.ruleType = AbstractPlacementRule.RuleType.VERTEX;
                rule.isSpecificBlock = false;
                vert.blockType = casing;
                rule.rules.add(vert);
                Rule exact = (Rule) rule.newRule();
                exact.ruleType = AbstractPlacementRule.RuleType.BETWEEN;
                rule.isSpecificBlock = false;
                exact.blockType = casing;
                exact.min = exact.max = 3;
                rule.rules.add(exact);
                break;
            case 5:
                rule.ruleType = AbstractPlacementRule.RuleType.OR;
                ConfigList rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rules.getConfig(i)));
                }
                break;
            case 6:
                rule.ruleType = AbstractPlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rules.getConfig(i)));
                }
                break;
        }
        return rule;
    }

    @Override
    protected net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg) {
        return readGenericRuleNcpf2(underhaulPostLoadMap, new net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule(),
                ruleCfg, net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING);
    }
    @Override
    protected net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
        return readGenericRuleNcpf2(overhaulSFRPostLoadMap, new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule(),
                ruleCfg, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING);
    }
    @Override
    protected net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
        return readGenericRuleNcpf2(overhaulMSRPostLoadMap, new net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule(),
                ruleCfg, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING);
    }
}