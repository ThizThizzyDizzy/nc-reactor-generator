package net.ncplanner.plannerator.planner.file.reader;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyFluidElement;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyItemElement;
import static net.ncplanner.plannerator.planner.file.reader.LegacyNCPF11Reader.loadNCPFTexture;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulFusionConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulFusionDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulTurbineDesign;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.BreedingBlanketModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatsinkModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ReflectorModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ShieldingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BladeModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule;
import net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.StatorModule;
public class LegacyNCPF9Reader extends LegacyNCPF10Reader {
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

    protected void loadTurbineEfficiencyFactors(Config turbine, OverhaulTurbineConfiguration configuration) {
        configuration.settings.throughputEfficiencyLeniencyMultiplier = turbine.get("throughputEfficiencyLeniencyMult");
        configuration.settings.throughputEfficiencyLeniencyThreshold = turbine.get("throughputEfficiencyLeniencyThreshold");
    }

    protected float readOutputRatio(Config config, String name) {
        return config.getFloat(name);
    }
    protected boolean readBladeStator(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement blade, Config config, String name) {
        return config.get(name);
    }

    @Override
    protected synchronized Design readMultiblockUnderhaulSFR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList size = data.get("size");
        UnderhaulSFRDesign underhaulSFR = new UnderhaulSFRDesign(ncpf, (int)size.get(0),(int)size.get(1),(int)size.get(2));
        underhaulSFR.fuel = recovery.recoverUnderhaulSFRFuelLegacyNCPF(ncpf, data.getByte("fuel", (byte)-1));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 1; x<underhaulSFR.design.length-1; x++){
                for(int y = 1; y<underhaulSFR.design[x].length-1; y++){
                    for(int z = 1; z<underhaulSFR.design[x][y].length-1; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement b = recovery.recoverUnderhaulSFRBlockLegacyNCPF(ncpf, bid-1);
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
                net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement b = recovery.recoverUnderhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)underhaulSFR.design[x][y][z] = b;
            }
        }
        return underhaulSFR;
    }
    @Override
    protected synchronized Design readMultiblockOverhaulSFR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList size = data.get("size");
        OverhaulSFRDesign overhaulSFR = new OverhaulSFRDesign(ncpf, (int)size.get(0),(int)size.get(1),(int)size.get(2));
        overhaulSFR.coolantRecipe = recovery.recoverOverhaulSFRCoolantRecipeLegacyNCPF(ncpf, data.getByte("coolantRecipe", (byte)-1));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 1; x<overhaulSFR.design.length; x++){
                for(int y = 1; y<overhaulSFR.design[x].length; y++){
                    for(int z = 1; z<overhaulSFR.design[x][y].length; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b = recovery.recoverOverhaulSFRBlockLegacyNCPF(ncpf, bid-1);
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
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b = recovery.recoverOverhaulSFRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)overhaulSFR.design[x][y][z] = b;
            }
        }
        ConfigNumberList fuels = data.get("fuels");
        ConfigNumberList sources = data.get("sources");
        ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
        int fuelIndex = 0;
        int sourceIndex = 0;
        int recipeIndex = 0;
        ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement> srces = new ArrayList<>();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement bl : ncpf.getConfiguration(OverhaulSFRConfiguration::new).blocks){
            if(bl.neutronSource!=null)srces.add(bl);
        }
        for(int x = 1; x<overhaulSFR.design.length; x++){
            for(int y = 1; y<overhaulSFR.design[x].length; y++){
                for(int z = 1; z<overhaulSFR.design[x][y].length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block = overhaulSFR.design[x][y][z];
                    if(block.fuelCell!=null){
                        overhaulSFR.fuels[x][y][z] = recovery.recoverOverhaulSFRBlockRecipeLegacyNCPF(ncpf, block, (int)fuels.get(fuelIndex));
                        fuelIndex++;
                        int sid = (int) sources.get(sourceIndex);
                        if(sid>0)LegacyNeutronSourceHandler.addNeutronSource(overhaulSFR, x, y, z, srces.get(sid-1));
                        sourceIndex++;
                    }
                    if(block.irradiator!=null){
                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                        if(rid>0)overhaulSFR.irradiatorRecipes[x][y][z] = recovery.recoverOverhaulSFRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                }
            }
        }
        return overhaulSFR;
    }
    @Override
    protected synchronized Design readMultiblockOverhaulMSR(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList size = data.get("size");
        OverhaulMSRDesign overhaulMSR = new OverhaulMSRDesign(ncpf, (int)size.get(0),(int)size.get(1),(int)size.get(2));
        boolean compact = data.get("compact");
        ConfigNumberList blocks = data.get("blocks");
        if(compact){
            int[] index = new int[1];
            for(int x = 1; x<overhaulMSR.design.length; x++){
                for(int y = 1; y<overhaulMSR.design[x].length; y++){
                    for(int z = 1; z<overhaulMSR.design[x][y].length; z++){
                        int bid = (int) blocks.get(index[0]);
                        if(bid>0){
                            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b = recovery.recoverOverhaulMSRBlockLegacyNCPF(ncpf, bid-1);
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
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b = recovery.recoverOverhaulMSRBlockLegacyNCPF(ncpf, bid-1);
                if(b!=null)overhaulMSR.design[x][y][z] = b;
            }
        }
        ConfigNumberList fuels = data.get("fuels");
        ConfigNumberList sources = data.get("sources");
        ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
        int fuelIndex = 0;
        int sourceIndex = 0;
        int recipeIndex = 0;
        ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement> srces = new ArrayList<>();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement bl : ncpf.getConfiguration(OverhaulMSRConfiguration::new).blocks){
            if(bl.neutronSource!=null)srces.add(bl);
        }
        for(int x = 1; x<overhaulMSR.design.length; x++){
            for(int y = 1; y<overhaulMSR.design[x].length; y++){
                for(int z = 1; z<overhaulMSR.design[x][y].length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block = overhaulMSR.design[x][y][z];
                    if(block.fuelVessel!=null){
                        overhaulMSR.fuels[x][y][z] = recovery.recoverOverhaulMSRBlockRecipeLegacyNCPF(ncpf, block, (int)fuels.get(fuelIndex));
                        fuelIndex++;
                        int sid = (int) sources.get(sourceIndex);
                        if(sid>0)LegacyNeutronSourceHandler.addNeutronSource(overhaulMSR, x, y, z, srces.get(sid-1));
                        sourceIndex++;
                    }
                    if(block.irradiator!=null){
                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                        if(rid>0)overhaulMSR.irradiatorRecipes[x][y][z] = recovery.recoverOverhaulMSRBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                }
            }
        }
        return overhaulMSR;
    }
    @Override
    protected synchronized Design readMultiblockOverhaulTurbine(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList size = data.get("size");
        int width = (int)size.get(0);
        int depth = (int)size.get(1);
        int externalDepth = depth+2;
        OverhaulTurbineDesign overhaulTurbine = new OverhaulTurbineDesign(ncpf, width, width, depth);
        overhaulTurbine.recipe = recovery.recoverOverhaulTurbineRecipeLegacyNCPF(ncpf, data.getByte("recipe", (byte)-1));
        setBearing(overhaulTurbine, (int)size.get(2), ncpf.getConfiguration(OverhaulTurbineConfiguration::new));
        if(data.hasProperty("inputs")){
            overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
            ConfigNumberList inputs = data.get("inputs");
            for(int i = 0; i<inputs.size(); i++){
                overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add((int)inputs.get(i));
            }
        }
        ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement> allCoils = new ArrayList<>();
        ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement> allBlades = new ArrayList<>();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement b : ncpf.getConfiguration(OverhaulTurbineConfiguration::new).blocks){
            if(b.blade!=null)allBlades.add(b);
            else allCoils.add(b);//uhh okay
        }
        ConfigNumberList coils = data.get("coils");
        int index = 0;
        for(int z = 0; z<2; z++){
            if(z==1)z = depth-1;
            for(int x = 1; x<=width; x++){
                for(int y = 1; y<=width; y++){
                    int bid = (int) coils.get(index);
                    if(bid>0){
                        overhaulTurbine.design[x][y][z] = recovery.recoverOverhaulTurbineBlockLegacyNCPF(ncpf, bid-1);
                    }
                    index++;
                }
            }
        }
        ConfigNumberList blades = data.get("blades");
        index = 0;
        for(int z = 1; z<=depth; z++){
            int bid = (int) blades.get(index);
            if(bid>0){
                setBlade(overhaulTurbine, (int)size.get(2), z, allBlades.get(bid-1));
            }
            index++;
        }
        return overhaulTurbine;
    }
    @Override
    protected synchronized Design readMultiblockOverhaulFusionReactor(Project ncpf, Config data, RecoveryHandler recovery) {
        ConfigNumberList size = data.get("size");
        OverhaulFusionDesign overhaulFusion = new OverhaulFusionDesign(ncpf, (int)size.get(0),(int)size.get(1),(int)size.get(2),(int)size.get(3));
        overhaulFusion.recipe = recovery.recoverOverhaulFusionRecipeLegacyNCPF(ncpf, data.getByte("recipe", (byte)-1));
        overhaulFusion.coolantRecipe = recovery.recoverOverhaulFusionCoolantRecipeLegacyNCPF(ncpf, data.getByte("coolantRecipe", (byte)-1));
        ConfigNumberList blocks = data.get("blocks");
        int[] findex = new int[1];
        for(int x = 0; x<overhaulFusion.design.length; x++){
            for(int y = 0; y<overhaulFusion.design.length; y++){
                for(int z = 0; z<overhaulFusion.design.length; z++){
                    int bid = (int)blocks.get(findex[0]);
                    if(bid>0)overhaulFusion.design[x][y][z] = recovery.recoverOverhaulFusionBlockLegacyNCPF(ncpf, bid-1);
                    findex[0]++;
                }
            }
        }
        ConfigNumberList breedingBlanketRecipes = data.get("breedingBlanketRecipes");
        int recipeIndex = 0;
        for(int x = 0; x<overhaulFusion.design.length; x++){
            for(int y = 0; y<overhaulFusion.design.length; y++){
                for(int z = 0; z<overhaulFusion.design.length; z++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement block = overhaulFusion.design[x][y][z];
                    if(block.breedingBlanket!=null){
                        int rid = (int) breedingBlanketRecipes.get(recipeIndex);
                        if(rid>0)overhaulFusion.breedingBlanketRecipes[x][y][z] = recovery.recoverOverhaulFusionBlockRecipeLegacyNCPF(ncpf, block, rid-1);
                        recipeIndex++;
                    }
                }
            }
        }
        return overhaulFusion;
    }

    @Override
    protected void loadUnderhaulBlocks(NCPFConfigurationContainer project, Config config, boolean loadSettings) {
        if(config.hasProperty("underhaul")){
            Config underhaul = config.get("underhaul");
            if(underhaul.hasProperty("fissionSFR")){
                UnderhaulSFRConfiguration configuration = new UnderhaulSFRConfiguration();
                Config fissionSFR = underhaul.get("fissionSFR");
                if(loadSettings){
                    configuration.settings.minSize = fissionSFR.get("minSize");
                    configuration.settings.maxSize = fissionSFR.get("maxSize");
                    configuration.settings.neutronReach = fissionSFR.get("neutronReach");
                    configuration.settings.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                    configuration.settings.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                    configuration.settings.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                }
                ConfigList blocks = fissionSFR.get("blocks");
                underhaulPostLoadMap.clear();
                for(int i = 0; i<blocks.size(); i++){
                    Config blockCfg = blocks.getConfig(i);
                    net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                    configuration.blocks.add(block);
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
                    if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.getConfigNumberList("texture"));
                    if(blockCfg.hasProperty("rules")){
                        ConfigList rules = blockCfg.getConfigList("rules");
                        for(int idx = 0; idx<rules.size(); idx++){
                            coolerStats.rules.add(readUnderRule(rules.getConfig(idx), block.definition.toString()));
                        }
                    }
                }
                ConfigList fuels = fissionSFR.get("fuels");
                for(int i = 0; i<fuels.size(); i++){
                    Config fuelCfg = fuels.getConfig(i);
                    net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel(new NCPFLegacyItemElement(fuelCfg.getString("name")));
                    fuel.stats.power = fuelCfg.getFloat("power");
                    fuel.stats.heat = fuelCfg.getFloat("heat");
                    fuel.stats.time = fuelCfg.getInt("time");
                    configuration.fuels.add(fuel);
                }
                project.setConfiguration(configuration);
            }
        }
    }
    @Override
    protected void loadOverhaulSFRBlocks(NCPFConfigurationContainer parent, NCPFConfigurationContainer project, Config overhaul, boolean loadSettings, boolean loadingAddon, boolean isAddon, List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement> additionalBlocks) {
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
            ConfigList blocks = fissionSFR.get("blocks");
            overhaulSFRPostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                int cooling = blockCfg.get("cooling", 0);
                if(cooling!=0){
                    block.heatsink = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.HeatsinkModule();
                    block.heatsink.cooling = cooling;
                }
                if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ConductorModule();
                if(blockCfg.getBoolean("fuelCell", false))block.fuelCell = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.FuelCellModule();
                if(blockCfg.get("reflector", false)){
                    block.reflector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ReflectorModule();
                    block.reflector.efficiency = blockCfg.get("efficiency");
                    block.reflector.reflectivity = blockCfg.get("reflectivity");
                }
                if(blockCfg.getBoolean("irradiator", false))block.irradiator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.IrradiatorModule();
                if(blockCfg.get("moderator", false)){
                    block.moderator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.ModeratorModule();
                    block.moderator.flux = blockCfg.get("flux");
                    block.moderator.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.get("shield", false)){
                    block.neutronShield = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronShieldModule();
                    block.neutronShield.heatPerFlux = blockCfg.get("heatMult");
                    block.neutronShield.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.heatsink.rules.add(readOverSFRRule(rules.getConfig(idx), block.definition.toString()));
                    }
                }
            }
            ConfigList fuels = fissionSFR.get("fuels");
            for(int i = 0; i<fuels.size(); i++){
                Config fuelCfg = fuels.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel(new NCPFLegacyItemElement(fuelCfg.get("name")));
                fuel.stats.efficiency = fuelCfg.get("efficiency");
                fuel.stats.heat = fuelCfg.get("heat");
                fuel.stats.time = fuelCfg.get("time");
                fuel.stats.criticality = fuelCfg.get("criticality");
                fuel.stats.selfPriming = fuelCfg.get("selfPriming", false);
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
                    if(b.fuelCell!=null){
                        b.fuels.add(fuel);
                    }
                }
            }
            ConfigList sources = fissionSFR.get("sources");
            for(int i = 0; i<sources.size(); i++){
                Config sourceCfg = sources.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement(new NCPFLegacyBlockElement(sourceCfg.getString("name")));
                configuration.blocks.add(block);
                block.neutronSource = new net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR.NeutronSourceModule();
                block.neutronSource.efficiency = sourceCfg.get("efficiency");
            }
            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
            for(int i = 0; i<irradiatorRecipes.size(); i++){
                Config irradiatorRecipeCfg = irradiatorRecipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe(new NCPFLegacyItemElement(irradiatorRecipeCfg.getString("name")));
                recipe.stats.efficiency = irradiatorRecipeCfg.get("efficiency");
                recipe.stats.heat = irradiatorRecipeCfg.get("heat");
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
                    if(b.irradiator!=null){
                        b.irradiatorRecipes.add(recipe);
                    }
                }
            }
            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
            for(int i = 0; i<coolantRecipes.size(); i++){
                Config coolantRecipeCfg = coolantRecipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe(new NCPFLegacyFluidElement(coolantRecipeCfg.getString("input")));
                recipe.stats.heat = coolantRecipeCfg.get("heat");
                recipe.stats.outputRatio = readOutputRatio(coolantRecipeCfg, "outputRatio");
                configuration.coolantRecipes.add(recipe);
            }
        }
    }
    @Override
    protected void loadOverhaulMSRBlocks(NCPFConfigurationContainer parent, NCPFConfigurationContainer project, Config overhaul, boolean loadSettings, boolean loadingAddon, boolean isAddon, List<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement> additionalBlocks) {
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
            ConfigList blocks = fissionMSR.get("blocks");
            overhaulMSRPostLoadMap.clear();
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement(new NCPFLegacyBlockElement(blockCfg.getString("name")));
                configuration.blocks.add(block);
                int cooling = blockCfg.get("cooling", 0);
                if(cooling!=0){
                    block.heater = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.HeaterModule();
                    HeaterRecipe recipe = new HeaterRecipe(new NCPFLegacyFluidElement("null"));
                    recipe.stats.cooling = cooling;
                    block.heaterRecipes.add(recipe);
                }
                if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ConductorModule();
                if(blockCfg.getBoolean("fuelVessel", false))block.fuelVessel = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.FuelVesselModule();
                if(blockCfg.get("reflector", false)){
                    block.reflector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ReflectorModule();
                    block.reflector.efficiency = blockCfg.get("efficiency");
                    block.reflector.reflectivity = blockCfg.get("reflectivity");
                }
                if(blockCfg.getBoolean("irradiator", false))block.irradiator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.IrradiatorModule();
                if(blockCfg.get("moderator", false)){
                    block.moderator = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.ModeratorModule();
                    block.moderator.flux = blockCfg.get("flux");
                    block.moderator.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.get("shield", false)){
                    block.neutronShield = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronShieldModule();
                    block.neutronShield.heatPerFlux = blockCfg.get("heatMult");
                    block.neutronShield.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.heater.rules.add(readOverMSRRule(rules.getConfig(idx), block.definition.toString()));
                    }
                }
            }
            ConfigList fuels = fissionMSR.get("fuels");
            for(int i = 0; i<fuels.size(); i++){
                Config fuelCfg = fuels.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel fuel = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel(new NCPFLegacyItemElement(fuelCfg.get("name")));
                fuel.stats.efficiency = fuelCfg.get("efficiency");
                fuel.stats.heat = fuelCfg.get("heat");
                fuel.stats.time = fuelCfg.get("time");
                fuel.stats.criticality = fuelCfg.get("criticality");
                fuel.stats.selfPriming = fuelCfg.get("selfPriming", false);
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b : project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
                    if(b.fuelVessel!=null){
                        b.fuels.add(fuel);
                    }
                }
            }
            ConfigList sources = fissionMSR.get("sources");
            for(int i = 0; i<sources.size(); i++){
                Config sourceCfg = sources.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement(new NCPFLegacyBlockElement(sourceCfg.getString("name")));
                configuration.blocks.add(block);
                block.neutronSource = new net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR.NeutronSourceModule();
                block.neutronSource.efficiency = sourceCfg.get("efficiency");
            }
            ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
            for(int i = 0; i<irradiatorRecipes.size(); i++){
                Config irradiatorRecipeCfg = irradiatorRecipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe(new NCPFLegacyItemElement(irradiatorRecipeCfg.getString("name")));
                recipe.stats.efficiency = irradiatorRecipeCfg.get("efficiency");
                recipe.stats.heat = irradiatorRecipeCfg.get("heat");
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b : project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
                    if(b.irradiator!=null){
                        b.irradiatorRecipes.add(recipe);
                    }
                }
            }
        }
    }
    @Override
    protected void loadOverhaulTurbineBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings){
        if(overhaul.hasProperty("turbine")){
            OverhaulTurbineConfiguration configuration = new OverhaulTurbineConfiguration();
            Config turbine = overhaul.get("turbine");
            if(loadSettings){
                configuration.settings.minWidth = turbine.get("minWidth");
                configuration.settings.minLength = turbine.get("minLength");
                configuration.settings.maxSize = turbine.get("maxSize");
                configuration.settings.fluidPerBlade = turbine.get("fluidPerBlade");
                loadTurbineEfficiencyFactors(turbine, configuration);
                configuration.settings.throughputFactor = turbine.get("throughputFactor");
                configuration.settings.powerBonus = turbine.get("powerBonus");
            }
            ConfigList coils = turbine.get("coils");
            overhaulTurbinePostLoadMap.clear();
            for(int i = 0; i<coils.size(); i++){
                Config blockCfg = coils.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement(new NCPFLegacyBlockElement(blockCfg.get("name")));
                configuration.blocks.add(block);
                if(blockCfg.get("bearing", false))block.bearing = new BearingModule();
                if(blockCfg.get("connector", false))block.connector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule();
                float eff = blockCfg.get("efficiency");
                if(eff>0){
                    block.coil = new CoilModule();
                    block.coil.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.coil.rules.add(readOverTurbineRule(rules.getConfig(idx), block.definition.toString()));
                    }
                }
                if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.get("texture"));
            }
            ConfigList blades = turbine.get("blades");
            for(int i = 0; i<blades.size(); i++){
                Config blockCfg = blades.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement blade = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement(new NCPFLegacyBlockElement(blockCfg.get("name")));
                configuration.blocks.add(blade);
                if(readBladeStator(blade, blockCfg, "stator")){
                    blade.stator = new StatorModule();
                    blade.stator.expansion = blockCfg.get("expansion");
                }else{
                    blade.blade = new BladeModule();
                    blade.blade.expansion = blockCfg.get("expansion");
                    blade.blade.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.hasProperty("texture"))blade.texture.texture = loadNCPFTexture(blockCfg.get("texture"));
            }
            ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement> allCoils = new ArrayList<>();
            ArrayList<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement> allBlades = new ArrayList<>();
            for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement b : project.getConfiguration(OverhaulTurbineConfiguration::new).blocks){
                if(b.blade!=null||b.stator!=null)allBlades.add(b);
                else allCoils.add(b);
            }
            ConfigList recipes = turbine.get("recipes");
            for(int i = 0; i<recipes.size(); i++){
                Config recipeCfg = recipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe(new NCPFLegacyFluidElement(recipeCfg.get("input")));
                recipe.stats.power = recipeCfg.get("power");
                recipe.stats.coefficient = recipeCfg.get("coefficient");
                configuration.recipes.add(recipe);
            }
        }
    }
    @Override
    protected void loadOverhaulFusionGeneratorBlocks(NCPFConfigurationContainer project, Config overhaul, boolean loadSettings){
        if(overhaul.hasProperty("fusion")){
            OverhaulFusionConfiguration configuration = new OverhaulFusionConfiguration();
            Config fusion = overhaul.get("fusion");
            if(loadSettings){
                configuration.settings.minInnerRadius = fusion.get("minInnerRadius");
                configuration.settings.maxInnerRadius = fusion.get("maxInnerRadius");
                configuration.settings.minCoreSize = fusion.get("minCoreSize");
                configuration.settings.maxCoreSize = fusion.get("maxCoreSize");
                configuration.settings.minToroidWidth = fusion.get("minToroidWidth");
                configuration.settings.maxToroidWidth = fusion.get("maxToroidWidth");
                configuration.settings.minLiningThickness = fusion.get("minLiningThickness");
                configuration.settings.maxLiningThickness = fusion.get("maxLiningThickness");
                configuration.settings.coolingEfficiencyLeniency = fusion.get("coolingEfficiencyLeniency");
                configuration.settings.sparsityPenaltyMultiplier = fusion.get("sparsityPenaltyMult");
                configuration.settings.sparsityPenaltyThreshold = fusion.get("sparsityPenaltyThreshold");
            }
            ConfigList blocks = fusion.get("blocks");
            overhaulFusionPostLoadMap.clear();
            boolean augmented = false;
            for(int i = 0; i<blocks.size(); i++){
                Config blockCfg = blocks.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement block = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement(new NCPFLegacyBlockElement(blockCfg.get("name")));
                configuration.blocks.add(block);
                int cooling = blockCfg.get("cooling", 0);
                if(cooling!=0){
                    block.heatsink = new HeatsinkModule();
                    block.heatsink.cooling = cooling;
                }if(blockCfg.getBoolean("conductor", false))block.conductor = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConductorModule();
                if(blockCfg.getBoolean("connector", false))block.connector = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ConnectorModule();
                if(blockCfg.getBoolean("core", false))block.core = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.CoreModule();
                if(blockCfg.getBoolean("electromagnet", false)){
                    block.toroid = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.ToroidalElectromagnetModule();
                    block.poloid = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.PoloidalElectromagnetModule();
                }
                if(blockCfg.getBoolean("heatingBlanket", false))block.heatingBlanket = new net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion.HeatingBlanketModule();
                if(blockCfg.get("reflector", false)){
                    block.reflector = new ReflectorModule();
                    block.reflector.efficiency = blockCfg.get("efficiency");
                }
                if(blockCfg.get("breedingBlanket", false))block.breedingBlanket = new BreedingBlanketModule();
                augmented = blockCfg.getBoolean("breedingBlanketAugmented", false);
                if(blockCfg.get("shielding", false)){
                    block.shielding = new ShieldingModule();
                    block.shielding.shieldiness = blockCfg.get("shieldiness");
                }
                if(blockCfg.hasProperty("texture"))block.texture.texture = loadNCPFTexture(blockCfg.get("texture"));
                if(blockCfg.hasProperty("rules")){
                    ConfigList rules = blockCfg.get("rules");
                    for(int idx = 0; idx<rules.size(); idx++){
                        block.heatsink.rules.add(readOverFusionRule(rules.getConfig(idx), block.definition.toString()));
                    }
                }
            }
            ConfigList breedingBlanketRecipes = fusion.get("breedingBlanketRecipes");
            for(int i = 0; i<breedingBlanketRecipes.size(); i++){
                Config breedingBlanketRecipeCfg = breedingBlanketRecipes.getConfig(i);
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BlockElement b : project.getConfiguration(OverhaulFusionConfiguration::new).blocks){
                    if(b.breedingBlanket!=null){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe(new NCPFLegacyBlockElement(breedingBlanketRecipeCfg.get("name")));
                        recipe.stats.efficiency = breedingBlanketRecipeCfg.get("efficiency");
                        recipe.stats.heat = ((Number)breedingBlanketRecipeCfg.get("heat")).floatValue();
                        recipe.stats.augmented = augmented;//doesn't work for addons, but fusion addons don't exist this old anyway
                        b.breedingBlanketRecipes.add(recipe);
                    }
                }
            }
            ConfigList recipes = fusion.get("recipes");
            for(int i = 0; i<recipes.size(); i++){
                Config recipeCfg = recipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.Recipe(new NCPFLegacyFluidElement(recipeCfg.get("name")));
                recipe.stats.efficiency = recipeCfg.get("efficiency");
                recipe.stats.heat = recipeCfg.get("heat");
                recipe.stats.time = recipeCfg.get("time");
                recipe.stats.fluxiness = recipeCfg.getFloat("fluxiness");
                configuration.recipes.add(recipe);
            }
            ConfigList coolantRecipes = fusion.get("coolantRecipes");
            for(int i = 0; i<coolantRecipes.size(); i++){
                Config coolantRecipeCfg = coolantRecipes.getConfig(i);
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe recipe = new net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.CoolantRecipe(new NCPFLegacyFluidElement(coolantRecipeCfg.get("input")));
                recipe.stats.heat = coolantRecipeCfg.get("heat");
                recipe.stats.outputRatio = readOutputRatio(coolantRecipeCfg, "outputRatio");
                configuration.coolantRecipes.add(recipe);
            }
        }
    }
    public void setBearing(OverhaulTurbineDesign turbine, int bearingSize, OverhaulTurbineConfiguration configuration){
        int bearingMax = (turbine.design.length+2)/2+bearingSize/2;
        int bearingMin = (turbine.design.length+2)/2-bearingSize/2;
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement bearing = null;
        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement shaft = null;
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement block : configuration.blocks){
            if(block.shaft!=null&&shaft==null)shaft = block;
            if(block.bearing!=null&&bearing==null)bearing = block;
        }
        for(int z = 0; z<turbine.design[0][0].length+2; z++){
            for(int x = bearingMin; x<=bearingMax; x++){
                for(int y = bearingMin; y<=bearingMax; y++){
                    net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement block = shaft;
                    if(z==0||z==turbine.design[0][0].length+1)block = bearing;
                    if(block!=null)turbine.design[x][y][z] = block;
                }
            }
        }
    }
    public void setBlade(OverhaulTurbineDesign turbine, int bearingSize, int z, net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement block){
        int bearingMax = (turbine.design.length+2)/2+bearingSize/2;
        int bearingMin = (turbine.design.length+2)/2-bearingSize/2;
        for(int x = 1; x<=turbine.design.length; x++){
            for(int y = 1; y<=turbine.design[0].length; y++){
                boolean isXBlade = x>=bearingMin&&x<=bearingMax;
                boolean isYBlade = y>=bearingMin&&y<=bearingMax;
                if(isXBlade&&isYBlade)continue;//that's the bearing
                if(isXBlade||isYBlade)turbine.design[x][y][z] = block;
            }
        }
    }
}