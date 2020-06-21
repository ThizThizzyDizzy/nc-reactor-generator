package planner.file;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import simplelibrary.config2.Config;
import planner.configuration.Configuration;
import planner.configuration.PartialConfiguration;
import planner.configuration.overhaul.OverhaulConfiguration;
import planner.configuration.underhaul.UnderhaulConfiguration;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    static{
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(File file){
                try(FileInputStream in = new FileInputStream(file)){
                    Config header = Config.newConfig();
                    header.load(in);
                    return header.get("version", (byte)0)==(byte)1;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<planner.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<planner.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulPostLoadMap = new HashMap<>();
            @Override
            public synchronized NCPFFile read(File file){
                try(FileInputStream in = new FileInputStream(file)){
                    Config header = Config.newConfig();
                    header.load(in);
                    int multiblocks = header.get("count");
                    Config config = Config.newConfig();
                    config.load(in);
                    boolean partial = config.get("partial");
                    NCPFFile ncpf = new NCPFFile();
                    if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.get("version"));
                    else ncpf.configuration = new Configuration(config.get("name"), config.get("version"));
                    if(config.hasProperty("underhaul")){
                        ncpf.configuration.underhaul = new UnderhaulConfiguration();
                        Config underhaul = config.get("underhaul");
                        if(underhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.underhaul.fissionSFR = new planner.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
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
                                planner.configuration.underhaul.fissionsfr.Block block = new planner.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.active = blockCfg.get("active");
                                block.cooling = blockCfg.get("cooling", 0);
                                block.fuelCell = blockCfg.get("fuelCell", false);
                                block.moderator = blockCfg.get("moderator", false);
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            image.setRGB(x, y, (int) texture.get(index));
                                            index++;
                                        }
                                    }
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readUnderRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                            }
                            for(planner.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                                byte index = underhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.underhaul.fissionSFR.blocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                ncpf.configuration.underhaul.fissionSFR.fuels.add(new planner.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time")));
                            }
                        }
                    }
                    if(config.hasProperty("overhaul")){
                        ncpf.configuration.overhaul = new OverhaulConfiguration();
                        Config overhaul = config.get("overhaul");
                        if(overhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.overhaul.fissionSFR = new planner.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = overhaul.get("fissionSFR");
                            ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionSFR.get("blocks");
                            overhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                planner.configuration.overhaul.fissionsfr.Block block = new planner.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.cooling = blockCfg.get("cooling", 0);
                                block.cluster = blockCfg.get("cluster", false);
                                block.createCluster = blockCfg.get("createCluster", false);
                                block.conductor = blockCfg.get("conductor", false);
                                block.fuelCell = blockCfg.get("fuelCell", false);
                                block.reflector = blockCfg.get("reflector", false);
                                block.irradiator = blockCfg.get("irradiator", false);
                                block.moderator = blockCfg.get("moderator", false);
                                block.activeModerator = blockCfg.get("activeModerator", false);
                                block.shield = blockCfg.get("shield", false);
                                if(blockCfg.hasProperty("flux"))block.flux = blockCfg.get("flux");
                                if(blockCfg.hasProperty("efficiency"))block.efficiency = blockCfg.get("efficiency");
                                if(blockCfg.hasProperty("reflectivity"))block.reflectivity = blockCfg.get("reflectivity");
                                if(blockCfg.hasProperty("heatMult"))block.heatMult = blockCfg.get("heatMult");
                                block.blocksLOS = blockCfg.get("blocksLOS", false);
                                block.functional = blockCfg.get("functional");
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            image.setRGB(x, y, (int) texture.get(index));
                                            index++;
                                        }
                                    }
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                            }
                            for(planner.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulPostLoadMap.keySet()){
                                byte index = overhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionSFR.blocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                ncpf.configuration.overhaul.fissionSFR.fuels.add(new planner.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming")));
                            }
                            ConfigList sources = fissionSFR.get("sources");
                            for(Iterator fit = sources.iterator(); fit.hasNext();){
                                Config sourceCfg = (Config)fit.next();
                                ncpf.configuration.overhaul.fissionSFR.sources.add(new planner.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency")));
                            }
                            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                            for(Iterator fit = irradiatorRecipes.iterator(); fit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)fit.next();
                                ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(new planner.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat")));
                            }
                        }
                    }
                    //TODO and the multiblocks?
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private planner.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                planner.configuration.underhaul.fissionsfr.PlacementRule rule = new planner.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.NO_PANCAKES;
                        break;
                    case 5:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = planner.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private planner.configuration.overhaul.fissionsfr.PlacementRule readOverRule(Config ruleCfg){
                planner.configuration.overhaul.fissionsfr.PlacementRule rule = new planner.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = planner.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.NO_PANCAKES;
                        break;
                    case 5:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = planner.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverRule(rulC));
                        }
                        break;
                }
                return rule;
            }
        });// .ncpf
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(File file){//There's probably a better way of detecting the format...
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    String s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    return s.contains("D:fission_cooling_rate");
                }catch(IOException ex){}
                return false;
            }
            String s = "";
            @Override
            public synchronized NCPFFile read(File file){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    NCPFFile ncpf = new NCPFFile();
                    s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    ncpf.configuration = new Configuration(null, null);
                    ncpf.configuration.underhaul = new UnderhaulConfiguration();
                    ncpf.configuration.underhaul.fissionSFR = new planner.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                    boolean waterCoolerRequirements = getBoolean("fission_water_cooler_requirement");
                    double powerMult = getDouble("fission_power");
                    double fuelUseMult = getDouble("fission_fuel_use");
                    double heatMult = getDouble("fission_heat_generation");
                    ncpf.configuration.underhaul.fissionSFR.minSize = getInt("fission_min_size");
                    ncpf.configuration.underhaul.fissionSFR.maxSize = getInt("fission_max_size");
                    ncpf.configuration.underhaul.fissionSFR.neutronReach = getInt("fission_neutron_reach");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = (float) getDouble("fission_moderator_extra_power");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = (float) getDouble("fission_moderator_extra_heat");
                    ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = getInt("fission_active_cooler_max_rate");
                    int[] coolingRates = getDoublesAsInts("fission_cooling_rate");
                    planner.configuration.underhaul.fissionsfr.Block cell = planner.configuration.underhaul.fissionsfr.Block.fuelCell("Fuel Cell", "underhaul/cell.png");
                    planner.configuration.underhaul.fissionsfr.Block water = planner.configuration.underhaul.fissionsfr.Block.cooler("Water Cooler", coolingRates[0], "underhaul/water.png", planner.configuration.underhaul.fissionsfr.PlacementRule.or(planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR)));
                    if(!waterCoolerRequirements){
                        water.rules.clear();
                    }
                    planner.configuration.underhaul.fissionsfr.Block redstone = planner.configuration.underhaul.fissionsfr.Block.cooler("Redstone Cooler", coolingRates[1], "underhaul/redstone.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block quartz = planner.configuration.underhaul.fissionsfr.Block.cooler("Quartz Cooler", coolingRates[2], "underhaul/quartz.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    planner.configuration.underhaul.fissionsfr.Block gold = planner.configuration.underhaul.fissionsfr.Block.cooler("Gold Cooler", coolingRates[3], "underhaul/gold.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, redstone));
                    planner.configuration.underhaul.fissionsfr.Block glowstone = planner.configuration.underhaul.fissionsfr.Block.cooler("Glowstone Cooler", coolingRates[4], "underhaul/glowstone.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    planner.configuration.underhaul.fissionsfr.Block lapis = planner.configuration.underhaul.fissionsfr.Block.cooler("Lapis Cooler", coolingRates[5], "underhaul/lapis.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL),planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    planner.configuration.underhaul.fissionsfr.Block diamond = planner.configuration.underhaul.fissionsfr.Block.cooler("Diamond Cooler", coolingRates[6], "underhaul/diamond.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, quartz));
                    planner.configuration.underhaul.fissionsfr.Block helium = planner.configuration.underhaul.fissionsfr.Block.cooler("Helium Cooler", coolingRates[7], "underhaul/helium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.exactly(1, redstone), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    planner.configuration.underhaul.fissionsfr.Block enderium = planner.configuration.underhaul.fissionsfr.Block.cooler("Enderium Cooler", coolingRates[8], "underhaul/enderium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.exactly(3, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), planner.configuration.underhaul.fissionsfr.PlacementRule.noPancake());
                    planner.configuration.underhaul.fissionsfr.Block cryotheum = planner.configuration.underhaul.fissionsfr.Block.cooler("Cryotheum Cooler", coolingRates[9], "underhaul/cryotheum.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block iron = planner.configuration.underhaul.fissionsfr.Block.cooler("Iron Cooler", coolingRates[10], "underhaul/iron.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, gold));
                    planner.configuration.underhaul.fissionsfr.Block emerald = planner.configuration.underhaul.fissionsfr.Block.cooler("Emerald Cooler", coolingRates[11], "underhaul/emerald.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block copper = planner.configuration.underhaul.fissionsfr.Block.cooler("Copper Cooler", coolingRates[12], "underhaul/copper.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, glowstone));
                    planner.configuration.underhaul.fissionsfr.Block tin = planner.configuration.underhaul.fissionsfr.Block.cooler("Tin Cooler", coolingRates[13], "underhaul/tin.png", planner.configuration.underhaul.fissionsfr.PlacementRule.axis(lapis));
                    planner.configuration.underhaul.fissionsfr.Block magnesium = planner.configuration.underhaul.fissionsfr.Block.cooler("Magnesium Cooler", coolingRates[14], "underhaul/magnesium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    planner.configuration.underhaul.fissionsfr.Block graphite = planner.configuration.underhaul.fissionsfr.Block.moderator("Graphite", "underhaul/graphite.png");
                    planner.configuration.underhaul.fissionsfr.Block beryllium = planner.configuration.underhaul.fissionsfr.Block.moderator("Beryllium", "underhaul/beryllium.png");
                    int[] activeCoolingRates = getDoublesAsInts("fission_active_cooling_rate");
                    planner.configuration.underhaul.fissionsfr.Block activeWater = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Water Cooler", activeCoolingRates[0], "Water", "underhaul/water.png", planner.configuration.underhaul.fissionsfr.PlacementRule.or(planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR)));
                    planner.configuration.underhaul.fissionsfr.Block activeRedstone = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Redstone Cooler", activeCoolingRates[1], "Destabilized Redstone", "underhaul/redstone.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block activeQuartz = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Quartz Cooler", activeCoolingRates[2], "Molten Quartz", "underhaul/quartz.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    planner.configuration.underhaul.fissionsfr.Block activeGold = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Gold Cooler", activeCoolingRates[3], "Molten Gold", "underhaul/gold.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, redstone));
                    planner.configuration.underhaul.fissionsfr.Block activeGlowstone = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Glowstone Cooler", activeCoolingRates[4], "Energized Glowstone", "underhaul/glowstone.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    planner.configuration.underhaul.fissionsfr.Block activeLapis = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Lapis Cooler", activeCoolingRates[5], "Molten Lapis", "underhaul/lapis.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL),planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    planner.configuration.underhaul.fissionsfr.Block activeDiamond = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Diamond Cooler", activeCoolingRates[6], "Molten Diamond", "underhaul/diamond.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, quartz));
                    planner.configuration.underhaul.fissionsfr.Block activeHelium = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Helium Cooler", activeCoolingRates[7], "Liquid Helium", "underhaul/helium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.exactly(1, redstone), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    planner.configuration.underhaul.fissionsfr.Block activeEnderium = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Enderium Cooler", activeCoolingRates[8], "Resonant Ender", "underhaul/enderium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.exactly(3, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), planner.configuration.underhaul.fissionsfr.PlacementRule.noPancake());
                    planner.configuration.underhaul.fissionsfr.Block activeCryotheum = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Cryotheum Cooler", activeCoolingRates[9], "Gelid Cryotheum", "underhaul/cryotheum.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block activeIron = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Iron Cooler", activeCoolingRates[10], "Molten Iron", "underhaul/iron.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, gold));
                    planner.configuration.underhaul.fissionsfr.Block activeEmerald = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Emerald Cooler", activeCoolingRates[11], "Molten Emerald", "underhaul/emerald.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    planner.configuration.underhaul.fissionsfr.Block activeCopper = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Copper Cooler", activeCoolingRates[12], "Molten Copper", "underhaul/copper.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, glowstone));
                    planner.configuration.underhaul.fissionsfr.Block activeTin = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Tin Cooler", activeCoolingRates[13], "Molten Tin", "underhaul/tin.png", planner.configuration.underhaul.fissionsfr.PlacementRule.axis(lapis));
                    planner.configuration.underhaul.fissionsfr.Block activeMagnesium = planner.configuration.underhaul.fissionsfr.Block.activeCooler("Active Magnesium Cooler", activeCoolingRates[14], "Molten Magnesium", "underhaul/magnesium.png", planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), planner.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, planner.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(cell);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(water);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(redstone);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(quartz);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(gold);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(glowstone);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(lapis);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(diamond);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(helium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(enderium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(cryotheum);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(iron);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(emerald);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(copper);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(tin);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(magnesium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(graphite);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(beryllium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeWater);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeRedstone);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeQuartz);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeGold);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeGlowstone);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeLapis);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeDiamond);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeHelium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeEnderium);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeCryotheum);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeIron);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeEmerald);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeCopper);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeTin);
                    ncpf.configuration.underhaul.fissionSFR.blocks.add(activeMagnesium);
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "thorium", "TBU", "TBU Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "uranium", "LEU-233", "LEU-233 Oxide", "HEU-233", "HEU-233 Oxide", "LEU-235", "LEU-235 Oxide", "HEU-235", "HEU-235 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "neptunium", "LEN-236", "LEN-236 Oxide", "HEN-236", "HEN-236 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "plutonium", "LEP-239", "LEP-239 Oxide", "HEP-239", "HEP-239 Oxide", "LEP-241", "LEP-241 Oxide", "HEP-241", "HEP-241 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "mox", "MOX-239", "MOX-241");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "americium", "LEA-242", "LEA-242 Oxide", "HEA-242", "HEA-242 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "curium", "LECm-243", "LECm-243 Oxide", "HECm-243", "HECm-243 Oxide", "LECm-245", "LECm-245 Oxide", "HECm-245", "HECm-245 Oxide", "LECm-247", "LECm-247 Oxide", "HECm-247", "HECm-247 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "berkelium", "LEB-248", "LEB-248 Oxide", "HEB-248", "HEB-248 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "californium", "LECf-249", "LECf-249 Oxide", "HECf-249", "HECf-249 Oxide", "LECf-251", "LECf-251 Oxide", "HECf-251", "HECf-251 Oxide");
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private void addFuels(NCPFFile ncpf, double powerMult, double heatMult, double fuelUseMult, String baseName, String... fuelNames){
                double[] time = getDoubles("fission_"+baseName+"_fuel_time");
                double[] power = getDoubles("fission_"+baseName+"_power");
                double[] heat = getDoubles("fission_"+baseName+"_heat_generation");
                for(int i = 0; i<fuelNames.length; i++){
                    ncpf.configuration.underhaul.fissionSFR.fuels.add(new planner.configuration.underhaul.fissionsfr.Fuel(fuelNames[i], (float)(power[i]*powerMult), (float)(heat[i]*heatMult), (int)(time[i]/fuelUseMult)));
                }
            }
            private double getDouble(String name){
                String str = s.substring(s.indexOf("D:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Double.parseDouble(str);
            }
            private int getInt(String name){
                String str = s.substring(s.indexOf("I:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Integer.parseInt(str);
            }
            private boolean getBoolean(String name){
                String str = s.substring(s.indexOf("B:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Boolean.parseBoolean(str);
            }
            private double[] getDoubles(String name){
                ArrayList<Double> doubles = new ArrayList<>();
                String str = s.substring(s.indexOf("D:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    doubles.add(Double.parseDouble(st.trim()));
                }
                double[] ret = new double[doubles.size()];
                for(int i = 0; i<doubles.size(); i++){
                    ret[i] = doubles.get(i);
                }
                return ret;
            }
            private int[] getDoublesAsInts(String name){
                double[] ds = getDoubles(name);
                int[] is = new int[ds.length];
                for(int i = 0; i<is.length; i++){
                    is[i] = (int)ds[i];
                }
                return is;
            }
            private int[] getInts(String name){
                ArrayList<Integer> ints = new ArrayList<>();
                String str = s.substring(s.indexOf("I:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    ints.add(Integer.parseInt(st.trim()));
                }
                int[] ret = new int[ints.size()];
                for(int i = 0; i<ints.size(); i++){
                    ret[i] = ints.get(i);
                }
                return ret;
            }
            private boolean[] getBooleans(String name){
                ArrayList<Boolean> booleans = new ArrayList<>();
                String str = s.substring(s.indexOf("B:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    booleans.add(Boolean.parseBoolean(st.trim()));
                }
                boolean[] ret = new boolean[booleans.size()];
                for(int i = 0; i<booleans.size(); i++){
                    ret[i] = booleans.get(i);
                }
                return ret;
            }
        });// UNDERHAUL nuclearcraft.cfg
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(File file){//There's probably a better way of detecting the format...
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    String s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    return s.contains("I:fission_sink_cooling_rate");
                }catch(IOException ex){}
                return false;
            }
            String s = "";
            @Override
            public synchronized NCPFFile read(File file){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))){
                    NCPFFile ncpf = new NCPFFile();
                    s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    ncpf.configuration = new Configuration(null, null);
                    ncpf.configuration.overhaul = new OverhaulConfiguration();
                    ncpf.configuration.overhaul.fissionSFR = new planner.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                    ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = getInt("fission_cooling_efficiency_leniency");
                    ncpf.configuration.overhaul.fissionSFR.minSize = getInt("fission_min_size");
                    ncpf.configuration.overhaul.fissionSFR.maxSize = getInt("fission_max_size");
                    ncpf.configuration.overhaul.fissionSFR.neutronReach = getInt("fission_neutron_reach");
                    double fuelTimeMult = getDouble("fission_fuel_time_multiplier");
                    double[] sparsity = getDoubles("fission_sparsity_penalty_params");
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = (float) sparsity[0];
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = (float) sparsity[1];
                    double[] sourceEfficiency = getDoubles("fission_source_efficiency");
                    ncpf.configuration.overhaul.fissionSFR.sources.add(new planner.configuration.overhaul.fissionsfr.Source("Ra-Be", (float) sourceEfficiency[0]));
                    ncpf.configuration.overhaul.fissionSFR.sources.add(new planner.configuration.overhaul.fissionsfr.Source("Po-Be", (float) sourceEfficiency[1]));
                    ncpf.configuration.overhaul.fissionSFR.sources.add(new planner.configuration.overhaul.fissionsfr.Source("Cf-252", (float) sourceEfficiency[2]));
                    int[] coolingRates = getInts("fission_sink_cooling_rate");
                    String[] rules = getStrings("fission_sink_rule");
                    planner.configuration.overhaul.fissionsfr.Block water = planner.configuration.overhaul.fissionsfr.Block.heatsink("Water Heat Sink", coolingRates[0], "overhaul/water.png");
                    planner.configuration.overhaul.fissionsfr.Block iron = planner.configuration.overhaul.fissionsfr.Block.heatsink("Iron Heat Sink", coolingRates[1], "overhaul/iron.png");
                    planner.configuration.overhaul.fissionsfr.Block redstone = planner.configuration.overhaul.fissionsfr.Block.heatsink("Redstone Heat Sink", coolingRates[2], "overhaul/redstone.png");
                    planner.configuration.overhaul.fissionsfr.Block quartz = planner.configuration.overhaul.fissionsfr.Block.heatsink("Quartz Heat Sink", coolingRates[3], "overhaul/quartz.png");
                    planner.configuration.overhaul.fissionsfr.Block obsidian = planner.configuration.overhaul.fissionsfr.Block.heatsink("Obsidian Heat Sink", coolingRates[4], "overhaul/obsidian.png");
                    planner.configuration.overhaul.fissionsfr.Block netherBrick = planner.configuration.overhaul.fissionsfr.Block.heatsink("Nether Brick Heat Sink", coolingRates[5], "overhaul/netherBrick.png");
                    planner.configuration.overhaul.fissionsfr.Block glowstone = planner.configuration.overhaul.fissionsfr.Block.heatsink("Glowstone Heat Sink", coolingRates[6], "overhaul/glowstone.png");
                    planner.configuration.overhaul.fissionsfr.Block lapis = planner.configuration.overhaul.fissionsfr.Block.heatsink("Lapis Heat Sink", coolingRates[7], "overhaul/lapis.png");
                    planner.configuration.overhaul.fissionsfr.Block gold = planner.configuration.overhaul.fissionsfr.Block.heatsink("Gold Heat Sink", coolingRates[8], "overhaul/gold.png");
                    planner.configuration.overhaul.fissionsfr.Block prismarine = planner.configuration.overhaul.fissionsfr.Block.heatsink("Prismarine Heat Sink", coolingRates[9], "overhaul/prismarine.png");
                    planner.configuration.overhaul.fissionsfr.Block slime = planner.configuration.overhaul.fissionsfr.Block.heatsink("Slime Heat Sink", coolingRates[10], "overhaul/slime.png");
                    planner.configuration.overhaul.fissionsfr.Block endStone = planner.configuration.overhaul.fissionsfr.Block.heatsink("End Stone Heat Sink", coolingRates[11], "overhaul/endStone.png");
                    planner.configuration.overhaul.fissionsfr.Block purpur = planner.configuration.overhaul.fissionsfr.Block.heatsink("Purpur Heat Sink", coolingRates[12], "overhaul/purpur.png");
                    planner.configuration.overhaul.fissionsfr.Block diamond = planner.configuration.overhaul.fissionsfr.Block.heatsink("Diamond Heat Sink", coolingRates[13], "overhaul/diamond.png");
                    planner.configuration.overhaul.fissionsfr.Block emerald = planner.configuration.overhaul.fissionsfr.Block.heatsink("Emerald Heat Sink", coolingRates[14], "overhaul/emerald.png");
                    planner.configuration.overhaul.fissionsfr.Block copper = planner.configuration.overhaul.fissionsfr.Block.heatsink("Copper Heat Sink", coolingRates[15], "overhaul/copper.png");
                    planner.configuration.overhaul.fissionsfr.Block tin = planner.configuration.overhaul.fissionsfr.Block.heatsink("Tin Heat Sink", coolingRates[16], "overhaul/tin.png");
                    planner.configuration.overhaul.fissionsfr.Block lead = planner.configuration.overhaul.fissionsfr.Block.heatsink("Lead Heat Sink", coolingRates[17], "overhaul/lead.png");
                    planner.configuration.overhaul.fissionsfr.Block boron = planner.configuration.overhaul.fissionsfr.Block.heatsink("Boron Heat Sink", coolingRates[18], "overhaul/boron.png");
                    planner.configuration.overhaul.fissionsfr.Block lithium = planner.configuration.overhaul.fissionsfr.Block.heatsink("Lithium Heat Sink", coolingRates[19], "overhaul/lithium.png");
                    planner.configuration.overhaul.fissionsfr.Block magnesium = planner.configuration.overhaul.fissionsfr.Block.heatsink("Magnesium Heat Sink", coolingRates[20], "overhaul/magnesium.png");
                    planner.configuration.overhaul.fissionsfr.Block manganese = planner.configuration.overhaul.fissionsfr.Block.heatsink("Manganese Heat Sink", coolingRates[21], "overhaul/manganese.png");
                    planner.configuration.overhaul.fissionsfr.Block aluminum = planner.configuration.overhaul.fissionsfr.Block.heatsink("Aluminum Heat Sink", coolingRates[22], "overhaul/aluminum.png");
                    planner.configuration.overhaul.fissionsfr.Block silver = planner.configuration.overhaul.fissionsfr.Block.heatsink("Silver Heat Sink", coolingRates[23], "overhaul/silver.png");
                    planner.configuration.overhaul.fissionsfr.Block fluorite = planner.configuration.overhaul.fissionsfr.Block.heatsink("Fluorite Heat Sink", coolingRates[24], "overhaul/fluorite.png");
                    planner.configuration.overhaul.fissionsfr.Block villiaumite = planner.configuration.overhaul.fissionsfr.Block.heatsink("Villiaumite Heat Sink", coolingRates[25], "overhaul/villiaumite.png");
                    planner.configuration.overhaul.fissionsfr.Block carobbiite = planner.configuration.overhaul.fissionsfr.Block.heatsink("Carobbiite Heat Sink", coolingRates[26], "overhaul/carobbiite.png");
                    planner.configuration.overhaul.fissionsfr.Block arsenic = planner.configuration.overhaul.fissionsfr.Block.heatsink("Arsenic Heat Sink", coolingRates[27], "overhaul/arsenic.png");
                    planner.configuration.overhaul.fissionsfr.Block nitrogen = planner.configuration.overhaul.fissionsfr.Block.heatsink("Liquid Nitrogen Heat Sink", coolingRates[28], "overhaul/nitrogen.png");
                    planner.configuration.overhaul.fissionsfr.Block helium = planner.configuration.overhaul.fissionsfr.Block.heatsink("Liquid Helium Heat Sink", coolingRates[29], "overhaul/helium.png");
                    planner.configuration.overhaul.fissionsfr.Block enderium = planner.configuration.overhaul.fissionsfr.Block.heatsink("Enderium Heat Sink", coolingRates[30], "overhaul/enderium.png");
                    planner.configuration.overhaul.fissionsfr.Block cryotheum = planner.configuration.overhaul.fissionsfr.Block.heatsink("Cryotheum Heat Sink", coolingRates[31], "overhaul/cryotheum.png");
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.cell("Fuel Cell", "overhaul/cell.png"));
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.irradiator("Neutron Irradiator", "overhaul/irradiator.png"));
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(water);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(iron);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(redstone);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(quartz);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(obsidian);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(netherBrick);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(glowstone);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(lapis);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(gold);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(prismarine);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(slime);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(endStone);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(purpur);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(diamond);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(emerald);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(copper);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(tin);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(lead);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(boron);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(lithium);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(magnesium);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(manganese);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(aluminum);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(silver);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(fluorite);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(villiaumite);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(carobbiite);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(arsenic);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(nitrogen);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(helium);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(enderium);
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(cryotheum);
                    water.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[0]));
                    iron.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[1]));
                    redstone.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[2]));
                    quartz.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[3]));
                    obsidian.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[4]));
                    netherBrick.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[5]));
                    glowstone.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[6]));
                    lapis.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[7]));
                    gold.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[8]));
                    prismarine.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[9]));
                    slime.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[10]));
                    endStone.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[11]));
                    purpur.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[12]));
                    diamond.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[13]));
                    emerald.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[14]));
                    copper.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[15]));
                    tin.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[16]));
                    lead.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[17]));
                    boron.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[18]));
                    lithium.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[19]));
                    magnesium.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[20]));
                    manganese.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[21]));
                    aluminum.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[22]));
                    silver.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[23]));
                    fluorite.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[24]));
                    villiaumite.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[25]));
                    carobbiite.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[26]));
                    arsenic.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[27]));
                    nitrogen.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[28]));
                    helium.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[29]));
                    enderium.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[30]));
                    cryotheum.rules.add(planner.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[31]));
                    int[] fluxFac = getInts("fission_moderator_flux_factor");
                    double[] modEff = getDoubles("fission_moderator_efficiency");
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.moderator("Graphite Moderator", "overhaul/graphite.png", fluxFac[0], (float) modEff[0]));
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.moderator("Beryllium Moderator", "overhaul/beryllium.png", fluxFac[1], (float) modEff[1]));
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.moderator("Heavy Water Moderator", "overhaul/heavy water.png", fluxFac[2], (float) modEff[2]));
                    double[] refEff = getDoubles("fission_reflector_efficiency");
                    double[] refRef = getDoubles("fission_reflector_reflectivity");
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.reflector("Berrylium-Carbon Reflector", "overhaul/beryllium-carbon", (float) refEff[0], (float) refRef[0]));
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.reflector("Lead-Steel Reflector", "overhaul/lead-steel", (float) refEff[1], (float) refRef[1]));
                    double[] shieldHeat = getDoubles("fission_shield_heat_per_flux");
                    double[] shieldEff = getDoubles("fission_shield_efficiency");
                    ncpf.configuration.overhaul.fissionSFR.blocks.add(planner.configuration.overhaul.fissionsfr.Block.shield("Boron-Silver Neutron Shield", "overhaul/boron-silver", (int) shieldHeat[0], (float) shieldEff[0]));
                    double[] irrHeat = getDoubles("fission_irradiator_heat_per_flux");
                    double[] irrEff = getDoubles("fission_irradiator_efficiency");
                    ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(new planner.configuration.overhaul.fissionsfr.IrradiatorRecipe("Recipe 1", (float)irrEff[0], (float)irrHeat[0]));
                    ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(new planner.configuration.overhaul.fissionsfr.IrradiatorRecipe("Recipe 2", (float)irrEff[1], (float)irrHeat[1]));
                    ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(new planner.configuration.overhaul.fissionsfr.IrradiatorRecipe("Recipe 3", (float)irrEff[2], (float)irrHeat[2]));
                    addFuels(ncpf, fuelTimeMult, "thorium", null, "TBU Oxide", "TBU Nitride", "TBU-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "uranium", null, "LEU-233 Oxide", "LEU-233 Nitride", "LEU-233-Zirconium Alloy", null, null, "HEU-233 Oxide", "HEU-233 Nitride", "HEU-233-Zirconium Alloy", null, null, "LEU-235 Oxide", "LEU-235 Nitride", "LEU-235-Zirconium Alloy", null, null, "HEU-235 Oxide", "HEU-235 Nitride", "HEU-235-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "neptunium", null, "LEN-236 Oxide", "LEN-236 Nitride", "LEN-236-Zirconium Alloy", null, null, "HEN-236 Oxide", "HEN-236 Nitride", "HEN-236-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "plutonium", null, "LEP-239 Oxide", "LEP-239 Nitride", "LEP-239-Zirconium Alloy", null, null, "HEP-239 Oxide", "HEP-239 Nitride", "HEP-239-Zirconium Alloy", null, null, "LEP-241 Oxide", "LEP-241 Nitride", "LEP-241-Zirconium Alloy", null, null, "HEP-241 Oxide", "HEP-241 Nitride", "HEP-241-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "mixed", null, "MOX-239", "MNI-239", "MZA-239", null, null, "MOX-241", "MNI-241", "MZA-241", null);
                    addFuels(ncpf, fuelTimeMult, "americium", null, "LEA-242 Oxide", "LEA-242 Nitride", "LEA-242-Zirconium Alloy", null, null, "HEA-242 Oxide", "HEA-242 Nitride", "HEA-242-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "curium", null, "LECm-243 Oxide", "LECm-243 Nitride", "LECm-243-Zirconium Alloy", null, null, "HECm-243 Oxide", "HECm-243 Nitride", "HECm-243-Zirconium Alloy", null, null, "LECm-245 Oxide", "LECm-245 Nitride", "LECm-245-Zirconium Alloy", null, null, "HECm-245 Oxide", "HECm-245 Nitride", "HECm-245-Zirconium Alloy", null, null, "LECm-247 Oxide", "LECm-247 Nitride", "LECm-247-Zirconium Alloy", null, null, "HECm-247 Oxide", "HECm-247 Nitride", "HECm-247-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "berkelium", null, "LEB-248 Oxide", "LEB-248 Nitride", "LEB-248-Zirconium Alloy", null, null, "HEB-248 Oxide", "HEB-248 Nitride", "HEB-248-Zirconium Alloy", null);
                    addFuels(ncpf, fuelTimeMult, "californium", null, "LECf-249 Oxide", "LECf-249 Nitride", "LECf-249-Zirconium Alloy", null, null, "HECf-249 Oxide", "HECf-249 Nitride", "HECf-249-Zirconium Alloy", null, null, "LECf-251 Oxide", "LECf-251 Nitride", "LECf-251-Zirconium Alloy", null, null, "HECf-251 Oxide", "HECf-251 Nitride", "HECf-251-Zirconium Alloy", null);
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private void addFuels(NCPFFile ncpf, double timeMult, String baseName, String... fuelNames){
                int[] time = getInts("fission_"+baseName+"_fuel_time");
                int[] heat = getInts("fission_"+baseName+"_heat_generation");
                double[] efficiency = getDoubles("fission_"+baseName+"_efficiency");
                int[] criticality = getInts("fission_"+baseName+"_criticality");
                boolean[] selfPriming = getBooleans("fission_"+baseName+"_self_priming");
                for(int i = 0; i<fuelNames.length; i++){
                    if(fuelNames[i]==null)continue;
                    ncpf.configuration.overhaul.fissionSFR.fuels.add(new planner.configuration.overhaul.fissionsfr.Fuel(fuelNames[i], (float)efficiency[i], heat[i], (int)(time[i]*timeMult), criticality[i], selfPriming[i]));
                }
            }
            private double getDouble(String name){
                String str = s.substring(s.indexOf("D:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Double.parseDouble(str);
            }
            private int getInt(String name){
                String str = s.substring(s.indexOf("I:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Integer.parseInt(str);
            }
            private boolean getBoolean(String name){
                String str = s.substring(s.indexOf("B:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Boolean.parseBoolean(str);
            }
            private double[] getDoubles(String name){
                ArrayList<Double> doubles = new ArrayList<>();
                String str = s.substring(s.indexOf("D:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    doubles.add(Double.parseDouble(st.trim()));
                }
                double[] ret = new double[doubles.size()];
                for(int i = 0; i<doubles.size(); i++){
                    ret[i] = doubles.get(i);
                }
                return ret;
            }
            private int[] getDoublesAsInts(String name){
                double[] ds = getDoubles(name);
                int[] is = new int[ds.length];
                for(int i = 0; i<is.length; i++){
                    is[i] = (int)ds[i];
                }
                return is;
            }
            private int[] getInts(String name){
                ArrayList<Integer> ints = new ArrayList<>();
                String str = s.substring(s.indexOf("I:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    ints.add(Integer.parseInt(st.trim()));
                }
                int[] ret = new int[ints.size()];
                for(int i = 0; i<ints.size(); i++){
                    ret[i] = ints.get(i);
                }
                return ret;
            }
            private boolean[] getBooleans(String name){
                ArrayList<Boolean> booleans = new ArrayList<>();
                String str = s.substring(s.indexOf("B:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    booleans.add(Boolean.parseBoolean(st.trim()));
                }
                boolean[] ret = new boolean[booleans.size()];
                for(int i = 0; i<booleans.size(); i++){
                    ret[i] = booleans.get(i);
                }
                return ret;
            }
            private String[] getStrings(String name){
                ArrayList<String> strings = new ArrayList<>();
                String str = s.substring(s.indexOf("S:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    strings.add(st.trim());
                }
                String[] ret = new String[strings.size()];
                for(int i = 0; i<strings.size(); i++){
                    ret[i] = strings.get(i);
                }
                return ret;
            }
        });// OVERHAUL nuclearcraft.cfg
    }
    public static NCPFFile read(File file){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(file))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(file);
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
}