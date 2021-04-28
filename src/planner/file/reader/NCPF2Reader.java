package planner.file.reader;
import planner.core.PlannerImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.CuboidalMultiblock;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.file.FormatReader;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class NCPF2Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        try{
            Config header = Config.newConfig();
            header.load(in);
            in.close();
            return header.get("version", (byte)0)==(byte)2;
        }catch(Throwable t){
            return false;
        }
    }
    HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulSFRPostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Byte> overhaulMSRPostLoadMap = new HashMap<>();
    @Override
    public synchronized NCPFFile read(InputStream in){
        try{
            NCPFFile ncpf = new NCPFFile();
            Config header = Config.newConfig();
            header.load(in);
            int multiblocks = header.get("count");
            if(header.hasProperty("metadata")){
                Config metadata = header.get("metadata");
                for(String key : metadata.properties()){
                    ncpf.metadata.put(key, metadata.get(key));
                }
            }
            Config config = Config.newConfig();
            config.load(in);
            boolean partial = config.get("partial");
            if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
            else ncpf.configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
            ncpf.configuration.addon = false;
            //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
            if(config.hasProperty("underhaul")){
                ncpf.configuration.underhaul = new UnderhaulConfiguration();
                Config underhaul = config.get("underhaul");
                if(underhaul.hasProperty("fissionSFR")){
                    ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                    Config fissionSFR = underhaul.get("fissionSFR");
                    ncpf.configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                    ncpf.configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                    ncpf.configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                    ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                    ConfigList blocks = fissionSFR.get("blocks");
                    underhaulPostLoadMap.clear();
                    for(Iterator bit = blocks.iterator(); bit.hasNext();){
                        Config blockCfg = (Config)bit.next();
                        multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                        block.active = blockCfg.get("active");
                        block.cooling = blockCfg.get("cooling", 0);
                        block.fuelCell = blockCfg.get("fuelCell", false);
                        block.moderator = blockCfg.get("moderator", false);
                        if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                        if(blockCfg.hasProperty("rules")){
                            ConfigList rules = blockCfg.get("rules");
                            for(Iterator rit = rules.iterator(); rit.hasNext();){
                                Config ruleCfg = (Config)rit.next();
                                block.rules.add(readUnderRule(ruleCfg));
                            }
                        }
                        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                    }
                    for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                        byte index = underhaulPostLoadMap.get(rule);
                        if(index==0){
                            if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                            if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                            rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        }else{
                            rule.block = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                        }
                    }
                    ConfigList fuels = fissionSFR.get("fuels");
                    for(Iterator fit = fuels.iterator(); fit.hasNext();){
                        Config fuelCfg = (Config)fit.next();
                        multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                        ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                    }
                }
            }
//</editor-fold>
            //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
            if(config.hasProperty("overhaul")){
                ncpf.configuration.overhaul = new OverhaulConfiguration();
                Config overhaul = config.get("overhaul");
                //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                if(overhaul.hasProperty("fissionSFR")){
                    ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                    Config fissionSFR = overhaul.get("fissionSFR");
                    ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                    ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                    ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                    ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                    ConfigList blocks = fissionSFR.get("blocks");
                    overhaulSFRPostLoadMap.clear();
                    for(Iterator bit = blocks.iterator(); bit.hasNext();){
                        Config blockCfg = (Config)bit.next();
                        multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
                        int cooling = blockCfg.get("cooling", 0);
                        if(cooling!=0){
                            block.heatsink = true;
                            block.heatsinkHasBaseStats = true;
                            block.heatsinkCooling = cooling;
                        }
                        block.cluster = blockCfg.get("cluster", false);
                        block.createCluster = blockCfg.get("createCluster", false);
                        block.conductor = blockCfg.get("conductor", false);
                        block.fuelCell = blockCfg.get("fuelCell", false);
                        if(blockCfg.get("reflector", false)){
                            block.reflector = true;
                            block.reflectorHasBaseStats = true;
                            block.reflectorEfficiency = blockCfg.get("efficiency");
                            block.reflectorReflectivity = blockCfg.get("reflectivity");
                        }
                        block.irradiator = blockCfg.get("irradiator", false);
                        if(blockCfg.get("moderator", false)){
                            block.moderator = true;
                            block.moderatorHasBaseStats = true;
                            block.moderatorActive = blockCfg.get("activeModerator", false);
                            block.moderatorFlux = blockCfg.get("flux");
                            block.moderatorEfficiency = blockCfg.get("efficiency");
                        }
                        if(blockCfg.get("shield", false)){
                            block.shield = true;
                            block.shieldHasBaseStats = true;
                            block.shieldHeat = blockCfg.get("heatMult");
                            block.shieldEfficiency = blockCfg.get("efficiency");
                        }
                        block.blocksLOS = blockCfg.get("blocksLOS", false);
                        block.functional = blockCfg.get("functional");
                        if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                        if(blockCfg.hasProperty("closedTexture"))block.setShieldClosedTexture(loadNCPFTexture(blockCfg.get("closedTexture")));
                        if(blockCfg.hasProperty("rules")){
                            ConfigList rules = blockCfg.get("rules");
                            for(Iterator rit = rules.iterator(); rit.hasNext();){
                                Config ruleCfg = (Config)rit.next();
                                block.rules.add(readOverSFRRule(ruleCfg));
                            }
                        }
                        ncpf.configuration.overhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                    }
                    for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                        byte index = overhaulSFRPostLoadMap.get(rule);
                        if(index==0){
                            if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                            if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                            rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        }else{
                            rule.block = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                        }
                    }
                    ConfigList fuels = fissionSFR.get("fuels");
                    for(Iterator fit = fuels.iterator(); fit.hasNext();){
                        Config fuelCfg = (Config)fit.next();
                        multiblock.configuration.overhaul.fissionsfr.BlockRecipe fuel = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(fuelCfg.get("name"), "null");
                        fuel.fuelCellEfficiency = fuelCfg.get("efficiency");
                        fuel.fuelCellHeat = fuelCfg.get("heat");
                        fuel.fuelCellTime = fuelCfg.get("time");
                        fuel.fuelCellCriticality = fuelCfg.get("criticality");
                        fuel.fuelCellSelfPriming = fuelCfg.get("selfPriming", false);
                        for(multiblock.configuration.overhaul.fissionsfr.Block b : ncpf.configuration.overhaul.fissionSFR.allBlocks){
                            if(b.fuelCell){
                                b.allRecipes.add(fuel);b.recipes.add(fuel);
                            }
                        }
                    }
                    ConfigList sources = fissionSFR.get("sources");
                    for(Iterator sit = sources.iterator(); sit.hasNext();){
                        Config sourceCfg = (Config)sit.next();
                        multiblock.configuration.overhaul.fissionsfr.Block source = new multiblock.configuration.overhaul.fissionsfr.Block(sourceCfg.get("name"));
                        source.source = true;
                        source.sourceEfficiency = sourceCfg.get("efficiency");
                        ncpf.configuration.overhaul.fissionSFR.allBlocks.add(source);ncpf.configuration.overhaul.fissionSFR.blocks.add(source);
                    }
                    ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                    for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                        Config irradiatorRecipeCfg = (Config)irit.next();
                        multiblock.configuration.overhaul.fissionsfr.BlockRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(irradiatorRecipeCfg.get("name"), "null");
                        irrecipe.irradiatorEfficiency = irradiatorRecipeCfg.get("efficiency");
                        irrecipe.irradiatorHeat = irradiatorRecipeCfg.get("heat");
                        for(multiblock.configuration.overhaul.fissionsfr.Block b : ncpf.configuration.overhaul.fissionSFR.allBlocks){
                            if(b.irradiator){
                                b.allRecipes.add(irrecipe);b.recipes.add(irrecipe);
                            }
                        }
                    }
                    ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                    for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                        Config coolantRecipeCfg = (Config)irit.next();
                        multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.getInt("outputRatio"));
                        ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                    }
                    for(multiblock.configuration.overhaul.fissionsfr.Block b : ncpf.configuration.overhaul.fissionSFR.allBlocks){
                        if(!b.allRecipes.isEmpty()){
                            b.port = new multiblock.configuration.overhaul.fissionsfr.Block("null");
                        }
                    }
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                if(overhaul.hasProperty("fissionMSR")){
                    ncpf.configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                    Config fissionMSR = overhaul.get("fissionMSR");
                    ncpf.configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                    ncpf.configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                    ncpf.configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                    ncpf.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                    ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                    ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                    ConfigList blocks = fissionMSR.get("blocks");
                    overhaulMSRPostLoadMap.clear();
                    for(Iterator bit = blocks.iterator(); bit.hasNext();){
                        Config blockCfg = (Config)bit.next();
                        multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                        int cooling = blockCfg.get("cooling", 0);
                        if(cooling!=0){
                            block.heater = true;
                            multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(blockCfg.get("input", ""), blockCfg.get("output", ""));
                            recipe.heaterCooling = cooling;
                            recipe.inputRate = blockCfg.hasProperty("input")?1:0;
                            recipe.outputRate = blockCfg.hasProperty("output")?1:0;
                            block.allRecipes.add(recipe);block.recipes.add(recipe);
                        }
                        block.cluster = blockCfg.get("cluster", false);
                        block.createCluster = blockCfg.get("createCluster", false);
                        block.conductor = blockCfg.get("conductor", false);
                        block.fuelVessel = blockCfg.get("fuelVessel", false);
                        if(blockCfg.get("reflector", false)){
                            block.reflector = true;
                            block.reflectorHasBaseStats = true;
                            block.reflectorEfficiency = blockCfg.get("efficiency");
                            block.reflectorReflectivity = blockCfg.get("reflectivity");
                        }
                        block.irradiator = blockCfg.get("irradiator", false);
                        if(blockCfg.get("moderator", false)){
                            block.moderator = true;
                            block.moderatorHasBaseStats = true;
                            block.moderatorActive = blockCfg.get("activeModerator", false);
                            block.moderatorFlux = blockCfg.get("flux");
                            block.moderatorEfficiency = blockCfg.get("efficiency");
                        }
                        if(blockCfg.get("shield", false)){
                            block.shield = true;
                            block.shieldHasBaseStats = true;
                            block.shieldHeat = blockCfg.get("heatMult");
                            block.shieldEfficiency = blockCfg.get("efficiency");
                        }
                        block.blocksLOS = blockCfg.get("blocksLOS", false);
                        block.functional = blockCfg.get("functional");
                        if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                        if(blockCfg.hasProperty("closedTexture"))block.setShieldClosedTexture(loadNCPFTexture(blockCfg.get("closedTexture")));
                        if(blockCfg.hasProperty("rules")){
                            ConfigList rules = blockCfg.get("rules");
                            for(Iterator rit = rules.iterator(); rit.hasNext();){
                                Config ruleCfg = (Config)rit.next();
                                block.rules.add(readOverMSRRule(ruleCfg));
                            }
                        }
                        ncpf.configuration.overhaul.fissionMSR.allBlocks.add(block);ncpf.configuration.overhaul.fissionMSR.blocks.add(block);
                    }
                    for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                        byte index = overhaulMSRPostLoadMap.get(rule);
                        if(index==0){
                            if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                            if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                            rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                        }else{
                            rule.block = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(index-1);
                        }
                    }
                    ConfigList fuels = fissionMSR.get("fuels");
                    for(Iterator fit = fuels.iterator(); fit.hasNext();){
                        Config fuelCfg = (Config)fit.next();
                        multiblock.configuration.overhaul.fissionmsr.BlockRecipe fuel = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(fuelCfg.get("name"), "null");
                        fuel.inputRate = fuel.outputRate = 1;
                        fuel.fuelVesselEfficiency = fuelCfg.get("efficiency");
                        fuel.fuelVesselHeat = fuelCfg.get("heat");
                        fuel.fuelVesselTime = fuelCfg.get("time");
                        fuel.fuelVesselCriticality = fuelCfg.get("criticality");
                        fuel.fuelVesselSelfPriming = fuelCfg.get("selfPriming", false);
                        for(multiblock.configuration.overhaul.fissionmsr.Block b : ncpf.configuration.overhaul.fissionMSR.allBlocks){
                            if(b.fuelVessel){
                                b.allRecipes.add(fuel);b.recipes.add(fuel);
                            }
                        }
                    }
                    ConfigList sources = fissionMSR.get("sources");
                    for(Iterator sit = sources.iterator(); sit.hasNext();){
                        Config sourceCfg = (Config)sit.next();
                        multiblock.configuration.overhaul.fissionmsr.Block source = new multiblock.configuration.overhaul.fissionmsr.Block(sourceCfg.get("name"));
                        source.source = true;
                        source.sourceEfficiency = sourceCfg.get("efficiency");
                        ncpf.configuration.overhaul.fissionMSR.allBlocks.add(source);ncpf.configuration.overhaul.fissionMSR.blocks.add(source);
                    }
                    ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                    for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                        Config irradiatorRecipeCfg = (Config)irit.next();
                        multiblock.configuration.overhaul.fissionmsr.BlockRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(irradiatorRecipeCfg.get("name"), "null");
                        irrecipe.irradiatorEfficiency = irradiatorRecipeCfg.get("efficiency");
                        irrecipe.irradiatorHeat = irradiatorRecipeCfg.get("heat");
                        for(multiblock.configuration.overhaul.fissionmsr.Block b : ncpf.configuration.overhaul.fissionMSR.allBlocks){
                            if(b.irradiator){
                                b.allRecipes.add(irrecipe);b.recipes.add(irrecipe);
                            }
                        }
                    }
                    for(multiblock.configuration.overhaul.fissionmsr.Block b : ncpf.configuration.overhaul.fissionMSR.allBlocks){
                        if(!b.allRecipes.isEmpty()){
                            b.port = new multiblock.configuration.overhaul.fissionmsr.Block("null");
                        }
                    }
                }
//</editor-fold>
            }
//</editor-fold>
            for(int i = 0; i<multiblocks; i++){
                Config data = Config.newConfig();
                data.load(in);
                Multiblock multiblock;
                int id = data.get("id");
                switch(id){
                    case 0:
                        ConfigNumberList size = data.get("size");
                        UnderhaulSFR underhaulSFR = new UnderhaulSFR(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                        boolean compact = data.get("compact");
                        ConfigNumberList blocks = data.get("blocks");
                        if(compact){
                            int[] index = new int[1];
                            underhaulSFR.forEachInternalPosition((x, y, z) -> {
                                int bid = (int) blocks.get(index[0]);
                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(ncpf.configuration, x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                index[0]++;
                            });
                        }else{
                            for(int j = 0; j<blocks.size(); j+=4){
                                int x = (int) blocks.get(j)+1;
                                int y = (int) blocks.get(j+1)+1;
                                int z = (int) blocks.get(j+2)+1;
                                int bid = (int) blocks.get(j+3);
                                underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(ncpf.configuration, x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                            }
                        }
                        multiblock = underhaulSFR;
                        break;
                    case 1:
                        size = data.get("size");
                        OverhaulSFR overhaulSFR = new OverhaulSFR(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                        compact = data.get("compact");
                        blocks = data.get("blocks");
                        if(compact){
                            int[] index = new int[1];
                            overhaulSFR.forEachInternalPosition((x, y, z) -> {
                                int bid = (int) blocks.get(index[0]);
                                if(bid>0){
                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(ncpf.configuration, x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                }
                                index[0]++;
                            });
                        }else{
                            for(int j = 0; j<blocks.size(); j+=4){
                                int x = (int) blocks.get(j)+1;
                                int y = (int) blocks.get(j+1)+1;
                                int z = (int) blocks.get(j+2)+1;
                                int bid = (int) blocks.get(j+3);
                                overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(ncpf.configuration, x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                            }
                        }
                        ConfigNumberList fuels = data.get("fuels");
                        ConfigNumberList sources = data.get("sources");
                        ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                        int fuelIndex = 0;
                        int sourceIndex = 0;
                        int recipeIndex = 0;
                        ArrayList<multiblock.configuration.overhaul.fissionsfr.Block> srces = new ArrayList<>();
                        for(multiblock.configuration.overhaul.fissionsfr.Block bl : ncpf.configuration.overhaul.fissionSFR.allBlocks){
                            if(bl.source)srces.add(bl);
                        }
                        for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                            if(block.template.fuelCell){
                                block.recipe = block.template.allRecipes.get((int)fuels.get(fuelIndex));
                                fuelIndex++;
                                int sid = (int) sources.get(sourceIndex);
                                if(sid>0)block.addNeutronSource(overhaulSFR, srces.get(sid-1));
                                sourceIndex++;
                            }
                            if(block.template.irradiator){
                                int rid = (int) irradiatorRecipes.get(recipeIndex);
                                if(rid>0)block.recipe = block.template.allRecipes.get(rid-1);
                                recipeIndex++;
                            }
                        }
                        multiblock = overhaulSFR;
                        break;
                    case 2:
                        size = data.get("size");
                        OverhaulMSR overhaulMSR = new OverhaulMSR(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2));
                        compact = data.get("compact");
                        blocks = data.get("blocks");
                        if(compact){
                            int[] index = new int[1];
                            overhaulMSR.forEachInternalPosition((x, y, z) -> {
                                int bid = (int) blocks.get(index[0]);
                                if(bid>0){
                                    overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(ncpf.configuration, x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                }
                                index[0]++;
                            });
                        }else{
                            for(int j = 0; j<blocks.size(); j+=4){
                                int x = (int) blocks.get(j)+1;
                                int y = (int) blocks.get(j+1)+1;
                                int z = (int) blocks.get(j+2)+1;
                                int bid = (int) blocks.get(j+3);
                                overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(ncpf.configuration, x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                            }
                        }
                        fuels = data.get("fuels");
                        sources = data.get("sources");
                        irradiatorRecipes = data.get("irradiatorRecipes");
                        fuelIndex = 0;
                        sourceIndex = 0;
                        recipeIndex = 0;
                        ArrayList<multiblock.configuration.overhaul.fissionmsr.Block> msrces = new ArrayList<>();
                        for(multiblock.configuration.overhaul.fissionmsr.Block bl : ncpf.configuration.overhaul.fissionMSR.allBlocks){
                            if(bl.source)msrces.add(bl);
                        }
                        for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
                            if(block.template.fuelVessel){
                                block.recipe = block.template.allRecipes.get((int)fuels.get(fuelIndex));
                                fuelIndex++;
                                int sid = (int) sources.get(sourceIndex);
                                if(sid>0)block.addNeutronSource(overhaulMSR, msrces.get(sid-1));
                                sourceIndex++;
                            }
                            if(block.template.irradiator){
                                int rid = (int) irradiatorRecipes.get(recipeIndex);
                                if(rid>0)block.recipe = block.template.allRecipes.get(rid-1);
                                recipeIndex++;
                            }
                            if(block.template.heater&&!block.template.allRecipes.isEmpty())block.recipe = block.template.allRecipes.get(0);
                        }
                        multiblock = overhaulMSR;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
                }
                if(multiblock instanceof CuboidalMultiblock)((CuboidalMultiblock)multiblock).buildDefaultCasingOnConvert();
                if(data.hasProperty("metadata")){
                    Config metadata = data.get("metadata");
                    for(String key : metadata.properties()){
                        multiblock.metadata.put(key, metadata.get(key));
                    }
                }
                ncpf.multiblocks.add(multiblock);
            }
            in.close();
            return ncpf;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
        multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
        byte type = ruleCfg.get("type");
        switch(type){
            case 0:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 1:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 2:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                byte blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 3:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 4:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                multiblock.configuration.underhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                vert.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                vert.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                rule.rules.add(vert);
                multiblock.configuration.underhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                exact.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                exact.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                exact.min = exact.max = 3;
                rule.rules.add(exact);
                break;
            case 5:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                ConfigList rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readUnderRule(rulC));
                }
                break;
            case 6:
                rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readUnderRule(rulC));
                }
                break;
        }
        return rule;
    }
    private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
        multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
        byte type = ruleCfg.get("type");
        switch(type){
            case 0:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 1:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 2:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                byte blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                        break;
                    case 5:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                        break;
                    case 6:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                        break;
                    case 7:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                        break;
                    case 8:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 3:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                        break;
                    case 5:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                        break;
                    case 6:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                        break;
                    case 7:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                        break;
                    case 8:
                        rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 4:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                multiblock.configuration.overhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                vert.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                vert.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                rule.rules.add(vert);
                multiblock.configuration.overhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                exact.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                exact.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                exact.min = exact.max = 3;
                rule.rules.add(exact);
                break;
            case 5:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                ConfigList rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readOverSFRRule(rulC));
                }
                break;
            case 6:
                rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readOverSFRRule(rulC));
                }
                break;
        }
        return rule;
    }
    private multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
        multiblock.configuration.overhaul.fissionmsr.PlacementRule rule = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
        byte type = ruleCfg.get("type");
        switch(type){
            case 0:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN;
                overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 1:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL;
                overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 2:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                byte blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                        break;
                    case 5:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                        break;
                    case 6:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                        break;
                    case 7:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                        break;
                    case 8:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 3:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                blockType = ruleCfg.get("block");
                switch(blockType){
                    case 0:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                        break;
                    case 1:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                        break;
                    case 2:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                        break;
                    case 3:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                        break;
                    case 4:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                        break;
                    case 5:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                        break;
                    case 6:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                        break;
                    case 7:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                        break;
                    case 8:
                        rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                        break;
                }
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case 4:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                multiblock.configuration.overhaul.fissionmsr.PlacementRule vert = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                vert.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX_GROUP;
                vert.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                rule.rules.add(vert);
                multiblock.configuration.overhaul.fissionmsr.PlacementRule exact = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                exact.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                exact.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                exact.min = exact.max = 3;
                rule.rules.add(exact);
                break;
            case 5:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.OR;
                ConfigList rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readOverMSRRule(rulC));
                }
                break;
            case 6:
                rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                rules = ruleCfg.get("rules");
                for(Iterator rit = rules.iterator(); rit.hasNext();){
                    Config rulC = (Config)rit.next();
                    rule.rules.add(readOverMSRRule(rulC));
                }
                break;
        }
        return rule;
    }
    private PlannerImage loadNCPFTexture(ConfigNumberList texture){
        int size = (int) texture.get(0);
        PlannerImage image = new PlannerImage(size, size);
        int index = 1;
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                image.setRGB(x, y, (int)texture.get(index));
                index++;
            }
        }
        return image;
    }
}