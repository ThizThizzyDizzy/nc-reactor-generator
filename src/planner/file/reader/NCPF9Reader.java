package planner.file.reader;
import java.util.ArrayList;
import java.util.Iterator;
import multiblock.Multiblock;
import multiblock.configuration.Configuration;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class NCPF9Reader extends NCPF10Reader {
    @Override
    protected byte getTargetVersion() {
        return (byte) 9;
    }

    protected int parseInputRate(Config blockCfg) {
        return blockCfg.get("inputRate", 0);
    }
    protected int parseOutputRate(Config blockCfg) {
        return blockCfg.get("outputRate", 0);
    }

    protected void loadTurbineEfficiencyFactors(Config turbine, Configuration configuration) {
        configuration.overhaul.turbine.throughputEfficiencyLeniencyMult = turbine.get("throughputEfficiencyLeniencyMult");
        configuration.overhaul.turbine.throughputEfficiencyLeniencyThreshold = turbine.get("throughputEfficiencyLeniencyThreshold");
    }

    protected float readOutputRatio(Config config, String name) {
        return config.getFloat(name);
    }
    protected boolean readBladeStator(multiblock.configuration.overhaul.turbine.Block blade, Config config, String name) {
        return config.get(name);
    }

    @Override
    protected synchronized Multiblock readMultiblockUnderhaulSFR(NCPFFile ncpf, Config data) {
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
        return underhaulSFR;
    }
    @Override
    protected synchronized Multiblock readMultiblockOverhaulSFR(NCPFFile ncpf, Config data) {
        ConfigNumberList size = data.get("size");
        OverhaulSFR overhaulSFR = new OverhaulSFR(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
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
        return overhaulSFR;
    }
    @Override
    protected synchronized Multiblock readMultiblockOverhaulMSR(NCPFFile ncpf, Config data) {
        ConfigNumberList size = data.get("size");
        OverhaulMSR overhaulMSR = new OverhaulMSR(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
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
        ConfigNumberList fuels = data.get("fuels");
        ConfigNumberList sources = data.get("sources");
        ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
        int fuelIndex = 0;
        int sourceIndex = 0;
        int recipeIndex = 0;
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
        return overhaulMSR;
    }
    @Override
    protected synchronized Multiblock readMultiblockOverhaulTurbine(NCPFFile ncpf, Config data) {
        ConfigNumberList size = data.get("size");
        OverhaulTurbine overhaulTurbine = new OverhaulTurbine(ncpf.configuration, (int)size.get(0), (int)size.get(1), ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", (byte)-1)));
        overhaulTurbine.setBearing((int)size.get(2));
        if(data.hasProperty("inputs")){
            overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
            ConfigNumberList inputs = data.get("inputs");
            for(Number number : inputs.iterable()){
                overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
            }
        }
        ArrayList<multiblock.configuration.overhaul.turbine.Block> allCoils = new ArrayList<>();
        ArrayList<multiblock.configuration.overhaul.turbine.Block> allBlades = new ArrayList<>();
        for(multiblock.configuration.overhaul.turbine.Block b : ncpf.configuration.overhaul.turbine.allBlocks){
            if(b.blade)allBlades.add(b);
            else allCoils.add(b);
        }
        ConfigNumberList coils = data.get("coils");
        int index = 0;
        for(int z = 0; z<2; z++){
            if(z==1)z = overhaulTurbine.getExternalDepth()-1;
            for(int x = 1; x<=overhaulTurbine.getInternalWidth(); x++){
                for(int y = 1; y<=overhaulTurbine.getInternalHeight(); y++){
                    int bid = (int) coils.get(index);
                    if(bid>0){
                        overhaulTurbine.setBlockExact(x, y, z, new multiblock.overhaul.turbine.Block(ncpf.configuration, x, y, z, allCoils.get(bid-1)));
                    }
                    index++;
                }
            }
        }
        ConfigNumberList blades = data.get("blades");
        index = 0;
        for(int z = 1; z<=overhaulTurbine.getInternalDepth(); z++){
            int bid = (int) blades.get(index);
            if(bid>0){
                overhaulTurbine.setBlade((int)size.get(2), z, allBlades.get(bid-1));
            }
            index++;
        }
        return overhaulTurbine;
    }
    @Override
    protected synchronized Multiblock readMultiblockOverhaulFusionReactor(NCPFFile ncpf, Config data) {
        ConfigNumberList size = data.get("size");
        OverhaulFusionReactor overhaulFusionReactor = new OverhaulFusionReactor(ncpf.configuration, (int)size.get(0),(int)size.get(1),(int)size.get(2),(int)size.get(3),ncpf.configuration.overhaul.fusion.allRecipes.get(data.get("recipe", (byte)-1)),ncpf.configuration.overhaul.fusion.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
        ConfigNumberList blocks = data.get("blocks");
        int[] findex = new int[1];
        overhaulFusionReactor.forEachPosition((X, Y, Z) -> {
            int bid = (int)blocks.get(findex[0]);
            if(bid>0)overhaulFusionReactor.setBlockExact(X, Y, Z, new multiblock.overhaul.fusion.Block(ncpf.configuration, X, Y, Z, ncpf.configuration.overhaul.fusion.allBlocks.get(bid-1)));
            findex[0]++;
        });
        ConfigNumberList breedingBlanketRecipes = data.get("breedingBlanketRecipes");
        int recipeIndex = 0;
        for(multiblock.overhaul.fusion.Block block : overhaulFusionReactor.getBlocks()){
            if(block.template.breedingBlanket){
                int rid = (int) breedingBlanketRecipes.get(recipeIndex);
                if(rid>0)block.recipe = block.template.allRecipes.get(rid-1);
                recipeIndex++;
            }
        }
        return overhaulFusionReactor;
    }

    @Override
    protected void loadUnderhaulBlocks(Config config, Configuration parent, Configuration configuration, boolean loadSettings) {
        if(config.hasProperty("underhaul")){
            configuration.underhaul = new UnderhaulConfiguration();
            Config underhaul = config.get("underhaul");
            if(underhaul.hasProperty("fissionSFR")){
                configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                Config fissionSFR = underhaul.get("fissionSFR");
                if(loadSettings){
                    configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                    configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                    configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                    configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                    configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                    configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                }
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
                    parent.underhaul.fissionSFR.allFuels.add(fuel);configuration.underhaul.fissionSFR.fuels.add(fuel);
                }
            }
        }
    }
    @Override
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
                parent.overhaul.fissionSFR.allBlocks.add(block);configuration.overhaul.fissionSFR.blocks.add(block);
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
            if (loadingAddon) {
                for (multiblock.configuration.overhaul.fissionsfr.Block b : parent.overhaul.fissionSFR.allBlocks) {
                    if (!b.allRecipes.isEmpty()) {
                        multiblock.configuration.overhaul.fissionsfr.Block bl = new multiblock.configuration.overhaul.fissionsfr.Block(b.name);
                        bl.fuelCell = b.fuelCell;
                        bl.irradiator = b.irradiator;
                        configuration.overhaul.fissionSFR.allBlocks.add(bl);
                    }
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
                for(multiblock.configuration.overhaul.fissionsfr.Block b : parent.overhaul.fissionSFR.allBlocks){
                    if(b.fuelCell){
                        b.allRecipes.add(fuel);
                    }
                }
                for(multiblock.configuration.overhaul.fissionsfr.Block b : configuration.overhaul.fissionSFR.allBlocks){
                    if(b.fuelCell){
                        b.recipes.add(fuel);
                    }
                }
            }
            ConfigList sources = fissionSFR.get("sources");
            for(Iterator sit = sources.iterator(); sit.hasNext();){
                Config sourceCfg = (Config)sit.next();
                multiblock.configuration.overhaul.fissionsfr.Block source = new multiblock.configuration.overhaul.fissionsfr.Block(sourceCfg.get("name"));
                source.source = true;
                source.sourceEfficiency = sourceCfg.get("efficiency");
                parent.overhaul.fissionSFR.allBlocks.add(source);configuration.overhaul.fissionSFR.blocks.add(source);
            }
            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                Config irradiatorRecipeCfg = (Config)irit.next();
                multiblock.configuration.overhaul.fissionsfr.BlockRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.BlockRecipe(irradiatorRecipeCfg.get("name"), "null");
                irrecipe.irradiatorEfficiency = irradiatorRecipeCfg.get("efficiency");
                irrecipe.irradiatorHeat = irradiatorRecipeCfg.get("heat");
                for(multiblock.configuration.overhaul.fissionsfr.Block b : parent.overhaul.fissionSFR.allBlocks){
                    if(b.irradiator){
                        b.allRecipes.add(irrecipe);
                    }
                }
                for(multiblock.configuration.overhaul.fissionsfr.Block b : configuration.overhaul.fissionSFR.allBlocks){
                    if(b.irradiator){
                        b.recipes.add(irrecipe);
                    }
                }
            }
            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                Config coolantRecipeCfg = (Config)irit.next();
                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), readOutputRatio(coolantRecipeCfg, "outputRatio"));
                parent.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
            }
            for(multiblock.configuration.overhaul.fissionsfr.Block b : parent.overhaul.fissionSFR.allBlocks){
                if(!b.allRecipes.isEmpty()){
                    b.port = new multiblock.configuration.overhaul.fissionsfr.Block("null");
                }
            }
            if(configuration.addon){
                multiblock.configuration.overhaul.fissionsfr.Block cell = new multiblock.configuration.overhaul.fissionsfr.Block("Fuel Cell");
                cell.fuelCell = true;
                configuration.overhaul.fissionSFR.allBlocks.add(cell);
                cell.allRecipes.add(new multiblock.configuration.overhaul.fissionsfr.BlockRecipe("",""));
                multiblock.configuration.overhaul.fissionsfr.Block irradiator = new multiblock.configuration.overhaul.fissionsfr.Block("Neutron Irradiator");
                irradiator.irradiator = true;
                irradiator.allRecipes.add(new multiblock.configuration.overhaul.fissionsfr.BlockRecipe("",""));
                configuration.overhaul.fissionSFR.allBlocks.add(irradiator);
            }
        }
    }
    @Override
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
                multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                int cooling = blockCfg.get("cooling", 0);
                if(cooling!=0){
                    block.heater = true;
                    multiblock.configuration.overhaul.fissionmsr.BlockRecipe recipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(blockCfg.get("input", ""), blockCfg.get("output", ""));
                    recipe.heaterCooling = cooling;
                    recipe.inputRate = parseInputRate(blockCfg);
                    recipe.outputRate = parseOutputRate(blockCfg);
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
                parent.overhaul.fissionMSR.allBlocks.add(block);configuration.overhaul.fissionMSR.blocks.add(block);
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
            if (loadingAddon) {
                for (multiblock.configuration.overhaul.fissionmsr.Block b : parent.overhaul.fissionMSR.allBlocks) {
                    if (!b.allRecipes.isEmpty()) {
                        multiblock.configuration.overhaul.fissionmsr.Block bl = new multiblock.configuration.overhaul.fissionmsr.Block(b.name);
                        bl.fuelVessel = b.fuelVessel;
                        bl.irradiator = b.irradiator;
                        configuration.overhaul.fissionMSR.allBlocks.add(bl);
                    }
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
                for(multiblock.configuration.overhaul.fissionmsr.Block b : parent.overhaul.fissionMSR.allBlocks){
                    if(b.fuelVessel){
                        b.allRecipes.add(fuel);
                    }
                }
                for(multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(b.fuelVessel){
                        b.recipes.add(fuel);
                    }
                }
            }
            ConfigList sources = fissionMSR.get("sources");
            for(Iterator sit = sources.iterator(); sit.hasNext();){
                Config sourceCfg = (Config)sit.next();
                multiblock.configuration.overhaul.fissionmsr.Block source = new multiblock.configuration.overhaul.fissionmsr.Block(sourceCfg.get("name"));
                source.source = true;
                source.sourceEfficiency = sourceCfg.get("efficiency");
                parent.overhaul.fissionMSR.allBlocks.add(source);configuration.overhaul.fissionMSR.blocks.add(source);
            }
            ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                Config irradiatorRecipeCfg = (Config)irit.next();
                multiblock.configuration.overhaul.fissionmsr.BlockRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.BlockRecipe(irradiatorRecipeCfg.get("name"), "null");
                irrecipe.irradiatorEfficiency = irradiatorRecipeCfg.get("efficiency");
                irrecipe.irradiatorHeat = irradiatorRecipeCfg.get("heat");
                for(multiblock.configuration.overhaul.fissionmsr.Block b : parent.overhaul.fissionMSR.allBlocks){
                    if(b.irradiator){
                        b.allRecipes.add(irrecipe);
                    }
                }
                for(multiblock.configuration.overhaul.fissionmsr.Block b : configuration.overhaul.fissionMSR.allBlocks){
                    if(b.irradiator){
                        b.recipes.add(irrecipe);
                    }
                }
            }
            for(multiblock.configuration.overhaul.fissionmsr.Block b : parent.overhaul.fissionMSR.allBlocks){
                if(!b.allRecipes.isEmpty()){
                    b.port = new multiblock.configuration.overhaul.fissionmsr.Block("null");
                }
            }
            if(configuration.addon){
                multiblock.configuration.overhaul.fissionmsr.Block vessel = new multiblock.configuration.overhaul.fissionmsr.Block("Fuel Vessel");
                vessel.fuelVessel = true;
                configuration.overhaul.fissionMSR.allBlocks.add(vessel);
                vessel.allRecipes.add(new multiblock.configuration.overhaul.fissionmsr.BlockRecipe("",""));
                multiblock.configuration.overhaul.fissionmsr.Block irradiator = new multiblock.configuration.overhaul.fissionmsr.Block("Neutron Irradiator");
                irradiator.irradiator = true;
                irradiator.allRecipes.add(new multiblock.configuration.overhaul.fissionmsr.BlockRecipe("",""));
                configuration.overhaul.fissionMSR.allBlocks.add(irradiator);
            }
        }
    }
    @Override
    protected void loadOverhaulTurbineBlocks(Config overhaul, Configuration parent, Configuration configuration, boolean loadSettings) {
        if(overhaul.hasProperty("turbine")){
            configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
            Config turbine = overhaul.get("turbine");
            if(loadSettings){
                configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                configuration.overhaul.turbine.minLength = turbine.get("minLength");
                configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                loadTurbineEfficiencyFactors(turbine, configuration);
                configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
            }
            ConfigList coils = turbine.get("coils");
            overhaulTurbinePostLoadMap.clear();
            for(Iterator bit = coils.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.turbine.Block coil = new multiblock.configuration.overhaul.turbine.Block(blockCfg.get("name"));
                coil.bearing = blockCfg.get("bearing", false);
                coil.connector = blockCfg.get("connector", false);
                float eff = blockCfg.get("efficiency");
                if(eff>0){
                    coil.coil = true;
                    coil.coilEfficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.hasProperty("texture"))coil.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                        Config ruleCfg = (Config)rit.next();
                        coil.rules.add(readOverTurbineRule(ruleCfg));
                    }
                }
                parent.overhaul.turbine.allBlocks.add(coil);configuration.overhaul.turbine.blocks.add(coil);
            }
            ConfigList blades = turbine.get("blades");
            for(Iterator bit = blades.iterator(); bit.hasNext();){
                Config blockCfg = (Config)bit.next();
                multiblock.configuration.overhaul.turbine.Block blade = new multiblock.configuration.overhaul.turbine.Block(blockCfg.get("name"));
                blade.blade = true;
                blade.bladeExpansion = blockCfg.get("expansion");
                blade.bladeEfficiency = blockCfg.get("efficiency");
                blade.bladeStator = readBladeStator(blade, blockCfg, "stator");
                if(blockCfg.hasProperty("texture"))blade.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                parent.overhaul.turbine.allBlocks.add(blade);configuration.overhaul.turbine.blocks.add(blade);
            }
            ArrayList<multiblock.configuration.overhaul.turbine.Block> allCoils = new ArrayList<>();
            ArrayList<multiblock.configuration.overhaul.turbine.Block> allBlades = new ArrayList<>();
            for(multiblock.configuration.overhaul.turbine.Block b : parent.overhaul.turbine.allBlocks){
                if(b.blade)allBlades.add(b);
                else allCoils.add(b);
            }
            for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                int index = overhaulTurbinePostLoadMap.get(rule);
                if(index==0){
                    rule.isSpecificBlock = false;
                    rule.blockType = multiblock.configuration.overhaul.turbine.PlacementRule.BlockType.CASING;
                }else{
                    rule.block = allCoils.get(index-1);
                }
            }
            ConfigList recipes = turbine.get("recipes");
            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                Config recipeCfg = (Config)irit.next();
                multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(recipeCfg.get("input"), recipeCfg.get("output"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                parent.overhaul.turbine.allRecipes.add(recipe);configuration.overhaul.turbine.recipes.add(recipe);
            }
        }
    }
    @Override
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
                int cooling = blockCfg.get("cooling", 0);
                if(cooling!=0){
                    block.heatsink = true;
                    block.heatsinkHasBaseStats = true;
                    block.heatsinkCooling = cooling;
                }
                block.cluster = blockCfg.get("cluster", false);
                block.createCluster = blockCfg.get("createCluster", false);
                block.conductor = blockCfg.get("conductor", false);
                block.core = blockCfg.get("core", false);
                block.connector = blockCfg.get("connector", false);
                block.electromagnet = blockCfg.get("electromagnet", false);
                block.heatingBlanket = blockCfg.get("heatingBlanket", false);
                if(blockCfg.get("reflector", false)){
                    block.reflector = true;
                    block.reflectorHasBaseStats = true;
                    block.reflectorEfficiency = blockCfg.get("efficiency");
                }
                block.breedingBlanket = blockCfg.get("breedingBlanket", false);
                block.breedingBlanketAugmented = blockCfg.get("augmentedBreedingBlanket", false);
                if(blockCfg.get("shielding", false)){
                    block.shielding = true;
                    block.shieldingHasBaseStats = true;
                    block.shieldingShieldiness = blockCfg.get("shieldiness");
                }
                block.functional = blockCfg.get("functional");
                if(blockCfg.hasProperty("texture"))block.setTexture(loadNCPFTexture(blockCfg.get("texture")));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                        Config ruleCfg = (Config)rit.next();
                        block.rules.add(readOverFusionRule(ruleCfg));
                    }
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
            ConfigList breedingBlanketRecipes = fusion.get("breedingBlanketRecipes");
            for(Iterator irit = breedingBlanketRecipes.iterator(); irit.hasNext();){
                Config breedingBlanketRecipeCfg = (Config)irit.next();
                for(multiblock.configuration.overhaul.fusion.Block b : configuration.overhaul.fusion.allBlocks){
                    if(b.breedingBlanket){
                        multiblock.configuration.overhaul.fusion.BlockRecipe breebrecipe = new multiblock.configuration.overhaul.fusion.BlockRecipe(breedingBlanketRecipeCfg.get("name"), "null");
                        breebrecipe.breedingBlanketEfficiency = breedingBlanketRecipeCfg.get("efficiency");
                        breebrecipe.breedingBlanketHeat = breedingBlanketRecipeCfg.get("heat");
                        breebrecipe.breedingBlanketAugmented = b.breedingBlanketAugmented;
                        b.allRecipes.add(breebrecipe);b.recipes.add(breebrecipe);
                    }
                }
            }
            ConfigList recipes = fusion.get("recipes");
            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                Config recipeCfg = (Config)irit.next();
                multiblock.configuration.overhaul.fusion.Recipe recipe = new multiblock.configuration.overhaul.fusion.Recipe(recipeCfg.get("name"), "null", recipeCfg.get("efficiency"), recipeCfg.get("heat"), recipeCfg.get("time"), recipeCfg.getFloat("fluxiness"));
                configuration.overhaul.fusion.allRecipes.add(recipe);configuration.overhaul.fusion.recipes.add(recipe);
            }
            ConfigList coolantRecipes = fusion.get("coolantRecipes");
            for(Iterator coit = coolantRecipes.iterator(); coit.hasNext();){
                Config coolantRecipeCfg = (Config)coit.next();
                multiblock.configuration.overhaul.fusion.CoolantRecipe coolantRecipe = new multiblock.configuration.overhaul.fusion.CoolantRecipe(coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), readOutputRatio(coolantRecipeCfg, "outputRatio"));
                configuration.overhaul.fusion.allCoolantRecipes.add(coolantRecipe);configuration.overhaul.fusion.coolantRecipes.add(coolantRecipe);
            }
        }
    }
}