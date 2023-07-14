package net.ncplanner.plannerator.planner.file.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFModuleReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Addon;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulFusionDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulTurbineDesign;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
import net.ncplanner.plannerator.planner.ncpf.module.DisplayNamesModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CoolantVentModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BladeModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.StatorModule;
public class LegacyNCPF11Reader implements FormatReader {
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

    HashMap<net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule, Integer> underhaulPostLoadMap = new HashMap<>();
    HashMap<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule, Integer> overhaulSFRPostLoadMap = new HashMap<>();
    HashMap<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule, Integer> overhaulMSRPostLoadMap = new HashMap<>();
    HashMap<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule, Integer> overhaulTurbinePostLoadMap = new HashMap<>();
    HashMap<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.PlacementRule, Integer> overhaulFusionPostLoadMap = new HashMap<>();
    HashMap<OverhaulTurbineDesign, ArrayList<Integer>> overhaulTurbinePostLoadInputsMap = new HashMap<>();

    protected byte getTargetVersion() {
        return (byte) 11;
    }
    @Override
    public synchronized Project read(InputStream in, RecoveryHandler recovery){
        overhaulTurbinePostLoadInputsMap.clear();
        try{
            Project project = new Project();
            Config header = Config.newConfig();
            header.load(in);
            int multiblocks = header.getInt("count");
            if(header.hasProperty("metadata")){
                Config metadata = header.getConfig("metadata");
                for(String key : metadata.properties()){
                    project.metadata.put(key, metadata.get(key));
                }
            }
            Config config = Config.newConfig();
            config.load(in);
            loadConfiguration(project, config);
            project.conglomerate();
            for(int i = 0; i<multiblocks; i++){
                project.designs.add(readMultiblock(project, in, recovery));
            }
            if(!overhaulTurbinePostLoadInputsMap.isEmpty())throw new UnsupportedOperationException("Not yet implemented.");
            /*
            for(OverhaulTurbineDesign turbine : overhaulTurbinePostLoadInputsMap.keySet()){
                for(int i : overhaulTurbinePostLoadInputsMap.get(turbine)){
                    turbine.inputs.inputs.add(project.designs.get(i));
                }
            }
            */
            in.close();
            return project;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }

    protected synchronized Design readMultiblock(Project ncpf, InputStream in, RecoveryHandler recovery) {
        Config data = Config.newConfig();
        data.load(in);
        Design design;
        int id = data.getInt("id");
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
            case 3:
                design = readMultiblockOverhaulTurbine(ncpf, data, recovery);
                break;
            case 4:
                design = readMultiblockOverhaulFusionReactor(ncpf, data, recovery);
                break;
            default:
                throw new IllegalArgumentException("Unknown Multiblock ID: "+id);
        }
        if(data.hasProperty("metadata")){
            Config metadata = data.getConfig("metadata");
            for(String key : metadata.properties()){
                design.metadata.put(key, metadata.get(key));
            }
        }
        return design;
    }

    protected synchronized Design readMultiblockUnderhaulSFR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList dimensions = data.getConfigNumberList("dimensions");
        UnderhaulSFRDesign underhaulSFR = new UnderhaulSFRDesign(ncpf, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2));
        underhaulSFR.fuel = recovery.recoverUnderhaulSFRFuelLegacyNCPF(ncpf, data.getInt("fuel", -1));
        boolean compact = data.getBoolean("compact");
        ConfigNumberList blocks = data.getConfigNumberList("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 0; x<underhaulSFR.design.length; x++){
                for(int y = 0; y<underhaulSFR.design.length; y++){
                    for(int z = 0; z<underhaulSFR.design.length; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block b = recovery.recoverUnderhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                            if(b!=null)underhaulSFR.design[x][y][z] = b;
                        }
                        index[0]++;
                    }
                }
            }
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block b = recovery.recoverUnderhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)underhaulSFR.design[x][y][z] = b;
            }
        }
        return underhaulSFR;
    }
    protected synchronized Design readMultiblockOverhaulSFR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList dimensions = data.getConfigNumberList("dimensions");
        OverhaulSFRDesign overhaulSFR = new OverhaulSFRDesign(ncpf, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2));
        overhaulSFR.coolantRecipe = recovery.recoverOverhaulSFRCoolantRecipeLegacyNCPF(ncpf, data.getInt("coolantRecipe", -1));
        boolean compact = data.getBoolean("compact");
        ConfigNumberList blocks = data.getConfigNumberList("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 0; x<overhaulSFR.design.length; x++){
                for(int y = 0; y<overhaulSFR.design.length; y++){
                    for(int z = 0; z<overhaulSFR.design.length; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block b = recovery.recoverOverhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                            if(b!=null)overhaulSFR.design[x][y][z] = b;
                        }
                        index[0]++;
                    }
                }
            }
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block b = recovery.recoverOverhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)overhaulSFR.design[x][y][z] = b;
            }
        }
        ConfigNumberList blockRecipes = data.getConfigNumberList("blockRecipes");
        int recipeIndex = 0;
        ConfigNumberList ports = data.getConfigNumberList("ports");
        int portIndex = 0;
        for(int x = 0; x<overhaulSFR.design.length; x++){
            for(int y = 0; y<overhaulSFR.design.length; y++){
                for(int z = 0; z<overhaulSFR.design.length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block block = overhaulSFR.design[x][y][z];
                    if(!block.fuels.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulSFR.fuels[x][y][z] = recovery.recoverOverhaulSFRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                    if(!block.irradiatorRecipes.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulSFR.irradiatorRecipes[x][y][z] = recovery.recoverOverhaulSFRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                    if(block.port!=null||block.coolantVent!=null){
                        boolean isToggled = ports.get(portIndex)>0;
                        overhaulSFR.design[x][y][z] = isToggled?block.toggled:block.unToggled;
                        portIndex++;
                    }
                }
            }
        }
        return overhaulSFR;
    }
    protected synchronized Design readMultiblockOverhaulMSR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList dimensions = data.getConfigNumberList("dimensions");
        OverhaulMSRDesign overhaulMSR = new OverhaulMSRDesign(ncpf, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2));
        boolean compact = data.getBoolean("compact");
        ConfigNumberList blocks = data.getConfigNumberList("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 0; x<overhaulMSR.design.length; x++){
                for(int y = 0; y<overhaulMSR.design.length; y++){
                    for(int z = 0; z<overhaulMSR.design.length; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block b = recovery.recoverOverhaulMSRBlockLegacyNCPF(ncpf, bid-1);
                            if(b!=null)overhaulMSR.design[x][y][z] = b;
                        }
                        index[0]++;
                    }
                }
            }
        }else{
            for(int j = 0; j<blocks.size(); j+=4){
                int x = (int) blocks.get(j)+1;
                int y = (int) blocks.get(j+1)+1;
                int z = (int) blocks.get(j+2)+1;
                int bid = (int) blocks.get(j+3);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block b = recovery.recoverOverhaulMSRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)overhaulMSR.design[x][y][z] = b;
            }
        }
        ConfigNumberList blockRecipes = data.getConfigNumberList("blockRecipes");
        int recipeIndex = 0;
        ConfigNumberList ports = data.getConfigNumberList("ports");
        int portIndex = 0;
        for(int x = 0; x<overhaulMSR.design.length; x++){
            for(int y = 0; y<overhaulMSR.design.length; y++){
                for(int z = 0; z<overhaulMSR.design.length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block block = overhaulMSR.design[x][y][z];
                    if(!block.fuels.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulMSR.fuels[x][y][z] = recovery.recoverOverhaulMSRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                    if(!block.irradiatorRecipes.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulMSR.irradiatorRecipes[x][y][z] = recovery.recoverOverhaulMSRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                    if(!block.heaterRecipes.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulMSR.heaterRecipes[x][y][z] = recovery.recoverOverhaulMSRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                    if(block.port!=null){
                        boolean isToggled = ports.get(portIndex)>0;
                        overhaulMSR.design[x][y][z] = isToggled?block.toggled:block.unToggled;
                        portIndex++;
                    }
                }
            }
        }
        return overhaulMSR;
    }
    protected synchronized Design readMultiblockOverhaulTurbine(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList dimensions = data.getConfigNumberList("dimensions");
        OverhaulTurbineDesign overhaulTurbine = new OverhaulTurbineDesign(ncpf, (int)dimensions.get(0), (int)dimensions.get(1), (int)dimensions.get(2));
        overhaulTurbine.recipe = recovery.recoverOverhaulTurbineRecipeLegacyNCPF(ncpf, data.getInt("recipe", -1));
        if(data.hasProperty("inputs")){
            overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
            ConfigNumberList inputs = data.getConfigNumberList("inputs");
            for(int i = 0; i<inputs.size(); i++){
                overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add((int)inputs.get(i));
            }
        }
        ConfigNumberList blocks = data.getConfigNumberList("blocks");
        int[] index = new int[1];
        for(int x = 0; x<overhaulTurbine.design.length; x++){
            for(int y = 0; y<overhaulTurbine.design.length; y++){
                for(int z = 0; z<overhaulTurbine.design.length; z++){
                    int bid = (int) blocks.get(index[0]);
                    if(bid>0){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block b = recovery.recoverOverhaulTurbineBlockLegacyNCPF(ncpf, bid-1);
                        if(b!=null)overhaulTurbine.design[x][y][z] = b;
                    }
                    index[0]++;
                }
            }
        }
        return overhaulTurbine;
    }
    protected synchronized Design readMultiblockOverhaulFusionReactor(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList dimensions = data.getConfigNumberList("dimensions");
        OverhaulFusionDesign overhaulFusion = new OverhaulFusionDesign(ncpf, (int)dimensions.get(0),(int)dimensions.get(1),(int)dimensions.get(2),(int)dimensions.get(3));
        overhaulFusion.recipe = recovery.recoverOverhaulFusionRecipeLegacyNCPF(ncpf, data.getInt("recipe", -1));
        overhaulFusion.coolantRecipe = recovery.recoverOverhaulFusionCoolantRecipeLegacyNCPF(ncpf, data.getInt("coolantRecipe", -1));
        ConfigNumberList blocks = data.getConfigNumberList("blocks");
        int[] index = new int[0];
        for(int x = 0; x<overhaulFusion.design.length; x++){
            for(int y = 0; y<overhaulFusion.design.length; y++){
                for(int z = 0; z<overhaulFusion.design.length; z++){
                    int bid = (int) blocks.get(index[0]);
                    if(bid>0){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block b = recovery.recoverOverhaulFusionBlockLegacyNCPF(ncpf, bid-1);
                        if(b!=null)overhaulFusion.design[x][y][z] = b;
                    }
                    index[0]++;
                }
            }
        }
        ConfigNumberList blockRecipes = data.getConfigNumberList("blockRecipes");
        int recipeIndex = 0;
        for(int x = 0; x<overhaulFusion.design.length; x++){
            for(int y = 0; y<overhaulFusion.design.length; y++){
                for(int z = 0; z<overhaulFusion.design.length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block block = overhaulFusion.design[x][y][z];
                    if(!block.breedingBlanketRecipes.isEmpty()){
                        int rid = (int)blockRecipes.get(recipeIndex);
                        if(rid!=0)overhaulFusion.breedingBlanketRecipes[x][y][z] = recovery.recoverOverhaulFusionBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                }
            }
        }
        return overhaulFusion;
    }

    protected <Rule extends NCPFPlacementRule> void readRuleBlock(HashMap<Rule, Integer> postMap, Rule rule, Supplier<NCPFModule>[] blockTypes, Config ruleCfg) {
        boolean isSpecificBlock = ruleCfg.getBoolean("isSpecificBlock");
        if(isSpecificBlock){
            postMap.put(rule, ruleCfg.getInt("block"));
        }else{
            rule.target = new NCPFModuleReference(blockTypes[ruleCfg.getByte("blockType")]);;
        }
    }
    private final NCPFPlacementRule.RuleType[] ruleTypes = new NCPFPlacementRule.RuleType[]{
        NCPFPlacementRule.RuleType.BETWEEN, 
        NCPFPlacementRule.RuleType.AXIAL, 
        NCPFPlacementRule.RuleType.VERTEX, 
        NCPFPlacementRule.RuleType.EDGE, 
        NCPFPlacementRule.RuleType.OR, 
        NCPFPlacementRule.RuleType.AND
    };
    private final Supplier<NCPFModule>[] underhaulSFRBlockTypes = new Supplier[]{
        AirModule::new,//air
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule::new,//doesn't do active coolers, but this is underhaul so this isn't a thing anyway
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule::new
    };
    private final Supplier<NCPFModule>[] overhaulSFRBlockTypes = new Supplier[]{
        AirModule::new,//air
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.HeatsinkModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ModeratorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ReflectorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronShieldModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ConductorModule::new
    };
    private final Supplier<NCPFModule>[] overhaulMSRBlockTypes = new Supplier[]{
        AirModule::new,//air
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule::new
    };
    private final Supplier<NCPFModule>[] overhaulTurbineBlockTypes = new Supplier[]{
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule::new
    };
    private final Supplier<NCPFModule>[] overhaulFusionBlockTypes = new Supplier[]{
        AirModule::new,//air
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ToroidalElectromagnetModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.PoloidalElectromagnetModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatingBlanketModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ReflectorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatsinkModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatsinkModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConductorModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConnectorModule::new
    };
    protected <Rule extends NCPFPlacementRule> Rule readGenericRule(HashMap<Rule, Integer> postMap, Supplier<Rule> newRule, Supplier<NCPFModule>[] blockTypes, Config ruleCfg){
        Rule rule = newRule.get();
        byte type = ruleCfg.getByte("type");
        rule.rule = ruleTypes[type];
        switch(rule.rule){
            case BETWEEN:
            case AXIAL:
                readRuleBlock(postMap, rule, blockTypes, ruleCfg);
                rule.min = ruleCfg.getByte("min");
                rule.max = ruleCfg.getByte("max");
                break;
            case VERTEX:
            case EDGE:
                readRuleBlock(postMap, rule, blockTypes, ruleCfg);
                break;
            case OR:
                ConfigList rules = ruleCfg.getConfigList("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, newRule, blockTypes, rules.getConfig(i)));
                }
                break;
            case AND:
                rules = ruleCfg.getConfigList("rules");
                for(int i = 0; i<rules.size(); i++){
                    rule.rules.add(readGenericRule(postMap, newRule, blockTypes, rules.getConfig(i)));
                }
                break;
        }
        return rule;
    }

    protected net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule readUnderRule(Config ruleCfg) {
        return readGenericRule(underhaulPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule::new, underhaulSFRBlockTypes, ruleCfg);
    }
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule readOverSFRRule(Config ruleCfg){
        return readGenericRule(overhaulSFRPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule::new, overhaulSFRBlockTypes, ruleCfg);
    }
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule readOverMSRRule(Config ruleCfg){
        return readGenericRule(overhaulMSRPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule::new, overhaulMSRBlockTypes, ruleCfg);
    }
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule readOverTurbineRule(Config ruleCfg){
        return readGenericRule(overhaulTurbinePostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule::new, overhaulTurbineBlockTypes, ruleCfg);
    }
    protected net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.PlacementRule readOverFusionRule(Config ruleCfg) {
        return readGenericRule(overhaulFusionPostLoadMap, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.PlacementRule::new, overhaulFusionBlockTypes, ruleCfg);
    }

    protected void loadConfiguration(Project project, Config config){
        boolean partial = config.getBoolean("partial");
        String name = config.getString("name");
        String version = config.getString("version");
        String underhaulVersion = config.getString("underhaulVersion");
        boolean addon = config.getBoolean("addon");
        loadUnderhaulBlocks(project.configuration, config, !partial&&!addon);
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block> overhaulSFRAdditionalBlocks = new ArrayList<>();
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block> overhaulMSRAdditionalBlocks = new ArrayList<>();
        if(config.hasProperty("overhaul")){
            Config overhaul = config.getConfig("overhaul");
            loadOverhaulSFRBlocks(project.configuration, overhaul, !partial&&!addon, false, addon, overhaulSFRAdditionalBlocks);
            loadOverhaulMSRBlocks(project.configuration, overhaul, !partial&&!addon, false, addon, overhaulMSRAdditionalBlocks);
            loadOverhaulTurbineBlocks(project.configuration, overhaul, !partial&&!addon);
            loadOverhaulFusionGeneratorBlocks(project.configuration, overhaul, !partial&&!addon);
        }
        project.configuration.withConfiguration(UnderhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = underhaulVersion;
        });
        project.configuration.withConfiguration(OverhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulMSRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulTurbineConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulFusionConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        if(config.hasProperty("addons")){
            ConfigList addons = config.getConfigList("addons");
            for(int i = 0; i<addons.size(); i++){
                project.addons.add(loadAddon(project, addons.get(i)));
            }
        }
        if(!overhaulSFRAdditionalBlocks.isEmpty())project.configuration.getConfiguration(OverhaulSFRConfiguration::new).blocks.addAll(overhaulSFRAdditionalBlocks);
        if(!overhaulMSRAdditionalBlocks.isEmpty())project.configuration.getConfiguration(OverhaulMSRConfiguration::new).blocks.addAll(overhaulMSRAdditionalBlocks);
        project.conglomerate();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.PlacementRule rule : underhaulPostLoadMap.keySet()){
            int index = underhaulPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockReference(project.getConfiguration(UnderhaulSFRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
            int index = overhaulSFRPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(project.getConfiguration(OverhaulSFRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
            int index = overhaulMSRPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference(project.getConfiguration(OverhaulMSRConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
            int index = overhaulTurbinePostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockReference(project.getConfiguration(OverhaulTurbineConfiguration::new).blocks.get(index-1));
            }
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.PlacementRule rule : overhaulFusionPostLoadMap.keySet()){
            int index = overhaulFusionPostLoadMap.get(rule);
            if(index==0){
                rule.blockType = new NCPFModuleReference(AirModule::new);
            }else{
                rule.block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockReference(project.getConfiguration(OverhaulFusionConfiguration::new).blocks.get(index-1));
            }
        }
        //combine underhaul active coolers into one
        net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block activeCooler = null;
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block> it = project.configuration.getConfiguration(UnderhaulSFRConfiguration::new).blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block block = it.next();
            if(block.activeCooler!=null){
                if(activeCooler==null){
                    activeCooler = block;
                    continue;
                }
                activeCooler.activeCoolerRecipes.addAll(block.activeCoolerRecipes);
                it.remove();
            }
        }
        //do the same for the conglomeration
        activeCooler = null;
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block> it = project.getConfiguration(UnderhaulSFRConfiguration::new).blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block block = it.next();
            if(block.activeCooler!=null){
                if(activeCooler==null){
                    activeCooler = block;
                    continue;
                }
                activeCooler.activeCoolerRecipes.addAll(block.activeCoolerRecipes);
                it.remove();
            }
        }
    }
    protected Addon loadAddon(Project project, Config config){
        Addon addon = new Addon();
        String name = config.getString("name");
        String version = config.getString("version");
        String underhaulVersion = config.getString("underhaulVersion");
        boolean isAddon = config.getBoolean("addon");
        loadUnderhaulBlocks(addon.configuration, config, false);
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block> overhaulSFRAdditionalBlocks = new ArrayList<>();
        List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block> overhaulMSRAdditionalBlocks = new ArrayList<>();
        if(config.hasProperty("overhaul")){
            Config overhaul = config.getConfig("overhaul");
            loadOverhaulSFRBlocks(addon.configuration, overhaul, false, true, isAddon, overhaulSFRAdditionalBlocks);
            loadOverhaulMSRBlocks(addon.configuration, overhaul, false, true, isAddon, overhaulMSRAdditionalBlocks);
            loadOverhaulTurbineBlocks(addon.configuration, overhaul, false);
            // No support for fusion addons.
        }
        project.configuration.withConfiguration(UnderhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = underhaulVersion;
        });
        project.configuration.withConfiguration(OverhaulSFRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulMSRConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulTurbineConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        project.configuration.withConfiguration(OverhaulFusionConfiguration::new, (cfg)->{
            cfg.metadata.name = name;
            cfg.metadata.version = version;
        });
        //the addon was loading addon addons, but those were never a thing? so I deleted it. If you're reading this, it was probably important. sorry :(
        return addon;
    }

    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration - load blocks">
    protected void loadUnderhaulBlocks(NCPFConfigurationContainer project, Config config, boolean loadSettings) {
        if(config.hasProperty("underhaul")){
            Config underhaul = config.getConfig("underhaul");
            if(underhaul.hasProperty("fissionSFR")){
                UnderhaulSFRConfiguration configuration = new UnderhaulSFRConfiguration();
                Config fissionSFR = underhaul.getConfig("fissionSFR");
                ConfigList blocks = fissionSFR.getConfigList("blocks");
                if(loadSettings){
                    configuration.settings.minSize = fissionSFR.getInt("minSize");
                    configuration.settings.maxSize = fissionSFR.getInt("maxSize");
                    configuration.settings.neutronReach = fissionSFR.getInt("neutronReach");
                    configuration.settings.moderatorExtraPower = fissionSFR.getFloat("moderatorExtraPower");
                    configuration.settings.moderatorExtraHeat = fissionSFR.getFloat("moderatorExtraHeat");
                    configuration.settings.activeCoolerRate = fissionSFR.getInt("activeCoolerRate");
                }
                underhaulPostLoadMap.clear();
                for(int i = 0; i<blocks.size(); i++){
                    Config blockCfg = blocks.getConfig(i);
                    net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block block = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                    configuration.blocks.add(block);
                    block.names.displayName = blockCfg.getString("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int idx = 0; idx<names.size(); idx++){
                            block.names.legacyNames.add(names.get(idx));
                        }
                    }
                    String active = blockCfg.getString("active");
                    int cooling = blockCfg.getInt("cooling", 0);
                    net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule coolerStats = null;//used to add placement rules
                    if(active!=null){
                        block.activeCooler = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ActiveCoolerModule();
                        net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe(new NCPFLegacyFluidElement(active));
                        coolerStats = recipe.stats;
                        recipe.stats.cooling = cooling;
                        block.activeCoolerRecipes.add(recipe);
                    }else if(cooling!=0){
                        coolerStats = block.cooler = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule();
                        block.cooler.cooling = cooling;
                    }
                    if(blockCfg.getBoolean("fuelCell", false))block.fuelCell = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule();
                    if(blockCfg.getBoolean("moderator", false))block.moderator = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule();
                    if(blockCfg.getBoolean("casing", false))block.casing = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule();
                    if(blockCfg.getBoolean("controller", false))block.controller = new net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ControllerModule();
                    if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.getConfigList("rules");
                        for(int idx = 0; idx<rules.size(); idx++){
                            coolerStats.rules.add(readUnderRule(rules.getConfig(idx)));
                        }
                    }
                }
                ConfigList fuels = fissionSFR.getConfigList("fuels");
                for(int i = 0; i<fuels.size(); i++){
                    Config fuelCfg = fuels.getConfig(i);
                    net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel(new NCPFLegacyItemElement(fuelCfg.getString("name")));
                    fuel.stats.power = fuelCfg.getFloat("power");
                    fuel.stats.heat = fuelCfg.getFloat("heat");
                    fuel.stats.time = fuelCfg.getInt("time");
                    fuel.names.displayName = fuelCfg.getString("displayName");
                    if(fuelCfg.hasProperty("legacyNames")){
                        ConfigList names = fuelCfg.getConfigList("legacyNames");
                        for(int idx = 0; idx<names.size(); idx++){
                            fuel.names.legacyNames.add(names.get(idx));
                        }
                    }
                    if(fuelCfg.hasProperty("texture"))fuel.texture.texture = loadNCPFTexture(fuelCfg.getConfigNumberList("texture"));
                    configuration.fuels.add(fuel);
                }
                project.setConfiguration(configuration);
            }
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Overhaul SFR - load blocks">
    protected void loadOverhaulSFRBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings, boolean loadingAddon, boolean isAddon, List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block> additionalBlocks) {
        if(overhaul.hasProperty("fissionSFR")){
            OverhaulSFRConfiguration configuration = new OverhaulSFRConfiguration();
            Config fissionSFR = overhaul.getConfig("fissionSFR");
            if(loadSettings){
                configuration.settings.minSize = fissionSFR.getInt("minSize");
                configuration.settings.maxSize = fissionSFR.getInt("maxSize");
                configuration.settings.neutronReach = fissionSFR.getInt("neutronReach");
                configuration.settings.coolingEfficiencyLeniency = fissionSFR.getInt("coolingEfficiencyLeniency");
                configuration.settings.sparsityPenaltyMultiplier = fissionSFR.getFloat("sparsityPenaltyMult");
                configuration.settings.sparsityPenaltyThreshold = fissionSFR.getFloat("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fissionSFR.getConfigList("blocks");
            overhaulSFRPostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block theBlockThatThisBlockIsAnAddonRecipeBlockFor = null;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                if (loadingAddon) {
                    for (net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block b : project.getConfiguration(OverhaulSFRConfiguration::new).blocks) {
                        if (b.definition.matches(block.definition)) {
                            theBlockThatThisBlockIsAnAddonRecipeBlockFor = b;
                        }
                    }
                }
                boolean isFuelCell = false;
                boolean isIrradiator = false;
                if(theBlockThatThisBlockIsAnAddonRecipeBlockFor==null){
                    block.names.displayName = blockCfg.getString("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int idx = 0; idx<names.size(); idx++){
                            block.names.legacyNames.add(names.get(idx));
                        }
                    }
                    if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ConductorModule();
                    if(blockCfg.getBoolean("casing", false))block.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule(blockCfg.getBoolean("casingEdge", false));
                    if(blockCfg.getBoolean("controller", false))block.controller = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ControllerModule();
                    Config coolantVentCfg = blockCfg.getConfig("coolantVent");
                    if(coolantVentCfg!=null){
                        block.coolantVent = new CoolantVentModule();
                        //TODO different definition
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block(block.definition.copyTo(NCPFLegacyBlockElement::new));
                        output.casing = block.casing.copyTo(net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule::new);
                        block.coolantVent.output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(output);
                        if(coolantVentCfg.hasProperty("outTexture"))output.texture.texture = loadNCPFTexture(coolantVentCfg.getConfigNumberList("outTexture"));
                        output.names.displayName = coolantVentCfg.getString("outDisplayName");
                        additionalBlocks.add(output);
                    }
                    boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                    Config fuelCellCfg = blockCfg.getConfig("fuelCell");
                    if(fuelCellCfg!=null)block.fuelCell = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule();
                    Config irradiatorCfg = blockCfg.getConfig("irradiator");
                    if(irradiatorCfg!=null)block.irradiator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule();
                    Config reflectorCfg = blockCfg.getConfig("reflector");
                    if(reflectorCfg!=null){
                        block.reflector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ReflectorModule();
                        block.reflector.efficiency = reflectorCfg.getFloat("efficiency");
                        block.reflector.reflectivity = reflectorCfg.getFloat("reflectivity");
                    }
                    Config moderatorCfg = blockCfg.getConfig("moderator");
                    if(moderatorCfg!=null){
                        block.moderator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ModeratorModule();
                        block.moderator.flux = moderatorCfg.getInt("flux");
                        block.moderator.efficiency = moderatorCfg.getFloat("efficiency");
                    }
                    Config shieldCfg = blockCfg.getConfig("shield");
                    if(shieldCfg!=null){
                        block.neutronShield = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronShieldModule();
                        block.neutronShield.heatPerFlux = shieldCfg.getInt("heat");
                        block.neutronShield.efficiency = shieldCfg.getFloat("efficiency");
                        //TODO different definition
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block closed = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block(block.definition.copyTo(NCPFLegacyBlockElement::new));
                        block.neutronShield.closed = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(closed);
                        if(shieldCfg.hasProperty("closedTexture"))closed.texture.texture = loadNCPFTexture(shieldCfg.getConfigNumberList("closedTexture"));
                        additionalBlocks.add(closed);
                    }
                    Config heatsinkCfg = blockCfg.getConfig("heatsink");
                    if(heatsinkCfg!=null){
                        block.heatsink = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.HeatsinkModule();
                        block.heatsink.cooling = heatsinkCfg.getInt("cooling");
                    }
                    Config sourceCfg = blockCfg.getConfig("source");
                    if(sourceCfg!=null){
                        block.neutronSource = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronSourceModule();
                        block.neutronSource.efficiency = sourceCfg.getFloat("efficiency");
                    }
                    if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                    if(hasRecipes && (loadingAddon || !isAddon)){
                        Config portCfg = blockCfg.getConfig("port");
                        block.recipePorts = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.RecipePortsModule();
                        
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block input = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block(new NCPFLegacyBlockElement(portCfg.getString("name")));
                        block.recipePorts.input = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(input);
                        input.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule(false);
                        input.names.displayName = portCfg.getString("inputDisplayName");
                        if(portCfg.hasProperty("inputTexture"))input.texture.texture = loadNCPFTexture(portCfg.getConfigNumberList("inputTexture"));
                        configuration.blocks.add(input);
                        
                        //TODO different definition
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block(input.definition.copyTo(NCPFLegacyBlockElement::new));
                        block.recipePorts.output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference(output);
                        output.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.CasingModule(false);
                        output.names.displayName = portCfg.getString("outputDisplayName");
                        if(portCfg.hasProperty("outputTexture"))output.texture.texture = loadNCPFTexture(portCfg.getConfigNumberList("outputTexture"));
                        additionalBlocks.add(output);
                    }
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.getConfigList("rules");
                        for(int idx = 0; idx<rules.size(); idx++){
                            block.heatsink.rules.add(readOverSFRRule(rules.getConfig(idx)));
                        }
                    }
                }else{
                    isFuelCell = blockCfg.hasProperty("fuelCell");
                    isIrradiator = blockCfg.hasProperty("irradiator");
                }
                ConfigList recipes = blockCfg.getConfigList("recipes", new ConfigList());
                for(int idx = 0; idx<recipes.size(); idx++){
                    Config recipeCfg = recipes.get(idx);
                    Config inputCfg = recipeCfg.getConfig("input");
                    String name = inputCfg.getString("name");
                    DisplayNamesModule recipeNames = null;
                    TextureModule texture = null;
                    if(isFuelCell){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel(new NCPFLegacyItemElement(name));
                        //TODO output
                        recipeNames = fuel.names;
                        texture = fuel.texture;
                        Config recipeFuelCellCfg = recipeCfg.getConfig("fuelCell");
                        fuel.stats.efficiency = recipeFuelCellCfg.getFloat("efficiency");
                        fuel.stats.heat = recipeFuelCellCfg.getInt("heat");
                        fuel.stats.time = recipeFuelCellCfg.getInt("time");
                        fuel.stats.criticality = recipeFuelCellCfg.getInt("criticality");
                        fuel.stats.selfPriming = recipeFuelCellCfg.getBoolean("selfPriming", false);
                        block.fuels.add(fuel);
                    }
                    if(isIrradiator){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe(new NCPFLegacyItemElement(name));
                        //TODO output
                        recipeNames = recipe.names;
                        texture = recipe.texture;
                        Config recipeIrradiatorCfg = recipeCfg.getConfig("irradiator");
                        recipe.stats.efficiency = recipeIrradiatorCfg.getFloat("efficiency");
                        recipe.stats.heat = recipeIrradiatorCfg.getFloat("heat");
                        block.irradiatorRecipes.add(recipe);
                    }
                    recipeNames.displayName = inputCfg.getString("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipeNames.legacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                }
            }
            ConfigList coolantRecipes = fissionSFR.getConfigList("coolantRecipes");
            for(int i = 0; i<coolantRecipes.size(); i++){
                Config coolantRecipeCfg = coolantRecipes.getConfig(i);
                Config inputCfg = coolantRecipeCfg.getConfig("input");
                Config outputCfg = coolantRecipeCfg.getConfig("output");
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe(new NCPFLegacyFluidElement(inputCfg.getString("name")));
                //TODO output
                recipe.stats.heat = coolantRecipeCfg.getInt("heat");
                recipe.stats.outputRatio = coolantRecipeCfg.getFloat("outputRatio");
                recipe.names.displayName = inputCfg.getString("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.names.legacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                configuration.coolantRecipes.add(recipe);
            }
            project.setConfiguration(configuration);
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul MSR - load blocks">
    protected void loadOverhaulMSRBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings, boolean loadingAddon, boolean isAddon, List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block> additionalBlocks) {
        if(overhaul.hasProperty("fissionMSR")){
            OverhaulMSRConfiguration configuration = new OverhaulMSRConfiguration();
            Config fissionMSR = overhaul.getConfig("fissionMSR");
            if(loadSettings){
                configuration.settings.minSize = fissionMSR.getInt("minSize");
                configuration.settings.maxSize = fissionMSR.getInt("maxSize");
                configuration.settings.neutronReach = fissionMSR.getInt("neutronReach");
                configuration.settings.coolingEfficiencyLeniency = fissionMSR.getInt("coolingEfficiencyLeniency");
                configuration.settings.sparsityPenaltyMultiplier = fissionMSR.getFloat("sparsityPenaltyMult");
                configuration.settings.sparsityPenaltyThreshold = fissionMSR.getFloat("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fissionMSR.getConfigList("blocks");
            overhaulMSRPostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block theBlockThatThisBlockIsAnAddonRecipeBlockFor = null;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                if (loadingAddon) {
                    for (net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block b : project.getConfiguration(OverhaulMSRConfiguration::new).blocks) {
                        if (b.definition.matches(block.definition)) {
                            theBlockThatThisBlockIsAnAddonRecipeBlockFor = b;
                        }
                    }
                }
                boolean isFuelVessel = false;
                boolean isIrradiator = false;
                boolean isHeater = false;
                if(theBlockThatThisBlockIsAnAddonRecipeBlockFor==null){
                    block.names.displayName = blockCfg.getString("displayName");
                    if(blockCfg.hasProperty("legacyNames")){
                        ConfigList names = blockCfg.getConfigList("legacyNames");
                        for(int idx = 0; idx<names.size(); idx++){
                            block.names.legacyNames.add(names.get(idx));
                        }
                    }
                    if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule();
                    if(blockCfg.getBoolean("casing", false))block.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule(blockCfg.getBoolean("casingEdge", false));
                    if(blockCfg.getBoolean("controller", false))block.controller = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ControllerModule();
                    boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                    Config fuelVesselCfg = blockCfg.getConfig("fuelVessel");
                    if(fuelVesselCfg!=null)block.fuelVessel = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule();
                    Config irradiatorCfg = blockCfg.getConfig("irradiator");
                    if(irradiatorCfg!=null)block.irradiator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule();
                    Config reflectorCfg = blockCfg.getConfig("reflector");
                    if(reflectorCfg!=null){
                        block.reflector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule();
                        block.reflector.efficiency = reflectorCfg.getFloat("efficiency");
                        block.reflector.reflectivity = reflectorCfg.getFloat("reflectivity");
                    }
                    Config moderatorCfg = blockCfg.getConfig("moderator");
                    if(moderatorCfg!=null){
                        block.moderator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule();
                        block.moderator.flux = moderatorCfg.getInt("flux");
                        block.moderator.efficiency = moderatorCfg.getFloat("efficiency");
                    }
                    Config shieldCfg = blockCfg.getConfig("shield");
                    if(shieldCfg!=null){
                        block.neutronShield = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule();
                        block.neutronShield.heatPerFlux = shieldCfg.getInt("heat");
                        block.neutronShield.efficiency = shieldCfg.getFloat("efficiency");
                        //TODO different definition
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block closed = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block(block.definition.copyTo(NCPFLegacyBlockElement::new));
                        block.neutronShield.closed = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference(closed);
                        if(shieldCfg.hasProperty("closedTexture"))closed.texture.texture = loadNCPFTexture(shieldCfg.getConfigNumberList("closedTexture"));
                        additionalBlocks.add(closed);
                    }
                    Config heaterCfg = blockCfg.getConfig("heater");
                    if(heaterCfg!=null)block.heater = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule();
                    Config sourceCfg = blockCfg.getConfig("source");
                    if(sourceCfg!=null){
                        block.neutronSource = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronSourceModule();
                        block.neutronSource.efficiency = sourceCfg.getFloat("efficiency");
                    }
                    if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                    if(hasRecipes && (loadingAddon || !isAddon)){
                        Config portCfg = blockCfg.getConfig("port");
                        block.recipePorts = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.RecipePortsModule();
                        
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block input = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block(new NCPFLegacyBlockElement(portCfg.getString("name")));
                        block.recipePorts.input = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference(input);
                        input.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule(false);
                        input.names.displayName = portCfg.getString("inputDisplayName");
                        if(portCfg.hasProperty("inputTexture"))input.texture.texture = loadNCPFTexture(portCfg.getConfigNumberList("inputTexture"));
                        configuration.blocks.add(input);
                        
                        //TODO different definition
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block(input.definition.copyTo(NCPFLegacyBlockElement::new));
                        block.recipePorts.output = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockReference(output);
                        output.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.CasingModule(false);
                        output.names.displayName = portCfg.getString("outputDisplayName");
                        if(portCfg.hasProperty("outputTexture"))output.texture.texture = loadNCPFTexture(portCfg.getConfigNumberList("outputTexture"));
                        additionalBlocks.add(output);
                    }
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.getConfigList("rules");
                        for(int idx = 0; idx<rules.size(); idx++){
                            block.heater.rules.add(readOverMSRRule(rules.getConfig(idx)));
                        }
                    }
                }else{
                    isFuelVessel = blockCfg.hasProperty("fuelVessel");
                    isIrradiator = blockCfg.hasProperty("irradiator");
                    isHeater = blockCfg.hasProperty("heater");
                }
                ConfigList recipes = blockCfg.getConfigList("recipes", new ConfigList());
                for(int idx = 0; idx<recipes.size(); idx++){
                    Config recipeCfg = recipes.get(idx);
                    Config inputCfg = recipeCfg.getConfig("input");
                    String name = inputCfg.getString("name");
                    DisplayNamesModule recipeNames = null;
                    TextureModule texture = null;
                    if(isFuelVessel){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel(new NCPFLegacyFluidElement(name));
                        recipeNames = fuel.names;
                        texture = fuel.texture;
                        Config recipeFuelVesselCfg = recipeCfg.getConfig("fuelVessel");
                        fuel.stats.efficiency = recipeFuelVesselCfg.getFloat("efficiency");
                        fuel.stats.heat = recipeFuelVesselCfg.getInt("heat");
                        fuel.stats.time = recipeFuelVesselCfg.getInt("time");
                        fuel.stats.criticality = recipeFuelVesselCfg.getInt("criticality");
                        fuel.stats.selfPriming = recipeFuelVesselCfg.getBoolean("selfPriming", false);
                        block.fuels.add(fuel);
                    }
                    if(isIrradiator){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe(new NCPFLegacyItemElement(name));
                        recipeNames = recipe.names;
                        texture = recipe.texture;
                        Config recipeIrradiatorCfg = recipeCfg.getConfig("irradiator");
                        recipe.stats.efficiency = recipeIrradiatorCfg.getFloat("efficiency");
                        recipe.stats.heat = recipeIrradiatorCfg.getFloat("heat");
                        block.irradiatorRecipes.add(recipe);
                    }
                    if(isHeater){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe(new NCPFLegacyFluidElement(name));
                        recipeNames = recipe.names;
                        texture = recipe.texture;
                        Config recipeHeaterCfg = recipeCfg.getConfig("heater");
                        recipe.stats.cooling = recipeHeaterCfg.getInt("cooling");
                        block.heaterRecipes.add(recipe);
                    }
                    recipeNames.displayName = inputCfg.getString("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipeNames.legacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                }
            }
            project.setConfiguration(configuration);
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine - load blocks">
    protected void loadOverhaulTurbineBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings) {
        if(overhaul.hasProperty("turbine")){
            OverhaulTurbineConfiguration configuration = new OverhaulTurbineConfiguration();
            Config turbine = overhaul.getConfig("turbine");
            if(loadSettings){
                configuration.settings.minWidth = turbine.getInt("minWidth");
                configuration.settings.minLength = turbine.getInt("minLength");
                configuration.settings.maxSize = turbine.getInt("maxSize");
                configuration.settings.fluidPerBlade = turbine.getInt("fluidPerBlade");
                configuration.settings.throughputEfficiencyLeniencyMultiplier = turbine.getFloat("throughputEfficiencyLeniencyMult");
                configuration.settings.throughputEfficiencyLeniencyThreshold = turbine.getFloat("throughputEfficiencyLeniencyThreshold");
                configuration.settings.throughputFactor = turbine.getFloat("throughputFactor");
                configuration.settings.powerBonus = turbine.getFloat("powerBonus");
            }
            ConfigList blocks = turbine.getConfigList("blocks");
            overhaulTurbinePostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Block(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                block.names.displayName = blockCfg.getString("displayName");
                if(blockCfg.hasProperty("legacyNames")){
                    ConfigList names = blockCfg.getConfigList("legacyNames");
                    for(int idx = 0; idx<names.size(); idx++){
                        block.names.legacyNames.add(names.get(idx));
                    }
                }
                Config bladeCfg = blockCfg.getConfig("blade");
                if(bladeCfg!=null){
                    if(bladeCfg.getBoolean("stator", false)){
                        block.stator = new StatorModule();
                        block.stator.expansion = bladeCfg.getFloat("expansion");
                    }else{
                        block.blade = new BladeModule();
                        block.blade.efficiency = bladeCfg.getFloat("efficiency");
                        block.blade.expansion = bladeCfg.getFloat("expansion");
                    }
                }
                Config coilCfg = blockCfg.getConfig("coil");
                if(coilCfg!=null){
                    block.coil = new CoilModule();
                    block.coil.efficiency = coilCfg.getFloat("efficiency");
                }
                if(blockCfg.getBoolean("bearing", false))block.bearing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule();
                if(blockCfg.getBoolean("shaft", false))block.shaft = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ShaftModule();
                if(blockCfg.getBoolean("connector", false))block.connector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule();
                if(blockCfg.getBoolean("controller", false))block.controller = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ControllerModule();
                if(blockCfg.getBoolean("casing", false))block.casing = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule(blockCfg.get("casingEdge", false));
                if(blockCfg.getBoolean("inlet", false))block.inlet = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.InletModule();
                if(blockCfg.getBoolean("outlet", false))block.outlet = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.OutletModule();
                if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.getConfigList("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.coil.rules.add(readOverTurbineRule(rules.getConfig(idx)));
                    }
                }
            }
            ConfigList recipes = turbine.getConfigList("recipes");
            for(int i = 0; i<recipes.size(); i++){
                Config recipeCfg = recipes.getConfig(i);
                Config inputCfg = recipeCfg.getConfig("input");
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe(new NCPFLegacyFluidElement(inputCfg.getString("name")));
                recipe.stats.power = recipeCfg.getDouble("power");
                recipe.stats.coefficient = recipeCfg.getDouble("coefficient");
                recipe.names.displayName = inputCfg.getString("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.names.legacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                configuration.recipes.add(recipe);
            }
            project.setConfiguration(configuration);
        }
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Overhaul Fusion Generator - load blocks">
    protected void loadOverhaulFusionGeneratorBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings) {
        if(overhaul.hasProperty("fusion")){
            OverhaulFusionConfiguration configuration = new OverhaulFusionConfiguration();
            Config fusion = overhaul.getConfig("fusion");
            if(loadSettings){
                configuration.settings.minInnerRadius = fusion.getInt("minInnerRadius");
                configuration.settings.maxInnerRadius = fusion.getInt("maxInnerRadius");
                configuration.settings.minCoreSize = fusion.getInt("minCoreSize");
                configuration.settings.maxCoreSize = fusion.getInt("maxCoreSize");
                configuration.settings.minToroidWidth = fusion.getInt("minToroidWidth");
                configuration.settings.maxToroidWidth = fusion.getInt("maxToroidWidth");
                configuration.settings.minLiningThickness = fusion.getInt("minLiningThickness");
                configuration.settings.maxLiningThickness = fusion.getInt("maxLiningThickness");
                configuration.settings.coolingEfficiencyLeniency = fusion.getInt("coolingEfficiencyLeniency");
                configuration.settings.sparsityPenaltyMultiplier = fusion.getFloat("sparsityPenaltyMult");
                configuration.settings.sparsityPenaltyThreshold = fusion.getFloat("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fusion.getConfigList("blocks");
            overhaulFusionPostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Block(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                block.names.displayName = blockCfg.getString("displayName");
                if(blockCfg.hasProperty("legacyNames")){
                    ConfigList names = blockCfg.getConfigList("legacyNames");
                    for(int idx = 0; idx<names.size(); idx++){
                        block.names.legacyNames.add(names.get(idx));
                    }
                }
                if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConductorModule();
                if(blockCfg.getBoolean("connector", false))block.connector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConnectorModule();
                if(blockCfg.getBoolean("core", false))block.core = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.CoreModule();
                if(blockCfg.getBoolean("electromagnet", false)){
                    block.toroid = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ToroidalElectromagnetModule();
                    block.poloid = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.PoloidalElectromagnetModule();
                }
                if(blockCfg.getBoolean("heatingBlanket", false))block.heatingBlanket = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatingBlanketModule();
                boolean hasRecipes = blockCfg.getConfigList("recipes", new ConfigList()).size()>0;
                Config breedingBlanketCfg = blockCfg.getConfig("breedingBlanket");
                if(breedingBlanketCfg!=null)block.breedingBlanket = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketModule();
                Config shieldingCfg = blockCfg.getConfig("shielding");
                if(shieldingCfg!=null){
                    block.shielding = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ShieldingModule();
                    block.shielding.shieldiness = shieldingCfg.getFloat("shieldiness");
                }
                Config reflectorCfg = blockCfg.getConfig("reflector");
                if(reflectorCfg!=null){
                    block.reflector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ReflectorModule();
                    block.reflector.efficiency = reflectorCfg.getFloat("efficiency");
                }
                Config heatsinkCfg = blockCfg.getConfig("heatsink");
                if(heatsinkCfg!=null){
                    block.heatsink = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatsinkModule();
                    block.heatsink.cooling = heatsinkCfg.getInt("cooling");
                }
                if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.getConfigList("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.heatsink.rules.add(readOverFusionRule(rules.getConfig(idx)));
                    }
                }
                ConfigList recipes = blockCfg.getConfigList("recipes", new ConfigList());
                for(int idx = 0; idx<recipes.size(); idx++){
                    Config recipeCfg = recipes.get(idx);
                    Config inputCfg = recipeCfg.getConfig("input");
                    Config outputCfg = recipeCfg.getConfig("output");
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe(new NCPFLegacyItemElement(inputCfg.getString("name")));
                    recipe.names.displayName = inputCfg.getString("displayName");
                    if(inputCfg.hasProperty("legacyNames")){
                        ConfigList names = inputCfg.getConfigList("legacyNames");
                        for(int j = 0; j<names.size(); j++){
                            recipe.names.legacyNames.add(names.get(j));
                        }
                    }
                    if(inputCfg.hasProperty("texture"))recipe.texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                    Config recipeBreedingBlanketCfg = recipeCfg.getConfig("breedingBlanket");
                    recipe.stats.augmented = recipeBreedingBlanketCfg.getBoolean("augmented", false);
                    recipe.stats.efficiency = recipeBreedingBlanketCfg.getFloat("efficiency");
                    recipe.stats.heat = recipeBreedingBlanketCfg.getFloat("heat");
                    block.breedingBlanketRecipes.add(recipe);
                }
            }
            ConfigList recipes = fusion.getConfigList("recipes");
            for(int i = 0; i<recipes.size(); i++){
                Config recipeCfg = recipes.getConfig(i);
                Config inputCfg = recipeCfg.getConfig("input");
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe(new NCPFLegacyFluidElement(inputCfg.getString("name")));
                recipe.stats.efficiency = recipeCfg.getFloat("efficiency");
                recipe.stats.heat = recipeCfg.getInt("heat");
                recipe.stats.time = recipeCfg.getInt("time");
                recipe.stats.fluxiness = recipeCfg.getFloat("fluxiness");
                recipe.names.displayName = inputCfg.getString("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.names.legacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                configuration.recipes.add(recipe);
            }
            ConfigList coolantRecipes = fusion.getConfigList("coolantRecipes");
            for(int i = 0; i<coolantRecipes.size(); i++){
                Config coolantRecipeCfg = coolantRecipes.getConfig(i);
                Config inputCfg = coolantRecipeCfg.getConfig("input");
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe(new NCPFLegacyFluidElement(inputCfg.getString("name")));
                recipe.stats.heat = coolantRecipeCfg.getInt("heat");
                recipe.stats.outputRatio = coolantRecipeCfg.getFloat("outputRatio");
                recipe.names.displayName = inputCfg.getString("displayName");
                if(inputCfg.hasProperty("legacyNames")){
                    ConfigList names = inputCfg.getConfigList("legacyNames");
                    for(int j = 0; j<names.size(); j++){
                        recipe.names.legacyNames.add(names.get(j));
                    }
                }
                if(inputCfg.hasProperty("texture"))recipe.texture.texture = loadNCPFTexture(inputCfg.getConfigNumberList("texture"));
                configuration.coolantRecipes.add(recipe);
            }
        }
    }
//</editor-fold>

    public static Image loadNCPFTexture(ConfigNumberList texture){
        int size = (int) texture.get(0);
        Image image = new Image(size, size);
        int index = 1;
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                image.setRGB(x, image.getHeight()-y-1, (int)texture.get(index));//flip Y axis because GL
                index++;
            }
        }
        return image;
    }
}