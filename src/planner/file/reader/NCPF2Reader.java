package planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.Multiblock;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class NCPF2Reader extends NCPF3Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 2;
    }

    @Override
    protected synchronized Multiblock readMultiblock(NCPFFile ncpf, InputStream in) {
        Config data = Config.newConfig();
        data.load(in);
        Multiblock multiblock;
        int id = data.get("id");
        switch(id){
            case 0:
                multiblock = readMultiblockUnderhaulSFR(ncpf, data);
                break;
            case 1:
                multiblock = readMultiblockOverhaulSFR(ncpf, data);
                break;
            case 2:
                multiblock = readMultiblockOverhaulMSR(ncpf, data);
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
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rulC));
                }
                break;
            case 6:
                rule.ruleType = AbstractPlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readGenericRule(postMap, (Rule) rule.newRule(), rulC));
                }
                break;
        }
        return rule;
    }

    @Override
    protected multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg) {
        return readGenericRuleNcpf2(underhaulPostLoadMap, new multiblock.configuration.underhaul.fissionsfr.PlacementRule(),
                ruleCfg, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING);
    }
    @Override
    protected multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
        return readGenericRuleNcpf2(overhaulSFRPostLoadMap, new multiblock.configuration.overhaul.fissionsfr.PlacementRule(),
                ruleCfg, multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING);
    }
    @Override
    protected multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
        return readGenericRuleNcpf2(overhaulMSRPostLoadMap, new multiblock.configuration.overhaul.fissionmsr.PlacementRule(),
                ruleCfg, multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING);
    }
}