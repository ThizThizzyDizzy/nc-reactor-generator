package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class LegacyNCPF2Reader extends LegacyNCPF3Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 2;
    }
    @Override
    protected synchronized Design readMultiblock(Project ncpf, InputStream in, RecoveryHandler recovery){
        Config data = Config.newConfig();
        data.load(in);
        Design design;
        int id = data.get("id");
        switch(id){
            case 0:
                design = readMultiblockUnderhaulSFR(ncpf, data, recovery);
                break;
            case 1:
                design = readMultiblockOverhaulSFR(ncpf, data, recovery);
                break;
            case 2:
                design = readMultiblockOverhaulMSR(ncpf, data, recovery);
                break;
            default:
                throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
        }
        if(data.hasProperty("metadata")){
            Config metadata = data.get("metadata");
            for(String key : metadata.properties()){
                design.metadata.put(key, metadata.get(key));
            }
        }
        return design;
    }
    
    @Override
    protected void loadOverhaulTurbineBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings){
        // turbines did not exist in NCPF 2
    }

    protected <Rule extends NCPFPlacementRule> Rule readGenericRuleNcpf2(HashMap<Rule, Integer> postMap, Supplier<Rule> newRule, Supplier<NCPFModule>[] blockTypes, Config ruleCfg, Supplier<NCPFModule> casing, String blockName){
        Rule rule = newRule.get();
        byte type = ruleCfg.get("type");
        switch(type){
            case 0:
                rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
                readRuleBlock(postMap, rule, ruleCfg, blockName);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case 1:
                rule.rule = NCPFPlacementRule.RuleType.AXIAL;
                readRuleBlock(postMap, rule, ruleCfg, blockName);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case 2:
                rule.rule = NCPFPlacementRule.RuleType.BETWEEN;
                readRuleBlockType(rule, blockTypes, ruleCfg);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case 3:
                rule.rule = NCPFPlacementRule.RuleType.AXIAL;
                readRuleBlockType(rule, blockTypes, ruleCfg);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case 4:
                rule.rule = NCPFPlacementRule.RuleType.AND;
                Rule vert = newRule.get();
                vert.rule = NCPFPlacementRule.RuleType.VERTEX;
                vert.target = new NCPFModuleReference(casing);
                rule.rules.add(vert);
                Rule exact = newRule.get();
                exact.rule = NCPFPlacementRule.RuleType.BETWEEN;
                exact.target = new NCPFModuleReference(casing);
                exact.min = exact.max = 3;
                rule.rules.add(exact);
                break;
            case 5:
                rule.rule = NCPFPlacementRule.RuleType.OR;
                ConfigList rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRuleNcpf2(postMap, newRule, blockTypes, rules.getConfig(i), casing, blockName));
                }
                break;
            case 6:
                rule.rule = NCPFPlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRuleNcpf2(postMap, newRule, blockTypes, rules.getConfig(i), casing, blockName));
                }
                break;
        }
        return rule;
    }

    @Override
    protected net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule readUnderRule(Config ruleCfg, String blockName){
        return readGenericRuleNcpf2(underhaulPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule::new,
                underhaulSFRBlockTypes, ruleCfg, net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule::new, blockName);
    }
    @Override
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule readOverSFRRule(Config ruleCfg, String blockName){
        return readGenericRuleNcpf2(overhaulSFRPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule::new,
                overhaulSFRBlockTypes, ruleCfg, net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule::new, blockName);
    }
    @Override
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule readOverMSRRule(Config ruleCfg, String blockName){
        return readGenericRuleNcpf2(overhaulMSRPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule::new,
                overhaulMSRBlockTypes, ruleCfg, net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule::new, blockName);
    }
}