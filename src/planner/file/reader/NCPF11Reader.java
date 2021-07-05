package planner.file.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import multiblock.Multiblock;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockTemplate;
import multiblock.configuration.IBlockType;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.Core;
import planner.exception.MissingConfigurationEntryException;
import planner.file.FormatReader;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
import simplelibrary.image.Image;
public class NCPF11Reader implements FormatReader {
    @Override
    public boolean formatMatches(InputStream in){
        try{
            Config header = Config.newConfig();
            header.load(in);
            in.close();
            byte version = header.get("version", (byte)0);
            return version == getTargetVersion();
        } catch(Throwable t){
            return false;
        }
    }

    HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Integer> underhaulPostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Integer> overhaulSFRPostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Integer> overhaulMSRPostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.turbine.PlacementRule, Integer> overhaulTurbinePostLoadMap = new HashMap<>();
    HashMap<multiblock.configuration.overhaul.fusion.PlacementRule, Integer> overhaulFusionPostLoadMap = new HashMap<>();
    HashMap<OverhaulTurbine, ArrayList<Integer>> overhaulTurbinePostLoadInputsMap = new HashMap<>();

    protected byte getTargetVersion() {
        return (byte) 11;
    }

    @Override
    public synchronized NCPFFile read(InputStream in){
        overhaulTurbinePostLoadInputsMap.clear();
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
            ncpf.configuration = loadConfiguration(config);
            for(int i = 0; i<multiblocks; i++){
                ncpf.multiblocks.add(readMultiblock(ncpf, in));
            }
            for(OverhaulTurbine turbine : overhaulTurbinePostLoadInputsMap.keySet()){
                for(int i : overhaulTurbinePostLoadInputsMap.get(turbine)){
                    turbine.inputs.add(ncpf.multiblocks.get(i));
                }
            }
            in.close();
            return ncpf;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

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
            case 3:
                multiblock = readMultiblockOverhaulTurbine(ncpf, data);
                break;
            case 4:
                multiblock = readMultiblockOverhaulFusionReactor(ncpf, data);
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

    //<editor-fold defaultstate="collapsed" desc="Underhaul SFR - read multiblock">
    protected synchronized Multiblock readMultiblockUnderhaulSFR(NCPFFile ncpf, Config data) {
        ConfigNumberList dimensions = data.get("dimensions");
        Fuel f = ncpf.configuration.underhaul.fissionSFR.allFuels.get(0);
        try{
            f = ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", -1));
        }catch(IndexOutOfBoundsException ex){
            if(Core.recoveryMode){
                try{
                    f = Core.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", -1));
                }catch(IndexOutOfBoundsException exc){}
            }else throw new RuntimeException(ex);
        }
        UnderhaulSFR underhaulSFR = new UnderhaulSFR(ncpf.configuration, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2),f);
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            underhaulSFR.forEachPosition((x, y, z) -> {
                int bid = (int) blocks.get(index[0]);
                if(bid>0){
                    multiblock.configuration.underhaul.fissionsfr.Block b = null;
                    try{
                        b = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                b = Core.configuration.underhaul.fissionSFR.allBlocks.get(bid-1);
                            }catch(IndexOutOfBoundsException exc){}
                        }else throw new RuntimeException(ex);
                    }
                    if(b!=null)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(ncpf.configuration, x, y, z, b));
                }
                index[0]++;
            });
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                multiblock.configuration.underhaul.fissionsfr.Block b = null;
                try{
                    b = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1);
                }catch(IndexOutOfBoundsException ex){
                    if(Core.recoveryMode){
                        try{
                            b = Core.configuration.underhaul.fissionSFR.allBlocks.get(bid-1);
                        }catch(IndexOutOfBoundsException exc){}
                    }else throw new RuntimeException(ex);
                }
                if(b!=null)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(ncpf.configuration, x, y, z, b));
            }
        }
        return underhaulSFR;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul SFR - read multiblock">
    protected synchronized Multiblock readMultiblockOverhaulSFR(NCPFFile ncpf, Config data) {
        ConfigNumberList dimensions = data.get("dimensions");
        multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolantRecipe = ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(0);
        try{
            coolantRecipe = ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", -1));
        }catch(IndexOutOfBoundsException ex){
            if(Core.recoveryMode){
                try{
                    coolantRecipe = Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", -1));
                }catch(IndexOutOfBoundsException exc){}
            }else throw new RuntimeException(ex);
        }
        OverhaulSFR overhaulSFR = new OverhaulSFR(ncpf.configuration, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2),coolantRecipe);
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            overhaulSFR.forEachPosition((x, y, z) -> {
                int bid = (int) blocks.get(index[0]);
                if(bid>0){
                    multiblock.configuration.overhaul.fissionsfr.Block b = null;
                    try{
                        b = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                b = Core.configuration.overhaul.fissionSFR.allBlocks.get(bid-1);
                            }catch(IndexOutOfBoundsException exc){}
                        }else throw new RuntimeException(ex);
                    }
                    if(b!=null)overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(ncpf.configuration, x, y, z, b));
                }
                index[0]++;
            });
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                multiblock.configuration.overhaul.fissionsfr.Block b = null;
                try{
                    b = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1);
                }catch(IndexOutOfBoundsException ex){
                    if(Core.recoveryMode){
                        try{
                            b = Core.configuration.overhaul.fissionSFR.allBlocks.get(bid-1);
                        }catch(IndexOutOfBoundsException exc){}
                    }else throw new RuntimeException(ex);
                }
                if(b!=null)overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(ncpf.configuration, x, y, z, b));
            }
        }
        ConfigNumberList blockRecipes = data.get("blockRecipes");
        int recipeIndex = 0;
        for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
            multiblock.configuration.overhaul.fissionsfr.Block templ = block.template.parent==null?block.template:block.template.parent;
            if(!templ.allRecipes.isEmpty()){
                int rid = (int)blockRecipes.get(recipeIndex);
                if(rid!=0){
                    try{
                        block.recipe = templ.allRecipes.get(rid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                block.recipe = Core.configuration.overhaul.fissionSFR.convert(templ).allRecipes.get(rid-1);
                            }catch(IndexOutOfBoundsException | MissingConfigurationEntryException exc){}
                        }else throw new RuntimeException(ex);
                    }
                }
                recipeIndex++;
            }
        }
        ConfigNumberList ports = data.get("ports");
        int portIndex = 0;
        for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
            if(block.template.parent!=null||block.template.coolantVent){
                block.isToggled = ports.get(portIndex)>0;
                portIndex++;
            }
        }
        return overhaulSFR;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul MSR - read multiblock">
    protected synchronized Multiblock readMultiblockOverhaulMSR(NCPFFile ncpf, Config data) {
        ConfigNumberList dimensions = data.get("dimensions");
        OverhaulMSR overhaulMSR = new OverhaulMSR(ncpf.configuration, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            overhaulMSR.forEachPosition((x, y, z) -> {
                int bid = (int) blocks.get(index[0]);
                if(bid>0){
                    multiblock.configuration.overhaul.fissionmsr.Block b = null;
                    try{
                        b = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                b = Core.configuration.overhaul.fissionMSR.allBlocks.get(bid-1);
                            }catch(IndexOutOfBoundsException exc){}
                        }else throw new RuntimeException(ex);
                    }
                    if(b!=null)overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(ncpf.configuration, x, y, z, b));
                }
                index[0]++;
            });
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                multiblock.configuration.overhaul.fissionmsr.Block b = null;
                try{
                    b = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1);
                }catch(IndexOutOfBoundsException ex){
                    if(Core.recoveryMode){
                        try{
                            b = Core.configuration.overhaul.fissionMSR.allBlocks.get(bid-1);
                        }catch(IndexOutOfBoundsException exc){}
                    }else throw new RuntimeException(ex);
                }
                if(b!=null)overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(ncpf.configuration, x, y, z, b));
            }
        }
        ConfigNumberList blockRecipes = data.get("blockRecipes");
        int recipeIndex = 0;
        for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
            multiblock.configuration.overhaul.fissionmsr.Block templ = block.template.parent==null?block.template:block.template.parent;
            if(!templ.allRecipes.isEmpty()){
                int rid = (int)blockRecipes.get(recipeIndex);
                if(rid!=0){
                    try{
                        block.recipe = templ.allRecipes.get(rid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                block.recipe = Core.configuration.overhaul.fissionMSR.convert(templ).allRecipes.get(rid-1);
                            }catch(IndexOutOfBoundsException | MissingConfigurationEntryException exc){}
                        }else throw new RuntimeException(ex);
                    }
                }
                recipeIndex++;
            }
        }
        ConfigNumberList ports = data.get("ports");
        int portIndex = 0;
        for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
            if(block.template.parent!=null){
                block.isToggled = ports.get(portIndex)>0;
                portIndex++;
            }
        }
        return overhaulMSR;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine - read multiblock">
    protected synchronized Multiblock readMultiblockOverhaulTurbine(NCPFFile ncpf, Config data) {
        ConfigNumberList dimensions = data.get("dimensions");
        multiblock.configuration.overhaul.turbine.Recipe turbineRecipe = ncpf.configuration.overhaul.turbine.allRecipes.get(0);
        try{
            turbineRecipe = ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", -1));
        }catch(IndexOutOfBoundsException ex){
            if(Core.recoveryMode){
                try{
                    turbineRecipe = Core.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", -1));
                }catch(IndexOutOfBoundsException exc){}
            }else throw new RuntimeException(ex);
        }
        OverhaulTurbine overhaulTurbine = new OverhaulTurbine(ncpf.configuration, (int)dimensions.get(0), (int)dimensions.get(2), turbineRecipe);
        if(data.hasProperty("inputs")){
            overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
            ConfigNumberList inputs = data.get("inputs");
            for(Number number : inputs.iterable()){
                overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
            }
        }
        ConfigNumberList blocks = data.get("blocks");
        int[] index = new int[1];
        overhaulTurbine.forEachPosition((x, y, z) -> {
            int bid = (int) blocks.get(index[0]);
            if(bid>0){
                multiblock.configuration.overhaul.turbine.Block b = null;
                try{
                    b = ncpf.configuration.overhaul.turbine.allBlocks.get(bid-1);
                }catch(IndexOutOfBoundsException ex){
                    if(Core.recoveryMode){
                        try{
                            b = Core.configuration.overhaul.turbine.allBlocks.get(bid-1);
                        }catch(IndexOutOfBoundsException exc){}
                    }else throw new RuntimeException(ex);
                }
                if(b!=null)overhaulTurbine.setBlockExact(x, y, z, new multiblock.overhaul.turbine.Block(ncpf.configuration, x, y, z, b));
            }
            index[0]++;
        });
        return overhaulTurbine;
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Fusion Reactor - read multiblock">
    protected synchronized Multiblock readMultiblockOverhaulFusionReactor(NCPFFile ncpf, Config data) {
        ConfigNumberList dimensions = data.get("dimensions");
        multiblock.configuration.overhaul.fusion.CoolantRecipe fusionCoolantRecipe = ncpf.configuration.overhaul.fusion.allCoolantRecipes.get(0);
        try{
            fusionCoolantRecipe = ncpf.configuration.overhaul.fusion.allCoolantRecipes.get(data.get("coolantRecipe", -1));
        }catch(IndexOutOfBoundsException ex){
            if(Core.recoveryMode){
                try{
                    fusionCoolantRecipe = Core.configuration.overhaul.fusion.allCoolantRecipes.get(data.get("coolantRecipe", -1));
                }catch(IndexOutOfBoundsException exc){}
            }else throw new RuntimeException(ex);
        }

        multiblock.configuration.overhaul.fusion.Recipe fusionRecipe = ncpf.configuration.overhaul.fusion.allRecipes.get(0);
        try{
            fusionRecipe = ncpf.configuration.overhaul.fusion.allRecipes.get(data.get("recipe", -1));
        }catch(IndexOutOfBoundsException ex){
            if(Core.recoveryMode){
                try{
                    fusionRecipe = Core.configuration.overhaul.fusion.allRecipes.get(data.get("recipe", -1));
                }catch(IndexOutOfBoundsException exc){}
            }else throw new RuntimeException(ex);
        }
        OverhaulFusionReactor overhaulFusionReactor = new OverhaulFusionReactor(ncpf.configuration, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2),(int)dimensions.get(3),fusionRecipe,fusionCoolantRecipe);
        ConfigNumberList blocks = data.get("blocks");
        int[] findex = new int[0];
        overhaulFusionReactor.forEachPosition((X, Y, Z) -> {
            int bid = (int) blocks.get(findex[0]);
            if(bid>0){
                multiblock.configuration.overhaul.fusion.Block b = null;
                try{
                    b = ncpf.configuration.overhaul.fusion.allBlocks.get(bid-1);
                }catch(IndexOutOfBoundsException ex){
                    if(Core.recoveryMode){
                        try{
                            b = Core.configuration.overhaul.fusion.allBlocks.get(bid-1);
                        }catch(IndexOutOfBoundsException exc){}
                    }else throw new RuntimeException(ex);
                }
                if(b!=null)overhaulFusionReactor.setBlockExact(X, Y, Z, new multiblock.overhaul.fusion.Block(ncpf.configuration, X, Y, Z, b));
            }
            findex[0]++;
        });
        ConfigNumberList blockRecipes = data.get("blockRecipes");
        int recipeIndex = 0;
        for(multiblock.overhaul.fusion.Block block : overhaulFusionReactor.getBlocks()){
            if(!block.template.allRecipes.isEmpty()){
                int rid = (int)blockRecipes.get(recipeIndex);
                if(rid!=0){
                    try{
                        block.recipe = block.template.allRecipes.get(rid-1);
                    }catch(IndexOutOfBoundsException ex){
                        if(Core.recoveryMode){
                            try{
                                block.recipe = Core.configuration.overhaul.fusion.convert(block.template).allRecipes.get(rid-1);
                            }catch(IndexOutOfBoundsException | MissingConfigurationEntryException exc){}
                        }else throw new RuntimeException(ex);
                    }
                }
                recipeIndex++;
            }
        }
        return overhaulFusionReactor;
    }
    //</editor-fold>

    protected <Rule extends AbstractPlacementRule<BlockType, Template>,
            BlockType extends IBlockType,
            Template extends IBlockTemplate> void readRuleBlock(HashMap<Rule, Integer> postMap, Rule rule, Config ruleCfg) {
        if (ruleCfg.hasProperty("blockIdx")) {
            rule.isSpecificBlock = true;
            postMap.put(rule, ruleCfg.getInt("blockIdx"));
        } else {
            rule.isSpecificBlock = false;
            rule.blockType = rule.loadBlockType(ruleCfg.getByte("blockType"));
        }
    }
    protected <Rule extends AbstractPlacementRule<BlockType, Template>,
            BlockType extends IBlockType,
            Template extends IBlockTemplate> Rule readGenericRule(HashMap<Rule, Integer> postMap, Rule rule, Config ruleCfg){
        byte type = ruleCfg.get("type");
        rule.ruleType = AbstractPlacementRule.RuleType.values()[type];
        switch(rule.ruleType){
            case BETWEEN:
            case AXIAL:
                readRuleBlock(postMap, rule, ruleCfg);
                rule.min = ruleCfg.get("min");
                rule.max = ruleCfg.get("max");
                break;
            case VERTEX:
            case EDGE:
                readRuleBlock(postMap, rule, ruleCfg);
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

    protected multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg) {
        return readGenericRule(underhaulPostLoadMap, new multiblock.configuration.underhaul.fissionsfr.PlacementRule(), ruleCfg);
    }
    protected multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
        return readGenericRule(overhaulSFRPostLoadMap, new multiblock.configuration.overhaul.fissionsfr.PlacementRule(), ruleCfg);
    }
    protected multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
        return readGenericRule(overhaulMSRPostLoadMap, new multiblock.configuration.overhaul.fissionmsr.PlacementRule(), ruleCfg);
    }
    protected multiblock.configuration.overhaul.turbine.PlacementRule readOverTurbineRule(Config ruleCfg){
        return readGenericRule(overhaulTurbinePostLoadMap, new multiblock.configuration.overhaul.turbine.PlacementRule(), ruleCfg);
    }
    protected multiblock.configuration.overhaul.fusion.PlacementRule readOverFusionRule(Config ruleCfg) {
        return readGenericRule(overhaulFusionPostLoadMap, new multiblock.configuration.overhaul.fusion.PlacementRule(), ruleCfg);
    }

    protected Configuration loadConfiguration(Config config){
        boolean partial = config.get("partial");
        Configuration configuration;
        if(partial)configuration = new PartialConfiguration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
        else configuration = new Configuration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
        configuration.addon = config.get("addon");
        loadUnderhaulBlocks(config, configuration, configuration, !partial&&!configuration.addon);
        if(config.hasProperty("overhaul")){
            configuration.overhaul = new OverhaulConfiguration();
            Config overhaul = config.get("overhaul");
            loadOverhaulSFRBlocks(overhaul, configuration, configuration, !partial&&!configuration.addon, false);
            loadOverhaulMSRBlocks(overhaul, configuration, configuration, !partial&&!configuration.addon, false);
            loadOverhaulTurbineBlocks(overhaul, configuration, configuration, !partial&&!configuration.addon);
            loadOverhaulFusionGeneratorBlocks(overhaul, configuration, !partial&&!configuration.addon);
        }
        if(config.hasProperty("addons")){
            ConfigList addons = config.get("addons");
            for(int i = 0; i<addons.size(); i++){
                configuration.addons.add(loadAddon(configuration, addons.get(i)));
            }
        }
        return configuration;
    }
    protected Configuration loadAddon(Configuration parent, Config config){
        boolean partial = config.get("partial");
        Configuration configuration;
        if(partial)configuration = new PartialConfiguration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
        else configuration = new Configuration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
        configuration.addon = config.get("addon");
        loadUnderhaulBlocks(config, parent, configuration, false);
        if(config.hasProperty("overhaul")){
            configuration.overhaul = new OverhaulConfiguration();
            Config overhaul = config.get("overhaul");
            loadOverhaulSFRBlocks(overhaul, parent, configuration, false, true);
            loadOverhaulMSRBlocks(overhaul, parent, configuration, false, true);
            loadOverhaulTurbineBlocks(overhaul, parent, configuration, false);
            // TODO: No support for fusion addons.
        }
        if(config.hasProperty("addons")){
            ConfigList addons = config.get("addons");
            for(int i = 0; i<addons.size(); i++){
                configuration.addons.add(loadAddon(configuration, addons.get(i)));
            }
        }
        return configuration;
    }

    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration - load blocks">
    protected void loadUnderhaulBlocks(Config config, Configuration parent, Configuration configuration, boolean loadSettings) {
        if(config.hasProperty("underhaul")){
            configuration.underhaul = new UnderhaulConfiguration();
            Config underhaul = config.get("underhaul");
            if(underhaul.hasProperty("fissionSFR")){
                configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                Config fissionSFR = underhaul.get("fissionSFR");
                ConfigList blocks = fissionSFR.get("blocks");
                if(loadSettings){
                    configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                    configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                    configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                    configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                    configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                    configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                }
                underhaulPostLoadMap.clear();
                for(Iterator bit = blocks.iterator(); bit.hasNext();){
                    Config blockCfg = (Config)bit.next();
                    multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                    block.displayName = blockCfg.get("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int i = 0; i<names.size(); i++){
                            block.legacyNames.add(names.get(i));
                        }
                    }
                    block.active = blockCfg.get("active");
                    block.cooling = blockCfg.get("cooling", 0);
                    block.fuelCell = blockCfg.get("fuelCell", false);
                    block.moderator = blockCfg.get("moderator", false);
                    block.casing = blockCfg.get("casing", false);
                    block.controller = blockCfg.get("controller", false);
                    if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config ruleCfg = (Config)rit.next();
                            block.rules.add(readUnderRule(ruleCfg));
                        }
                    }
                    parent.underhaul.fissionSFR.allBlocks.add(block);configuration.underhaul.fissionSFR.blocks.add(block);
                }
                for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                    int index = underhaulPostLoadMap.get(rule);
                    if(index==0){
                        rule.isSpecificBlock = false;
                        rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                    }else{
                        rule.block = parent.underhaul.fissionSFR.allBlocks.get(index-1);
                    }
                }
                ConfigList fuels = fissionSFR.get("fuels");
                for(Iterator fit = fuels.iterator(); fit.hasNext();){
                    Config fuelCfg = (Config)fit.next();
                    multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                    fuel.displayName = fuelCfg.get("displayName");
                    if(fuelCfg.hasProperty("legacyNames")){
                        ConfigList names = fuelCfg.getConfigList("legacyNames");
                        for(int i = 0; i<names.size(); i++){
                            fuel.legacyNames.add(names.get(i));
                        }
                    }
                    if(fuelCfg.hasProperty("texture"))fuel.setTexture(loadNCPFTexture(fuelCfg.get("texture")));
                    parent.underhaul.fissionSFR.allFuels.add(fuel);configuration.underhaul.fissionSFR.fuels.add(fuel);
                }
            }
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overhaul SFR - load blocks">
    protected void loadOverhaulSFRBlocks(Config overhaul, Configuration parent, Configuration configuration, boolean loadSettings, boolean loadingAddon) {
        if(overhaul.hasProperty("fissionSFR")){
            configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
            Config fissionSFR = overhaul.get("fissionSFR");
            if(loadSettings){
                configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fissionSFR.get("blocks");
            overhaulSFRPostLoadMap.clear();
            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.fissionsfr.Block originalBlock = null;
                multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
                if (loadingAddon) {
                    for (multiblock.configuration.overhaul.fissionsfr.Block b : parent.overhaul.fissionSFR.allBlocks) {
                        if (b.name.equals(block.name)) {
                            originalBlock = b;
                        }
                    }
                }
                if(originalBlock==null){
                    block.displayName = blockCfg.get("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int i = 0; i<names.size(); i++){
                            block.legacyNames.add(names.get(i));
                        }
                    }
                    block.cluster = blockCfg.get("cluster", false);
                    block.createCluster = blockCfg.get("createCluster", false);
                    block.conductor = blockCfg.get("conductor", false);
                    block.functional = blockCfg.get("functional", false);
                    block.blocksLOS = blockCfg.get("blocksLOS", false);
                    block.casing = blockCfg.get("casing", false);
                    block.casingEdge = blockCfg.get("casingEdge", false);
                    block.controller = blockCfg.get("controller", false);
                    Config coolantVentCfg = blockCfg.get("coolantVent");
                    if(coolantVentCfg!=null){
                        block.coolantVent = true;
                        if(coolantVentCfg.hasProperty("outTexture"))block.setCoolantVentOutputTexture(loadNCPFTexture(coolantVentCfg.get("outTexture")));
                        block.coolantVentOutputDisplayName = coolantVentCfg.get("outDisplayName");
                    }
                    boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                    Config fuelCellCfg = blockCfg.get("fuelCell");
                    if(fuelCellCfg!=null){
                        block.fuelCell = true;
                        block.fuelCellHasBaseStats = fuelCellCfg.get("hasBaseStats", !hasRecipes);
                        if(block.fuelCellHasBaseStats){
                            block.fuelCellEfficiency = fuelCellCfg.get("efficiency");
                            block.fuelCellHeat = fuelCellCfg.get("heat");
                            block.fuelCellCriticality = fuelCellCfg.get("criticality");
                            block.fuelCellSelfPriming = fuelCellCfg.get("selfPriming", false);
                        }
                    }
                    Config irradiatorCfg = blockCfg.get("irradiator");
                    if(irradiatorCfg!=null){
                        block.irradiator = true;
                        block.irradiatorHasBaseStats = irradiatorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.irradiatorHasBaseStats){
                            block.irradiatorEfficiency = irradiatorCfg.get("efficiency");
                            block.irradiatorHeat = irradiatorCfg.get("heat");
                        }
                    }
                    Config reflectorCfg = blockCfg.get("reflector");
                    if(reflectorCfg!=null){
                        block.reflector = true;
                        block.reflectorHasBaseStats = reflectorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.reflectorHasBaseStats){
                            block.reflectorEfficiency = reflectorCfg.get("efficiency");
                            block.reflectorReflectivity = reflectorCfg.get("reflectivity");
                        }
                    }
                    Config moderatorCfg = blockCfg.get("moderator");
                    if(moderatorCfg!=null){
                        block.moderator = true;
                        block.moderatorHasBaseStats = moderatorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.moderatorHasBaseStats){
                            block.moderatorFlux = moderatorCfg.get("flux");
                            block.moderatorEfficiency = moderatorCfg.get("efficiency");
                            block.moderatorActive = moderatorCfg.get("active", false);
                        }
                    }
                    Config shieldCfg = blockCfg.get("shield");
                    if(shieldCfg!=null){
                        block.shield = true;
                        block.shieldHasBaseStats = shieldCfg.get("hasBaseStats", !hasRecipes);
                        if(block.shieldHasBaseStats){
                            block.shieldHeat = shieldCfg.get("heat");
                            block.shieldEfficiency = shieldCfg.get("efficiency");
                            if(shieldCfg.hasProperty("closedTexture"))block.setShieldClosedTexture(loadNCPFTexture(shieldCfg.get("closedTexture")));
                        }
                    }
                    Config heatsinkCfg = blockCfg.get("heatsink");
                    if(heatsinkCfg!=null){
                        block.heatsink = true;
                        block.heatsinkHasBaseStats = heatsinkCfg.get("hasBaseStats", !hasRecipes);
                        if(block.heatsinkHasBaseStats){
                            block.heatsinkCooling = heatsinkCfg.get("cooling");
                        }
                    }
                    Config sourceCfg = blockCfg.get("source");
                    if(sourceCfg!=null){
                        block.source = true;
                        block.sourceEfficiency = sourceCfg.get("efficiency");
                    }
                    if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                    if(hasRecipes && (loadingAddon || !configuration.addon)){
                        Config portCfg = blockCfg.get("port");
                        block.port = new multiblock.configuration.overhaul.fissionsfr.Block(portCfg.get("name"));
                        block.port.parent = block;
                        block.port.displayName = portCfg.get("inputDisplayName");
                        if(portCfg.hasProperty("inputTexture"))block.port.setTexture(loadNCPFTexture(portCfg.get("inputTexture")));
                        block.port.portOutputDisplayName = portCfg.get("outputDisplayName");
                        if(portCfg.hasProperty("outputTexture"))block.port.setPortOutputTexture(loadNCPFTexture(portCfg.get("outputTexture")));
                    }
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config ruleCfg = (Config)rit.next();
                            block.rules.add(readOverSFRRule(ruleCfg));
                        }
                    }
                }else{
                    block.fuelCell = blockCfg.hasProperty("fuelCell");
                    block.irradiator = blockCfg.hasProperty("irradiator");
                    block.reflector = blockCfg.hasProperty("reflector");
                    block.moderator = blockCfg.hasProperty("moderator");
                    block.shield = blockCfg.hasProperty("shield");
                    block.heatsink = blockCfg.hasProperty("heatsink");
                }
                ConfigList recipes = blockCfg.get("recipes", new ConfigList());
                for(int i = 0; i<recipes.size(); i++){
                    Config recipeCfg = recipes.get(i);
                    Config inputCfg = recipeCfg.get("input");
                    Config outputCfg = recipeCfg.get("output");
                    multiblock.configuration.overhaul.fissionsfr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(inputCfg.get("name"), outputCfg.get("name"));
                    recipe.inputDisplayName = inputCfg.get("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipe.inputLegacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))recipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                    recipe.inputRate = inputCfg.get("rate", 0);
                    recipe.outputDisplayName = outputCfg.get("displayName");
                    if(outputCfg.hasProperty("texture"))recipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                    recipe.outputRate = outputCfg.get("rate", 0);
                    if(block.fuelCell){
                        Config recipeFuelCellCfg = recipeCfg.get("fuelCell");
                        recipe.fuelCellEfficiency = recipeFuelCellCfg.get("efficiency");
                        recipe.fuelCellHeat = recipeFuelCellCfg.get("heat");
                        recipe.fuelCellTime = recipeFuelCellCfg.get("time");
                        recipe.fuelCellCriticality = recipeFuelCellCfg.get("criticality");
                        recipe.fuelCellSelfPriming = recipeFuelCellCfg.get("selfPriming", false);
                    }
                    if(block.irradiator){
                        Config recipeIrradiatorCfg = recipeCfg.get("irradiator");
                        recipe.irradiatorEfficiency = recipeIrradiatorCfg.get("efficiency");
                        recipe.irradiatorHeat = recipeIrradiatorCfg.get("heat");
                    }
                    if(block.reflector){
                        Config recipeReflectorCfg = recipeCfg.get("reflector");
                        recipe.reflectorEfficiency = recipeReflectorCfg.get("efficiency");
                        recipe.reflectorReflectivity = recipeReflectorCfg.get("reflectivity");
                    }
                    if(block.moderator){
                        Config recipeModeratorCfg = recipeCfg.get("moderator");
                        recipe.moderatorFlux = recipeModeratorCfg.get("flux");
                        recipe.moderatorEfficiency = recipeModeratorCfg.get("efficiency");
                        recipe.moderatorActive = recipeModeratorCfg.get("active", false);
                    }
                    if(block.shield){
                        Config recipeShieldCfg = recipeCfg.get("shield");
                        recipe.shieldHeat = recipeShieldCfg.get("heat");
                        recipe.shieldEfficiency = recipeShieldCfg.get("efficiency");
                    }
                    if(block.heatsink){
                        Config recipeHeatsinkCfg = recipeCfg.get("heatsink");
                        recipe.heatsinkCooling = recipeHeatsinkCfg.get("cooling");
                    }
                    if(originalBlock!=null)originalBlock.allRecipes.add(recipe);
                    else block.allRecipes.add(recipe);
                    block.recipes.add(recipe);
                }
                if(originalBlock!=null){
                    configuration.overhaul.fissionSFR.allBlocks.add(block);
                }else{
                    parent.overhaul.fissionSFR.allBlocks.add(block);configuration.overhaul.fissionSFR.blocks.add(block);
                    if(block.port!=null){
                        parent.overhaul.fissionSFR.allBlocks.add(block.port);configuration.overhaul.fissionSFR.blocks.add(block.port);
                    }
                }
            }
            for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                int index = overhaulSFRPostLoadMap.get(rule);
                if(index==0){
                    rule.isSpecificBlock = false;
                    rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                }else{
                    rule.block = parent.overhaul.fissionSFR.allBlocks.get(index-1);
                }
            }
            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                Config coolantRecipeCfg = (Config)irit.next();
                Config inputCfg = coolantRecipeCfg.get("input");
                Config outputCfg = coolantRecipeCfg.get("output");
                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(inputCfg.get("name"), outputCfg.get("name"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.getFloat("outputRatio"));
                coolRecipe.inputDisplayName = inputCfg.get("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        coolRecipe.inputLegacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))coolRecipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                coolRecipe.outputDisplayName = outputCfg.get("displayName");
                if(outputCfg.hasProperty("texture"))coolRecipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));;
                parent.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
            }
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul MSR - load blocks">
    protected void loadOverhaulMSRBlocks(Config overhaul, Configuration parent, Configuration configuration, boolean loadSettings, boolean loadingAddon) {
        if(overhaul.hasProperty("fissionMSR")){
            configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
            Config fissionMSR = overhaul.get("fissionMSR");
            if(loadSettings){
                configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fissionMSR.get("blocks");
            overhaulMSRPostLoadMap.clear();
            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.fissionmsr.Block theBlockThatThisBlockIsAnAddonRecipeBlockFor = null;
                multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                if (loadingAddon) {
                    for (multiblock.configuration.overhaul.fissionmsr.Block b : parent.overhaul.fissionMSR.allBlocks) {
                        if (b.name.equals(block.name)) {
                            theBlockThatThisBlockIsAnAddonRecipeBlockFor = b;
                        }
                    }
                }
                if(theBlockThatThisBlockIsAnAddonRecipeBlockFor==null){
                    block.displayName = blockCfg.get("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int i = 0; i<names.size(); i++){
                            block.legacyNames.add(names.get(i));
                        }
                    }
                    block.cluster = blockCfg.get("cluster", false);
                    block.createCluster = blockCfg.get("createCluster", false);
                    block.conductor = blockCfg.get("conductor", false);
                    block.functional = blockCfg.get("functional", false);
                    block.blocksLOS = blockCfg.get("blocksLOS", false);
                    block.casing = blockCfg.get("casing", false);
                    block.casingEdge = blockCfg.get("casingEdge", false);
                    block.controller = blockCfg.get("controller", false);
                    boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                    Config fuelVesselCfg = blockCfg.get("fuelVessel");
                    if(fuelVesselCfg!=null){
                        block.fuelVessel = true;
                        block.fuelVesselHasBaseStats = fuelVesselCfg.get("hasBaseStats", !hasRecipes);
                        if(block.fuelVesselHasBaseStats){
                            block.fuelVesselEfficiency = fuelVesselCfg.get("efficiency");
                            block.fuelVesselHeat = fuelVesselCfg.get("heat");
                            block.fuelVesselCriticality = fuelVesselCfg.get("criticality");
                            block.fuelVesselSelfPriming = fuelVesselCfg.get("selfPriming", false);
                        }
                    }
                    Config irradiatorCfg = blockCfg.get("irradiator");
                    if(irradiatorCfg!=null){
                        block.irradiator = true;
                        block.irradiatorHasBaseStats = irradiatorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.irradiatorHasBaseStats){
                            block.irradiatorEfficiency = irradiatorCfg.get("efficiency");
                            block.irradiatorHeat = irradiatorCfg.get("heat");
                        }
                    }
                    Config reflectorCfg = blockCfg.get("reflector");
                    if(reflectorCfg!=null){
                        block.reflector = true;
                        block.reflectorHasBaseStats = reflectorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.reflectorHasBaseStats){
                            block.reflectorEfficiency = reflectorCfg.get("efficiency");
                            block.reflectorReflectivity = reflectorCfg.get("reflectivity");
                        }
                    }
                    Config moderatorCfg = blockCfg.get("moderator");
                    if(moderatorCfg!=null){
                        block.moderator = true;
                        block.moderatorHasBaseStats = moderatorCfg.get("hasBaseStats", !hasRecipes);
                        if(block.moderatorHasBaseStats){
                            block.moderatorFlux = moderatorCfg.get("flux");
                            block.moderatorEfficiency = moderatorCfg.get("efficiency");
                            block.moderatorActive = moderatorCfg.get("active", false);
                        }
                    }
                    Config shieldCfg = blockCfg.get("shield");
                    if(shieldCfg!=null){
                        block.shield = true;
                        block.shieldHasBaseStats = shieldCfg.get("hasBaseStats", !hasRecipes);
                        if(block.shieldHasBaseStats){
                            block.shieldHeat = shieldCfg.get("heat");
                            block.shieldEfficiency = shieldCfg.get("efficiency");
                            if(shieldCfg.hasProperty("closedTexture"))block.setShieldClosedTexture(loadNCPFTexture(shieldCfg.get("closedTexture")));
                        }
                    }
                    Config heaterCfg = blockCfg.get("heater");
                    if(heaterCfg!=null){
                        block.heater = true;
                        block.heaterHasBaseStats = heaterCfg.get("hasBaseStats", !hasRecipes);
                        if(block.heaterHasBaseStats){
                            block.heaterCooling = heaterCfg.get("cooling");
                        }
                    }
                    Config sourceCfg = blockCfg.get("source");
                    if(sourceCfg!=null){
                        block.source = true;
                        block.sourceEfficiency = sourceCfg.get("efficiency");
                    }
                    if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                    if(hasRecipes && (loadingAddon || !configuration.addon)){
                        Config portCfg = blockCfg.get("port");
                        block.port = new multiblock.configuration.overhaul.fissionmsr.Block(portCfg.get("name"));
                        block.port.parent = block;
                        block.port.displayName = portCfg.get("inputDisplayName");
                        if(portCfg.hasProperty("inputTexture"))block.port.setTexture(loadNCPFTexture(portCfg.get("inputTexture")));
                        block.port.portOutputDisplayName = portCfg.get("outputDisplayName");
                        if(portCfg.hasProperty("outputTexture"))block.port.setPortOutputTexture(loadNCPFTexture(portCfg.get("outputTexture")));
                    }
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config ruleCfg = (Config)rit.next();
                            block.rules.add(readOverMSRRule(ruleCfg));
                        }
                    }
                }else{
                    block.fuelVessel = blockCfg.hasProperty("fuelVessel");
                    block.irradiator = blockCfg.hasProperty("irradiator");
                    block.reflector = blockCfg.hasProperty("reflector");
                    block.moderator = blockCfg.hasProperty("moderator");
                    block.shield = blockCfg.hasProperty("shield");
                    block.heater = blockCfg.hasProperty("heater");
                }
                ConfigList recipes = blockCfg.get("recipes", new ConfigList());
                for(int i = 0; i<recipes.size(); i++){
                    Config recipeCfg = recipes.get(i);
                    Config inputCfg = recipeCfg.get("input");
                    Config outputCfg = recipeCfg.get("output");
                    multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(inputCfg.get("name"), outputCfg.get("name"));
                    recipe.inputDisplayName = inputCfg.get("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipe.inputLegacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))recipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                    recipe.inputRate = inputCfg.get("rate", 0);
                    recipe.outputDisplayName = outputCfg.get("displayName");
                    if(outputCfg.hasProperty("texture"))recipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                    recipe.outputRate = outputCfg.get("rate", 0);
                    if(block.fuelVessel){
                        Config recipeFuelVesselCfg = recipeCfg.get("fuelVessel");
                        recipe.fuelVesselEfficiency = recipeFuelVesselCfg.get("efficiency");
                        recipe.fuelVesselHeat = recipeFuelVesselCfg.get("heat");
                        recipe.fuelVesselTime = recipeFuelVesselCfg.get("time");
                        recipe.fuelVesselCriticality = recipeFuelVesselCfg.get("criticality");
                        recipe.fuelVesselSelfPriming = recipeFuelVesselCfg.get("selfPriming", false);
                    }
                    if(block.irradiator){
                        Config recipeIrradiatorCfg = recipeCfg.get("irradiator");
                        recipe.irradiatorEfficiency = recipeIrradiatorCfg.get("efficiency");
                        recipe.irradiatorHeat = recipeIrradiatorCfg.get("heat");
                    }
                    if(block.reflector){
                        Config recipeReflectorCfg = recipeCfg.get("reflector");
                        recipe.reflectorEfficiency = recipeReflectorCfg.get("efficiency");
                        recipe.reflectorReflectivity = recipeReflectorCfg.get("reflectivity");
                    }
                    if(block.moderator){
                        Config recipeModeratorCfg = recipeCfg.get("moderator");
                        recipe.moderatorFlux = recipeModeratorCfg.get("flux");
                        recipe.moderatorEfficiency = recipeModeratorCfg.get("efficiency");
                        recipe.moderatorActive = recipeModeratorCfg.get("active", false);
                    }
                    if(block.shield){
                        Config recipeShieldCfg = recipeCfg.get("shield");
                        recipe.shieldHeat = recipeShieldCfg.get("heat");
                        recipe.shieldEfficiency = recipeShieldCfg.get("efficiency");
                    }
                    if(block.heater){
                        Config recipeHeaterCfg = recipeCfg.get("heater");
                        recipe.heaterCooling = recipeHeaterCfg.get("cooling");
                    }
                    if(theBlockThatThisBlockIsAnAddonRecipeBlockFor!=null)theBlockThatThisBlockIsAnAddonRecipeBlockFor.allRecipes.add(recipe);
                    else block.allRecipes.add(recipe);
                    block.recipes.add(recipe);
                }
                if(theBlockThatThisBlockIsAnAddonRecipeBlockFor!=null){
                    configuration.overhaul.fissionMSR.allBlocks.add(block);
                }else{
                    parent.overhaul.fissionMSR.allBlocks.add(block);configuration.overhaul.fissionMSR.blocks.add(block);
                    if(block.port!=null){
                        parent.overhaul.fissionMSR.allBlocks.add(block.port);configuration.overhaul.fissionMSR.blocks.add(block.port);
                    }
                }
            }
            for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                int index = overhaulMSRPostLoadMap.get(rule);
                if(index==0){
                    rule.isSpecificBlock = false;
                    rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                }else{
                    rule.block = parent.overhaul.fissionMSR.allBlocks.get(index-1);
                }
            }
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine - load blocks">
    protected void loadOverhaulTurbineBlocks(Config overhaul, Configuration parent, Configuration configuration, boolean loadSettings) {
        if(overhaul.hasProperty("turbine")){
            configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
            Config turbine = overhaul.get("turbine");
            if(loadSettings){
                configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                configuration.overhaul.turbine.minLength = turbine.get("minLength");
                configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                configuration.overhaul.turbine.throughputEfficiencyLeniencyMult = turbine.get("throughputEfficiencyLeniencyMult");
                configuration.overhaul.turbine.throughputEfficiencyLeniencyThreshold = turbine.get("throughputEfficiencyLeniencyThreshold");
                configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
            }
            ConfigList blocks = turbine.get("blocks");
            overhaulTurbinePostLoadMap.clear();
            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.turbine.Block block = new multiblock.configuration.overhaul.turbine.Block(blockCfg.get("name"));
                block.displayName = blockCfg.get("displayName");
                if(blockCfg.hasProperty("legacyNames")){
                    ConfigList names = blockCfg.getConfigList("legacyNames");
                    for(int i = 0; i<names.size(); i++){
                        block.legacyNames.add(names.get(i));
                    }
                }
                Config bladeCfg = blockCfg.get("blade");
                if(bladeCfg!=null){
                    block.blade = true;
                    block.bladeEfficiency = bladeCfg.get("efficiency");
                    block.bladeExpansion = bladeCfg.get("expansion");
                    block.bladeStator = bladeCfg.get("stator", false);
                }
                Config coilCfg = blockCfg.get("coil");
                if(coilCfg!=null){
                    block.coil = true;
                    block.coilEfficiency = coilCfg.get("efficiency");
                }
                block.bearing = blockCfg.get("bearing", false);
                block.shaft = blockCfg.get("shaft", false);
                block.connector = blockCfg.get("connector", false);
                block.controller = blockCfg.get("controller", false);
                block.casing = blockCfg.get("casing", false);
                block.casingEdge = blockCfg.get("casingEdge", false);
                block.inlet = blockCfg.get("inlet", false);
                block.outlet = blockCfg.get("outlet", false);
                if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                        Config ruleCfg = (Config)rit.next();
                        block.rules.add(readOverTurbineRule(ruleCfg));
                    }
                }
                parent.overhaul.turbine.allBlocks.add(block);configuration.overhaul.turbine.blocks.add(block);
            }
            for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                int index = overhaulTurbinePostLoadMap.get(rule);
                if(index==0){
                    rule.isSpecificBlock = false;
                    rule.blockType = multiblock.configuration.overhaul.turbine.PlacementRule.BlockType.CASING;
                }else{
                    rule.block = parent.overhaul.turbine.allBlocks.get(index-1);
                }
            }
            ConfigList recipes = turbine.get("recipes");
            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                Config recipeCfg = (Config)irit.next();
                Config inputCfg = recipeCfg.get("input");
                Config outputCfg = recipeCfg.get("output");
                multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(inputCfg.get("name"), outputCfg.get("name"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                recipe.inputDisplayName = inputCfg.get("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.inputLegacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                recipe.outputDisplayName = outputCfg.get("displayName");
                if(outputCfg.hasProperty("texture"))recipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                parent.overhaul.turbine.allRecipes.add(recipe);configuration.overhaul.turbine.recipes.add(recipe);
            }
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Fusion Generator - load blocks">
    protected void loadOverhaulFusionGeneratorBlocks(Config overhaul, Configuration configuration, boolean loadSettings) {
        if(overhaul.hasProperty("fusion")){
            configuration.overhaul.fusion = new multiblock.configuration.overhaul.fusion.FusionConfiguration();
            Config fusion = overhaul.get("fusion");
            if(loadSettings){
                configuration.overhaul.fusion.minInnerRadius = fusion.get("minInnerRadius");
                configuration.overhaul.fusion.maxInnerRadius = fusion.get("maxInnerRadius");
                configuration.overhaul.fusion.minCoreSize = fusion.get("minCoreSize");
                configuration.overhaul.fusion.maxCoreSize = fusion.get("maxCoreSize");
                configuration.overhaul.fusion.minToroidWidth = fusion.get("minToroidWidth");
                configuration.overhaul.fusion.maxToroidWidth = fusion.get("maxToroidWidth");
                configuration.overhaul.fusion.minLiningThickness = fusion.get("minLiningThickness");
                configuration.overhaul.fusion.maxLiningThickness = fusion.get("maxLiningThickness");
                configuration.overhaul.fusion.coolingEfficiencyLeniency = fusion.get("coolingEfficiencyLeniency");
                configuration.overhaul.fusion.sparsityPenaltyMult = fusion.get("sparsityPenaltyMult");
                configuration.overhaul.fusion.sparsityPenaltyThreshold = fusion.get("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fusion.get("blocks");
            overhaulFusionPostLoadMap.clear();
            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.fusion.Block block = new multiblock.configuration.overhaul.fusion.Block(blockCfg.get("name"));
                block.displayName = blockCfg.get("displayName");
                if(blockCfg.hasProperty("legacyNames")){
                    ConfigList names = blockCfg.getConfigList("legacyNames");
                    for(int i = 0; i<names.size(); i++){
                        block.legacyNames.add(names.get(i));
                    }
                }
                block.cluster = blockCfg.get("cluster", false);
                block.createCluster = blockCfg.get("createCluster", false);
                block.conductor = blockCfg.get("conductor", false);
                block.connector = blockCfg.get("connector", false);
                block.core = blockCfg.get("core", false);
                block.electromagnet = blockCfg.get("electromagnet", false);
                block.heatingBlanket = blockCfg.get("heatingBlanket", false);
                block.functional = blockCfg.get("functional", false);
                boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                Config breedingBlanketCfg = blockCfg.get("breedingBlanket");
                if(breedingBlanketCfg!=null){
                    block.breedingBlanket = true;
                    block.breedingBlanketHasBaseStats = breedingBlanketCfg.get("hasBaseStats", !hasRecipes);
                    if(block.breedingBlanketHasBaseStats){
                        block.breedingBlanketEfficiency = breedingBlanketCfg.get("efficiency");
                        block.breedingBlanketHeat = breedingBlanketCfg.get("heat");
                        block.breedingBlanketAugmented = breedingBlanketCfg.get("augmented", false);
                    }
                }
                Config shieldingCfg = blockCfg.get("shielding");
                if(shieldingCfg!=null){
                    block.shielding = true;
                    block.shieldingHasBaseStats = shieldingCfg.get("hasBaseStats", !hasRecipes);
                    if(block.shieldingHasBaseStats){
                        block.shieldingShieldiness = shieldingCfg.get("shieldiness");
                    }
                }
                Config reflectorCfg = blockCfg.get("reflector");
                if(reflectorCfg!=null){
                    block.reflector = true;
                    block.reflectorHasBaseStats = reflectorCfg.get("hasBaseStats", !hasRecipes);
                    if(block.reflectorHasBaseStats){
                        block.reflectorEfficiency = reflectorCfg.get("efficiency");
                    }
                }
                Config heatsinkCfg = blockCfg.get("heatsink");
                if(heatsinkCfg!=null){
                    block.heatsink = true;
                    block.heatsinkHasBaseStats = heatsinkCfg.get("hasBaseStats", !hasRecipes);
                    if(block.heatsinkHasBaseStats){
                        block.heatsinkCooling = heatsinkCfg.get("cooling");
                    }
                }
                if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                        Config ruleCfg = (Config)rit.next();
                        block.rules.add(readOverFusionRule(ruleCfg));
                    }
                }
                ConfigList recipes = blockCfg.get("recipes", new ConfigList());
                for(int i = 0; i<recipes.size(); i++){
                    Config recipeCfg = recipes.get(i);
                    Config inputCfg = recipeCfg.get("input");
                    Config outputCfg = recipeCfg.get("output");
                    multiblock.configuration.overhaul.fusion.BlockRecipe recipe = new multiblock.configuration.overhaul.fusion.BlockRecipe(inputCfg.get("name"), outputCfg.get("name"));
                    recipe.inputDisplayName = inputCfg.get("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipe.inputLegacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))recipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                    recipe.inputRate = inputCfg.get("rate", 0);
                    recipe.outputDisplayName = outputCfg.get("displayName");
                    if(outputCfg.hasProperty("texture"))recipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                    recipe.outputRate = outputCfg.get("rate", 0);
                    if(block.breedingBlanket){
                        Config recipeBreedingBlanketCfg = recipeCfg.get("breedingBlanket");
                        recipe.breedingBlanketAugmented = recipeBreedingBlanketCfg.get("augmented", false);
                        recipe.breedingBlanketEfficiency = recipeBreedingBlanketCfg.get("efficiency");
                        recipe.breedingBlanketHeat = recipeBreedingBlanketCfg.get("heat");
                    }
                    if(block.shielding){
                        Config recipeShieldingCfg = recipeCfg.get("shielding");
                        recipe.shieldingShieldiness = recipeShieldingCfg.get("shieldiness");
                    }
                    if(block.reflector){
                        Config recipeReflectorCfg = recipeCfg.get("reflector");
                        recipe.reflectorEfficiency = recipeReflectorCfg.get("efficiency");
                    }
                    if(block.heatsink){
                        Config recipeHeatsinkCfg = recipeCfg.get("heatsink");
                        recipe.heatsinkCooling = recipeHeatsinkCfg.get("cooling");
                    }
                    block.allRecipes.add(recipe);block.recipes.add(recipe);
                }
                configuration.overhaul.fusion.allBlocks.add(block);configuration.overhaul.fusion.blocks.add(block);
            }
            for(multiblock.configuration.overhaul.fusion.PlacementRule rule : overhaulFusionPostLoadMap.keySet()){
                int index = overhaulFusionPostLoadMap.get(rule);
                if(index==0){
                    rule.isSpecificBlock = false;
                    rule.blockType = multiblock.configuration.overhaul.fusion.PlacementRule.BlockType.AIR;
                }else{
                    rule.block = configuration.overhaul.fusion.allBlocks.get(index-1);
                }
            }
            ConfigList recipes = fusion.get("recipes");
            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                Config recipeCfg = (Config)irit.next();
                Config inputCfg = recipeCfg.get("input");
                Config outputCfg = recipeCfg.get("output");
                multiblock.configuration.overhaul.fusion.Recipe recipe = new multiblock.configuration.overhaul.fusion.Recipe(inputCfg.get("name"), outputCfg.get("name"), recipeCfg.get("efficiency"), recipeCfg.get("heat"), recipeCfg.get("time"), recipeCfg.getFloat("fluxiness"));
                recipe.inputDisplayName = inputCfg.get("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.inputLegacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                recipe.outputDisplayName = outputCfg.get("displayName");
                if(outputCfg.hasProperty("texture"))recipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                configuration.overhaul.fusion.allRecipes.add(recipe);configuration.overhaul.fusion.recipes.add(recipe);
            }
            ConfigList coolantRecipes = fusion.get("coolantRecipes");
            for(Iterator coit = coolantRecipes.iterator(); coit.hasNext();){
                Config coolantRecipeCfg = (Config)coit.next();
                Config inputCfg = coolantRecipeCfg.get("input");
                Config outputCfg = coolantRecipeCfg.get("output");
                multiblock.configuration.overhaul.fusion.CoolantRecipe coolantRecipe = new multiblock.configuration.overhaul.fusion.CoolantRecipe(inputCfg.get("name"), outputCfg.get("name"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.getFloat("outputRatio"));
                coolantRecipe.inputDisplayName = inputCfg.get("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        coolantRecipe.inputLegacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))coolantRecipe.setInputTexture(loadNCPFTexture(inputCfg.get("texture")));
                coolantRecipe.outputDisplayName = outputCfg.get("displayName");
                if(outputCfg.hasProperty("texture"))coolantRecipe.setOutputTexture(loadNCPFTexture(outputCfg.get("texture")));
                configuration.overhaul.fusion.allCoolantRecipes.add(coolantRecipe);configuration.overhaul.fusion.coolantRecipes.add(coolantRecipe);
            }
        }
    }
//</editor-fold>

    protected Image loadNCPFTexture(ConfigNumberList texture){
        int size = (int) texture.get(0);
        Image image = new Image(size, size);
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