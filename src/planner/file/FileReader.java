package planner.file;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                }catch(Exception ex){
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
                            ConfigList blocks = fissionSFR.get("blocks");
                            underhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                planner.configuration.underhaul.fissionsfr.Block block = new planner.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
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
        });
    }
    public static NCPFFile read(File file){
        for(FormatReader reader : formats){
            if(reader.formatMatches(file)){
                return reader.read(file);
            }
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
}