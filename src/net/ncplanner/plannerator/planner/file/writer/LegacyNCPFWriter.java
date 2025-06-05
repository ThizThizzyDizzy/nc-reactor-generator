package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.config2.ConfigNumberList;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.NCPFElementReference;
import net.ncplanner.plannerator.ncpf.NCPFPlacementRule;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.design.NCPFCuboidalMultiblockDesign;
import net.ncplanner.plannerator.ncpf.design.NCPFDesignDefinition;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulMSRDesign;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.design.NCPFOverhaulTurbineDesign;
import net.ncplanner.plannerator.ncpf.design.NCPFUnderhaulSFRDesign;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulTurbineConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.CoolantRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.Recipe;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulTurbineDesign;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
import net.ncplanner.plannerator.planner.ncpf.module.AirModule;
import net.ncplanner.plannerator.planner.ncpf.module.TextureModule;
public class LegacyNCPFWriter extends FormatWriter{
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.LEGACY_NCPF;
    }
    @Override
    public void write(Project ncpf, OutputStream stream){
        ncpf = ncpf.copyTo(Project::new);
        Config header = Config.newConfig();
        header.setByte("version", (byte)11);
        header.setInt("count", ncpf.designs.size());
        Config meta = Config.newConfig();
        for(String key : ncpf.metadata.metadata.keySet()){
            String value = ncpf.metadata.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.setString(key,value);
        }
        if(meta.properties().length>0){
            header.setConfig("metadata", meta);
        }
        header.save(stream);
        saveConfiguration(Config.newConfig(), ncpf.conglomeration).save(stream); // Copy it to prevent destructive modifications during legacy NCPF save
        for(Design d : ncpf.designs){
            saveDesign(d, ncpf.configuration).save(stream);
        }
        try{
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return multi instanceof UnderhaulSFR
                || multi instanceof OverhaulSFR
                || multi instanceof OverhaulMSR
                || multi instanceof OverhaulTurbine;
    }
    public static void saveTexture(Config config, Image texture){
        saveTexture(config, "texture", texture);
    }
    public static void saveTexture(Config config, TextureModule texture){
        if(texture==null)return;
        saveTexture(config, "texture", texture.texture);
    }
    public static void saveTexture(Config config, String keyName, Image texture){
        if(texture==null)return;
        ConfigNumberList tex = new ConfigNumberList();
        tex.add(texture.getWidth());
        for(int x = 0; x<texture.getWidth(); x++){
            for(int y = 0; y<texture.getHeight(); y++){
                tex.add(texture.getRGB(x, texture.getHeight()-y-1));//flip Y axis because GL
            }
        }
        config.setConfigNumberList(keyName, tex);
    }
    public static void saveTexture(Config config, String keyName, TextureModule texture){
        if(texture==null)return;
        saveTexture(config, keyName, texture.texture);
    }
    private Config saveConfiguration(Config config, NCPFConfigurationContainer configuration){
        config.setBoolean("partial", true);//always call it partial
        config.setBoolean("addon", false);//never save as an addon
        Config underhaul = Config.newConfig();
        Config overhaul = Config.newConfig();
        configuration.withConfiguration(UnderhaulSFRConfiguration::new, (sfr)->{
            if(sfr.metadata.version!=null)config.setString("underhaulVersion", sfr.metadata.version);
            if(sfr.metadata.name!=null)config.setString("name", sfr.metadata.name);
            underhaul.setConfig("fissionSFR", saveUnderhaulSFRConfiguration(sfr));
        });
        configuration.withConfiguration(OverhaulSFRConfiguration::new, (sfr)->{
            if(sfr.metadata.version!=null)config.setString("version", sfr.metadata.version);
            if(sfr.metadata.name!=null)config.setString("name", sfr.metadata.name);
            overhaul.setConfig("fissionSFR", saveOverhaulSFRConfiguration(sfr));
        });
        configuration.withConfiguration(OverhaulMSRConfiguration::new, (msr)->{
            if(msr.metadata.version!=null)config.setString("version", msr.metadata.version);
            if(msr.metadata.name!=null)config.setString("name", msr.metadata.name);
            overhaul.setConfig("fissionMSR", saveOverhaulMSRConfiguration(msr));
        });
        configuration.withConfiguration(OverhaulTurbineConfiguration::new, (turbine)->{
            if(turbine.metadata.version!=null)config.setString("version", turbine.metadata.version);
            if(turbine.metadata.name!=null)config.setString("name", turbine.metadata.name);
            overhaul.setConfig("turbine", saveOverhaulTurbineConfiguration(turbine));
        });
        if(configuration.hasConfiguration(UnderhaulSFRConfiguration::new))config.setConfig("underhaul", underhaul);
        if(configuration.hasConfiguration(OverhaulSFRConfiguration::new)
         ||configuration.hasConfiguration(OverhaulMSRConfiguration::new)
         ||configuration.hasConfiguration(OverhaulTurbineConfiguration::new))config.setConfig("overhaul", overhaul);
        return config;
    }
    private Config saveUnderhaulSFRConfiguration(UnderhaulSFRConfiguration sfr){
        Config config = Config.newConfig();
        config.setInt("minSize", sfr.settings.minSize);
        config.setInt("maxSize", sfr.settings.maxSize);
        config.setInt("neutronReach", sfr.settings.neutronReach);
        config.setFloat("moderatorExtraPower", sfr.settings.moderatorExtraPower);
        config.setFloat("moderatorExtraHeat", sfr.settings.moderatorExtraHeat);
        config.setInt("activeCoolerRate", sfr.settings.activeCoolerRate);
        ConfigList blocks = new ConfigList();
        for(BlockElement b : sfr.blocks){
            Config block = Config.newConfig();
            block.setString("name", convertElementDefinition(b.definition));
            if(b.names.displayName!=null)block.setString("displayName", b.names.displayName);
            if(b.cooler!=null)block.setInt("cooling", b.cooler.cooling);
            if(b.activeCooler!=null){
                block.setString("active", convertElementDefinition(b.activeCoolerRecipes.get(0).definition));
                block.setInt("cooling", b.activeCoolerRecipes.get(0).stats.cooling);
            }
            if(b.cooler!=null||b.activeCooler!=null){
                List<NCPFPlacementRule> rules = b.cooler==null?b.activeCoolerRecipes.get(0).stats.rules:b.cooler.rules;
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : rules){
                    ruls.addConfig(savePlacementRule(rule, sfr, underhaulSFRBlockTypes));
                }
                block.setConfigList("rules", ruls);
            }
            if(b.fuelCell!=null)block.setBoolean("fuelCell", true);
            if(b.moderator!=null)block.setBoolean("moderator", true);
            if(b.casing!=null)block.setBoolean("casing", true);
            if(b.controller!=null)block.setBoolean("controller", true);
            saveTexture(block, b.texture);
            blocks.addConfig(block);
        }
        config.setConfigList("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : sfr.fuels){
            Config fuel = Config.newConfig();
            fuel.setString("name", convertElementDefinition(f.definition));//toString formats legacy metadata and whatnot
            if(f.names.displayName!=null)fuel.setString("displayName", f.names.displayName);
            fuel.setFloat("power", f.stats.power);
            fuel.setFloat("heat", f.stats.heat);
            fuel.setInt("time", f.stats.time);
            saveTexture(fuel, f.texture);
            fuels.addConfig(fuel);
        }
        config.setConfigList("fuels", fuels);
        return config;
    }
    private Config saveOverhaulSFRConfiguration(OverhaulSFRConfiguration sfr){
        Config config = Config.newConfig();
        config.setInt("minSize", sfr.settings.minSize);
        config.setInt("maxSize", sfr.settings.maxSize);
        config.setInt("neutronReach", sfr.settings.neutronReach);
        config.setInt("coolingEfficiencyLeniency", sfr.settings.coolingEfficiencyLeniency);
        config.setFloat("sparsityPenaltyMult", sfr.settings.sparsityPenaltyMultiplier);
        config.setFloat("sparsityPenaltyThreshold", sfr.settings.sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement> it = sfr.blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b = it.next();
            if(b.port!=null||b.unToggled!=null)it.remove();// remove all ports and toggled blocks, because legacy NCPF doesn't have these, and it will mess up the indicies
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : sfr.blocks){
            Config block = Config.newConfig();
            block.setString("name", convertElementDefinition(b.definition));
            if(b.names.displayName!=null)block.setString("displayName", b.names.displayName);
            block.setBoolean("cluster", b.heatsink!=null||b.neutronShield!=null||b.conductor!=null||b.fuelCell!=null||b.irradiator!=null);
            block.setBoolean("createCluster", b.fuelCell!=null||b.irradiator!=null||b.neutronShield!=null);
            block.setBoolean("functional", b.fuelCell!=null||b.irradiator!=null||b.heatsink!=null||b.reflector!=null||b.neutronShield!=null);
            block.setBoolean("blocksLOS", b.reflector!=null||b.fuelCell!=null||b.irradiator!=null);
            block.setBoolean("casing", b.casing!=null);
            if(b.casing!=null)block.setBoolean("casingEdge", b.casing.edge);
            if(b.coolantVent!=null){
                Config coolantVentCfg = Config.newConfig();
                LegacyNCPFWriter.saveTexture(coolantVentCfg, "outTexture", b.toggled.texture);
                if(b.toggled.names.displayName!=null)coolantVentCfg.setString("outDisplayName", b.toggled.names.displayName);
                block.setConfig("coolantVent", coolantVentCfg);
            }
            block.setBoolean("controller", b.controller!=null);
            if(b.fuelCell!=null){
                Config fuelCellCfg = Config.newConfig();
                fuelCellCfg.setBoolean("hasBaseStats", false);
                block.setConfig("fuelCell", fuelCellCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel f : b.fuels){
                    Config fuel = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.setString("name", convertElementDefinition(f.definition));
                    if(f.names.displayName!=null)inputCfg.setString("displayName", f.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, f.texture);
                    fuel.setConfig("input", inputCfg);
                    fuel.setConfig("output", inputCfg);//...don't worry about it, it's fine
                    Config fuelCfg = Config.newConfig();
                    fuelCfg.setFloat("efficiency", f.stats.efficiency);
                    fuelCfg.setInt("heat", f.stats.heat);
                    fuelCfg.setInt("time", f.stats.time);
                    fuelCfg.setInt("criticality", f.stats.criticality);
                    if(f.stats.selfPriming)fuelCfg.setBoolean("selfPriming", true);
                    fuel.setConfig("fuelCell", fuelCfg);
                    recipesCfg.addConfig(fuel);
                }
                block.setConfigList("recipes", recipesCfg);
            }
            if(b.irradiator!=null){
                Config irradiatorCfg = Config.newConfig();
                irradiatorCfg.setBoolean("hasBaseStats", false);
                block.setConfig("irradiator", irradiatorCfg);
                ConfigList recipesCfg = new ConfigList();
                for( IrradiatorRecipe r : b.irradiatorRecipes){
                    Config recipe = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.setString("name", convertElementDefinition(r.definition));
                    if(r.names.displayName!=null)inputCfg.setString("displayName", r.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, r.texture);
                    recipe.setConfig("input", inputCfg);
                    recipe.setConfig("output", inputCfg);//...don't worry about it, it's fine
                    Config irrecipeCfg = Config.newConfig();
                    irrecipeCfg.setFloat("efficiency", r.stats.efficiency);
                    irrecipeCfg.setFloat("heat", r.stats.heat);
                    recipe.setConfig("irradiator", irrecipeCfg);
                    recipesCfg.addConfig(recipe);
                }
                block.setConfigList("recipes", recipesCfg);
            }
            if(b.reflector!=null){
                Config reflectorCfg = Config.newConfig();
                reflectorCfg.setBoolean("hasBaseStats", true);
                reflectorCfg.setFloat("efficiency", b.reflector.efficiency);
                reflectorCfg.setFloat("reflectivity", b.reflector.reflectivity);
                block.setConfig("reflector", reflectorCfg);
            }
            if(b.moderator!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.setBoolean("hasBaseStats", true);
                moderatorCfg.setInt("flux", b.moderator.flux);
                moderatorCfg.setFloat("efficiency", b.moderator.efficiency);
                moderatorCfg.setBoolean("active", true);
                block.setConfig("moderator", moderatorCfg);
            }
            if(b.neutronShield!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.setBoolean("hasBaseStats", true);
                moderatorCfg.setInt("flux", 0);
                moderatorCfg.setFloat("efficiency", b.neutronShield.efficiency);
                moderatorCfg.setBoolean("active", false);
                block.setConfig("moderator", moderatorCfg);
                Config shieldCfg = Config.newConfig();
                shieldCfg.setBoolean("hasBaseStats", true);
                shieldCfg.setInt("heat", b.neutronShield.heatPerFlux);
                shieldCfg.setFloat("efficiency", b.neutronShield.efficiency);
                LegacyNCPFWriter.saveTexture(shieldCfg, "closedTexture", b.toggled.texture);
                block.setConfig("shield", shieldCfg);
            }
            if(b.heatsink!=null){
                Config heatsinkCfg = Config.newConfig();
                heatsinkCfg.setBoolean("hasBaseStats", true);
                heatsinkCfg.setInt("cooling", b.heatsink.cooling);
                block.setConfig("heatsink", heatsinkCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.heatsink.rules){
                    ruls.addConfig(savePlacementRule(rule, sfr, overhaulSFRBlockTypes));
                }
                block.setConfigList("rules", ruls);
            }
            if(b.neutronSource!=null){
                Config sourceCfg = Config.newConfig();
                sourceCfg.setFloat("efficiency", b.neutronSource.efficiency);
                block.setConfig("source", sourceCfg);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture);
            if(b.recipePorts!=null){
                Config portCfg = Config.newConfig();
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement in = b.recipePorts.input.block;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement out = b.recipePorts.output.block;
                portCfg.setString("name", convertElementDefinition(in.definition));
                if(in.names.displayName!=null)portCfg.setString("inputDisplayName", in.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "inputTexture", in.texture);
                if(out.names.displayName!=null)portCfg.setString("outputDisplayName", out.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "outputTexture", out.texture);
                block.setConfig("port", portCfg);
            }
            blocks.addConfig(block);
        }
        config.setConfigList("blocks", blocks);
        ConfigList coolantRecipes = new ConfigList();
        for(CoolantRecipe r : sfr.coolantRecipes){
            Config recipe = Config.newConfig();
            Config inputCfg = Config.newConfig();
            inputCfg.setString("name", convertElementDefinition(r.definition));
            if(r.names.displayName!=null)inputCfg.setString("displayName", r.names.displayName);
            LegacyNCPFWriter.saveTexture(inputCfg, r.texture);
            recipe.setConfig("input", inputCfg);
            recipe.setConfig("output", inputCfg);//...don't worry about it, it's fine
            recipe.setInt("heat", r.stats.heat);
            recipe.setFloat("outputRatio", r.stats.outputRatio);
            coolantRecipes.addConfig(recipe);
        }
        config.setConfigList("coolantRecipes", coolantRecipes);
        return config;
    }
    private Config saveOverhaulMSRConfiguration(OverhaulMSRConfiguration msr){
        Config config = Config.newConfig();
        config.setInt("minSize", msr.settings.minSize);
        config.setInt("maxSize", msr.settings.maxSize);
        config.setInt("neutronReach", msr.settings.neutronReach);
        config.setInt("coolingEfficiencyLeniency", msr.settings.coolingEfficiencyLeniency);
        config.setFloat("sparsityPenaltyMult", msr.settings.sparsityPenaltyMultiplier);
        config.setFloat("sparsityPenaltyThreshold", msr.settings.sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement> it = msr.blocks.iterator(); it.hasNext();){
            net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b = it.next();
            if(b.port!=null||b.unToggled!=null)it.remove();// remove all ports and toggled blocks, because legacy NCPF doesn't have these, and it will mess up the indicies
        }
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b : msr.blocks){
            Config block = Config.newConfig();
            block.setString("name", convertElementDefinition(b.definition));
            if(b.names.displayName!=null)block.setString("displayName", b.names.displayName);
            block.setBoolean("cluster", b.heater!=null||b.neutronShield!=null||b.conductor!=null||b.fuelVessel!=null||b.irradiator!=null);
            block.setBoolean("createCluster", b.fuelVessel!=null||b.irradiator!=null||b.neutronShield!=null);
            block.setBoolean("functional", b.fuelVessel!=null||b.irradiator!=null||b.heater!=null||b.reflector!=null||b.neutronShield!=null);
            block.setBoolean("blocksLOS", b.reflector!=null||b.fuelVessel!=null||b.irradiator!=null);
            block.setBoolean("casing", b.casing!=null);
            if(b.casing!=null)block.setBoolean("casingEdge", b.casing.edge);
            block.setBoolean("controller", b.controller!=null);
            if(b.fuelVessel!=null){
                Config fuelVesselCfg = Config.newConfig();
                fuelVesselCfg.setBoolean("hasBaseStats", false);
                block.setConfig("fuelVessel", fuelVesselCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel f : b.fuels){
                    Config fuel = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.setString("name", convertElementDefinition(f.definition));
                    if(f.names.displayName!=null)inputCfg.setString("displayName", f.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, f.texture);
                    inputCfg.setInt("rate", 1);
                    fuel.setConfig("input", inputCfg);
                    fuel.setConfig("output", inputCfg);//...don't worry about it, it's fine
                    Config fuelCfg = Config.newConfig();
                    fuelCfg.setFloat("efficiency", f.stats.efficiency);
                    fuelCfg.setInt("heat", f.stats.heat);
                    fuelCfg.setInt("time", (int)f.stats.time);
                    fuelCfg.setInt("criticality", f.stats.criticality);
                    if(f.stats.selfPriming)fuelCfg.setBoolean("selfPriming", true);
                    fuel.setConfig("fuelVessel", fuelCfg);
                    recipesCfg.addConfig(fuel);
                }
                block.setConfigList("recipes", recipesCfg);
            }
            if(b.irradiator!=null){
                Config irradiatorCfg = Config.newConfig();
                irradiatorCfg.setBoolean("hasBaseStats", false);
                block.setConfig("irradiator", irradiatorCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe r : b.irradiatorRecipes){
                    Config recipe = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.setString("name", convertElementDefinition(r.definition));
                    if(r.names.displayName!=null)inputCfg.setString("displayName", r.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, r.texture);
                    recipe.setConfig("input", inputCfg);
                    recipe.setConfig("output", inputCfg);//...don't worry about it, it's fine
                    Config irrecipeCfg = Config.newConfig();
                    irrecipeCfg.setFloat("efficiency", r.stats.efficiency);
                    irrecipeCfg.setFloat("heat", r.stats.heat);
                    recipe.setConfig("irradiator", irrecipeCfg);
                    recipesCfg.addConfig(recipe);
                }
                block.setConfigList("recipes", recipesCfg);
            }
            if(b.reflector!=null){
                Config reflectorCfg = Config.newConfig();
                reflectorCfg.setBoolean("hasBaseStats", true);
                reflectorCfg.setFloat("efficiency", b.reflector.efficiency);
                reflectorCfg.setFloat("reflectivity", b.reflector.reflectivity);
                block.setConfig("reflector", reflectorCfg);
            }
            if(b.moderator!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.setBoolean("hasBaseStats", true);
                moderatorCfg.setInt("flux", b.moderator.flux);
                moderatorCfg.setFloat("efficiency", b.moderator.efficiency);
                moderatorCfg.setBoolean("active", true);
                block.setConfig("moderator", moderatorCfg);
            }
            if(b.neutronShield!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.setBoolean("hasBaseStats", true);
                moderatorCfg.setInt("flux", 0);
                moderatorCfg.setFloat("efficiency", b.neutronShield.efficiency);
                moderatorCfg.setBoolean("active", false);
                block.setConfig("moderator", moderatorCfg);
                Config shieldCfg = Config.newConfig();
                shieldCfg.setBoolean("hasBaseStats", true);
                shieldCfg.setInt("heat", b.neutronShield.heatPerFlux);
                shieldCfg.setFloat("efficiency", b.neutronShield.efficiency);
                LegacyNCPFWriter.saveTexture(shieldCfg, "closedTexture", b.toggled.texture);
                block.setConfig("shield", shieldCfg);
            }
            if(b.heater!=null){
                Config heatsinkCfg = Config.newConfig();
                heatsinkCfg.setBoolean("hasBaseStats", true);
                heatsinkCfg.setInt("cooling", b.heaterRecipes.get(0).stats.cooling);
                block.setConfig("heater", heatsinkCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.heater.rules){
                    ruls.addConfig(savePlacementRule(rule, msr, overhaulMSRBlockTypes));
                }
                block.setConfigList("rules", ruls);
                
                
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe r : b.heaterRecipes){
                    Config recipe = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.setString("name", convertElementDefinition(r.definition));
                    if(r.names.displayName!=null)inputCfg.setString("displayName", r.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, r.texture);
                    recipe.setConfig("input", inputCfg);
                    recipe.setConfig("output", inputCfg);//...don't worry about it, it's fine
                    Config hRecipeCfgM = Config.newConfig();
                    hRecipeCfgM.setFloat("efficiency", 0);
                    hRecipeCfgM.setInt("flux", 0);
                    recipe.setConfig("moderator", hRecipeCfgM);
                    Config hRecipeCfgH = Config.newConfig();
                    hRecipeCfgH.setInt("cooling", r.stats.cooling);
                    recipe.setConfig("heater", hRecipeCfgH);
                    recipesCfg.addConfig(recipe);
                }
                block.setConfigList("recipes", recipesCfg);
            }
            if(b.neutronSource!=null){
                Config sourceCfg = Config.newConfig();
                sourceCfg.setFloat("efficiency", b.neutronSource.efficiency);
                block.setConfig("source", sourceCfg);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture);
            if(b.recipePorts!=null){
                Config portCfg = Config.newConfig();
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement in = b.recipePorts.input.block;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement out = b.recipePorts.output.block;
                portCfg.setString("name", convertElementDefinition(in.definition));
                if(in.names.displayName!=null)portCfg.setString("inputDisplayName", in.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "inputTexture", in.texture);
                if(out.names.displayName!=null)portCfg.setString("outputDisplayName", out.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "outputTexture", out.texture);
                block.setConfig("port", portCfg);
            }
            blocks.addConfig(block);
        }
        config.setConfigList("blocks", blocks);
        return config;
    }
    private Config saveOverhaulTurbineConfiguration(OverhaulTurbineConfiguration turbine){
        Config config = Config.newConfig();
        config.setInt("minWidth", turbine.settings.minWidth);
        config.setInt("minLength", turbine.settings.minLength);
        config.setInt("maxSize", turbine.settings.maxSize);
        config.setInt("fluidPerBlade", turbine.settings.fluidPerBlade);
        config.setFloat("throughputEfficiencyLeniencyMult", turbine.settings.throughputEfficiencyLeniencyMultiplier);
        config.setFloat("throughputEfficiencyLeniencyThreshold", turbine.settings.throughputEfficiencyLeniencyThreshold);
        config.setFloat("throughputFactor", turbine.settings.throughputFactor);
        config.setFloat("powerBonus", turbine.settings.powerBonus);
        ConfigList blocks = new ConfigList();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement b : turbine.blocks){
            Config block = Config.newConfig();
            block.setString("name", convertElementDefinition(b.definition));
            if(b.names.displayName!=null)block.setString("displayName", b.names.displayName);
            if(b.bearing!=null)block.setBoolean("bearing", true);
            if(b.shaft!=null)block.setBoolean("shaft", true);
            if(b.connector!=null){
                block.setBoolean("connector", true);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.connector.rules){
                    ruls.addConfig(savePlacementRule(rule, turbine, overhaulTurbineBlockTypes));
                }
                block.setConfigList("rules", ruls);
            }
            if(b.controller!=null)block.setBoolean("controller", true);
            if(b.casing!=null){
                block.setBoolean("casing", true);
                block.setBoolean("casingEdge", b.casing.edge);
            }
            if(b.inlet!=null)block.setBoolean("inlet", true);
            if(b.outlet!=null)block.setBoolean("outlet", true);
            if(b.blade!=null){
                Config bladeCfg = Config.newConfig();
                bladeCfg.setFloat("efficiency", b.blade.efficiency);
                bladeCfg.setFloat("expansion", b.blade.expansion);
                bladeCfg.setBoolean("stator", false);
                block.setConfig("blade", bladeCfg);
            }
            if(b.stator!=null){
                Config bladeCfg = Config.newConfig();
                bladeCfg.setFloat("efficiency", 0f);
                bladeCfg.setFloat("expansion", b.stator.expansion);
                bladeCfg.setBoolean("stator", true);
                block.setConfig("blade", bladeCfg);
            }
            if(b.coil!=null){
                Config coilCfg = Config.newConfig();
                coilCfg.setFloat("efficiency", b.coil.efficiency);
                block.setConfig("coil", coilCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.coil.rules){
                    ruls.addConfig(savePlacementRule(rule, turbine, overhaulTurbineBlockTypes));
                }
                block.setConfigList("rules", ruls);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture);
            blocks.addConfig(block);
        }
        config.setConfigList("blocks", blocks);
        ConfigList recipes = new ConfigList();
        for(Recipe r : turbine.recipes){
            Config recipe = Config.newConfig();
            Config inputCfg = Config.newConfig();
            inputCfg.setString("name", convertElementDefinition(r.definition));
            if(r.names.displayName!=null)inputCfg.setString("displayName", r.names.displayName);
            LegacyNCPFWriter.saveTexture(inputCfg, r.texture);
            recipe.setConfig("input", inputCfg);
            recipe.setConfig("output", inputCfg);//...don't worry about it, it's fine
            recipe.setDouble("power", r.stats.power);
            recipe.setDouble("coefficient", r.stats.coefficient);
            recipes.addConfig(recipe);
        }
        config.setConfigList("recipes", recipes);
        return config;
    }
    
    private final NCPFPlacementRule.RuleType[] ruleTypes = new NCPFPlacementRule.RuleType[]{
        NCPFPlacementRule.RuleType.BETWEEN, 
        NCPFPlacementRule.RuleType.AXIAL, 
        NCPFPlacementRule.RuleType.VERTEX, 
        NCPFPlacementRule.RuleType.EDGE, 
        NCPFPlacementRule.RuleType.OR, 
        NCPFPlacementRule.RuleType.AND
    };
    protected final Supplier<NCPFModule>[] underhaulSFRBlockTypes = new Supplier[]{
        AirModule::new,//air
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CoolerModule::new,//doesn't do active coolers, but this is underhaul so this isn't a thing anyway
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule::new
    };
    protected final Supplier<NCPFModule>[] overhaulSFRBlockTypes = new Supplier[]{
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
    protected final Supplier<NCPFModule>[] overhaulMSRBlockTypes = new Supplier[]{
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
    protected final Supplier<NCPFModule>[] overhaulTurbineBlockTypes = new Supplier[]{
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CasingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.CoilModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.BearingModule::new,
        net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine.ConnectorModule::new
    };
    public Config savePlacementRule(NCPFPlacementRule rule, NCPFConfiguration cfg, Supplier<NCPFModule>[] blockTypes){
        Config config = Config.newConfig();
        config.setByte("type", (byte) indexof(rule.rule, ruleTypes));
        switch (rule.rule) {
            case BETWEEN:
            case AXIAL:
                saveRuleTarget(rule, config, cfg, blockTypes);
                config.setByte("min", (byte)rule.min);
                config.setByte("max", (byte)rule.max);
                break;
            case VERTEX:
            case EDGE:
                saveRuleTarget(rule, config, cfg, blockTypes);
                break;
            case OR:
            case AND:
                ConfigList ruls = new ConfigList();
                for (NCPFPlacementRule rul : rule.rules) {
                    ruls.addConfig(savePlacementRule(rul, cfg, blockTypes));
                }
                config.setConfigList("rules", ruls);
                break;
        }
        return config;
    }
    private void saveRuleTarget(NCPFPlacementRule rule, Config config, NCPFConfiguration cfg, Supplier<NCPFModule>[] blockTypes) {
        boolean isSpecificBlock = !rule.target.definition.typeMatches(NCPFModuleElement::new);
        config.setBoolean("isSpecificBlock", isSpecificBlock);
        if (isSpecificBlock) {
            config.setInt("block", indexof(rule.target, cfg.getElements()) + 1);
        } else {
            config.setByte("blockType", (byte)mindexof(((NCPFModuleElement)rule.target.definition).name, blockTypes));
        }
    }
    private int mindexof(String moduleName, Supplier<NCPFModule>[] arr){
        for(int i = 0; i<arr.length; i++){
            if(moduleName.equals(arr[i].get().name))return i;
        }
        return -1;
    }
    private int dindexof(NCPFDesignDefinition design, Supplier<NCPFDesignDefinition>[] arr){
        for(int i = 0; i<arr.length; i++){
            if(design.type.equals(arr[i].get().type))return i;
        }
        return -1;
    }
    private <T> int indexof(T elem, T[] arr){
        for(int i = 0; i<arr.length; i++){
            if(arr[i]==elem)return i;
        }
        return -1;
    }
    private <T extends NCPFElement> int indexof(T elem, List<T>... arr){
        for(List<T> lst : arr){
            for(int i = 0; i<lst.size(); i++){
                if(lst.get(i).definition.matches(elem.definition))return i;
            }
        }
        return -1;
    }
    private int indexof(NCPFElementReference elem, List<NCPFElement>... arr){
        for(List<NCPFElement> lst : arr){
            for(int i = 0; i<lst.size(); i++){
                if(lst.get(i).definition.matches(elem.definition))return i;
            }
        }
        return -1;
    }
    private final Supplier<NCPFDesignDefinition>[] designIndicies = new Supplier[]{
        NCPFUnderhaulSFRDesign::new,
        NCPFOverhaulSFRDesign::new,
        NCPFOverhaulMSRDesign::new,
        NCPFOverhaulTurbineDesign::new
    };
    private Config saveDesign(Design design, NCPFConfigurationContainer configuration){
        design.convertToObject(new NCPFObject());//set the references and stuff I guess
        NCPFCuboidalMultiblockDesign definition = (NCPFCuboidalMultiblockDesign)design.definition;
        int id = dindexof(design.definition, designIndicies);
        if(id==-1)return null;
        Config config = Config.newConfig();
        config.setInt("id", id);
        Config meta = Config.newConfig();
        for(String key : design.metadata.metadata.keySet()){
            String value = design.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.setString(key,value);
        }
        if(meta.properties().length>0){
            config.setConfig("metadata", meta);
        }
        ConfigNumberList dimensions = new ConfigNumberList();
        dimensions.add(definition.design.length-2);
        dimensions.add(definition.design[0].length-2);
        dimensions.add(definition.design[0][0].length-2);
        config.setConfigNumberList("dimensions", dimensions);
        if(design instanceof UnderhaulSFRDesign){
            UnderhaulSFRDesign sfr = (UnderhaulSFRDesign)design;
            UnderhaulSFRConfiguration cfg = configuration.getConfiguration(UnderhaulSFRConfiguration::new);
            config.setInt("fuel", indexof(sfr.fuel, cfg.fuels));
            config.setBoolean("compact", true);
            ConfigNumberList blox = new ConfigNumberList();
            for(int x = 0; x<sfr.design.length; x++){
                for(int y = 0; y<sfr.design[x].length; y++){
                    for(int z = 0; z<sfr.design[x][y].length; z++){
                        BlockElement block = sfr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else blox.add(indexof(block, cfg.blocks)+1);
                    }
                }
            }
            config.setConfigNumberList("blocks", blox);
        }
        if(design instanceof OverhaulSFRDesign){
            OverhaulSFRDesign sfr = (OverhaulSFRDesign)design;
            config.setBoolean("compact", true);
            OverhaulSFRConfiguration cfg = configuration.getConfiguration(OverhaulSFRConfiguration::new);
            for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement> it = cfg.blocks.iterator(); it.hasNext();){
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b = it.next();
                if(b.unToggled!=null)it.remove();// remove all toggled blocks, because legacy NCPF doesn't have these, but NOT all ports, because legacy NCPF sucks
            }
            int cr = indexof(sfr.coolantRecipe, cfg.coolantRecipes);
            config.setInt("coolantRecipe", Math.max(cr, 0));//give a default if it's none
            ConfigNumberList blox = new ConfigNumberList();
            ConfigNumberList blockRecipes = new ConfigNumberList();
            ConfigNumberList ports = new ConfigNumberList();
            for(int x = 0; x<sfr.design.length; x++){
                for(int y = 0; y<sfr.design[x].length; y++){
                    for(int z = 0; z<sfr.design[x][y].length; z++){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block = sfr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else{
                            boolean wasItAPort = block.port!=null;
                            if(block.port!=null||block.coolantVent!=null)ports.add(block.unToggled!=null?1:0);
                            if(block.port!=null)block = block.parent;
                            if(block.coolantVent!=null&&block.unToggled!=null)block = block.unToggled;
                            blox.add(indexof(block, cfg.blocks)+1+(wasItAPort?1:0));
                            if(block.fuelCell!=null){
                                blockRecipes.add(indexof(sfr.fuels[x][y][z], block.fuels)+1);
                            }
                            if(block.irradiator!=null){
                                blockRecipes.add(indexof(sfr.irradiatorRecipes[x][y][z], block.irradiatorRecipes)+1);
                            }
                        }
                    }
                }
            }
            config.setConfigNumberList("blocks", blox);
            config.setConfigNumberList("blockRecipes", blockRecipes);
            config.setConfigNumberList("ports", ports);
        }
        if(design instanceof OverhaulMSRDesign){
            OverhaulMSRDesign msr = (OverhaulMSRDesign)design;
            config.setBoolean("compact", true);
            OverhaulMSRConfiguration cfg = configuration.getConfiguration(OverhaulMSRConfiguration::new);
            for(Iterator<net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement> it = cfg.blocks.iterator(); it.hasNext();){
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b = it.next();
                if(b.unToggled!=null)it.remove(); // remove all toggled blocks, because legacy NCPF doesn't have these, but NOT all ports, because legacy NCPF sucks
            }
            ConfigNumberList blox = new ConfigNumberList();
            ConfigNumberList blockRecipes = new ConfigNumberList();
            ConfigNumberList ports = new ConfigNumberList();
            for(int x = 0; x<msr.design.length; x++){
                for(int y = 0; y<msr.design[x].length; y++){
                    for(int z = 0; z<msr.design[x][y].length; z++){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block = msr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else{
                            boolean wasItAPort = block.port!=null;
                            if(block.port!=null){
                                ports.add(block.unToggled!=null?1:0);
                                block = block.parent;
                            }
                            blox.add(indexof(block, cfg.blocks)+1+(wasItAPort?1:0));
                            if(block.fuelVessel!=null){
                                blockRecipes.add(indexof(msr.fuels[x][y][z], block.fuels)+1);
                            }
                            if(block.irradiator!=null){
                                blockRecipes.add(indexof(msr.irradiatorRecipes[x][y][z], block.irradiatorRecipes)+1);
                            }
                            if(block.heater!=null){
                                blockRecipes.add(indexof(msr.heaterRecipes[x][y][z], block.heaterRecipes)+1);
                            }
                        }
                    }
                }
            }
            config.setConfigNumberList("blocks", blox);
            config.setConfigNumberList("blockRecipes", blockRecipes);
            config.setConfigNumberList("ports", ports);
        }
        if(design instanceof OverhaulTurbineDesign){
            OverhaulTurbineDesign turbine = (OverhaulTurbineDesign)design;
            OverhaulTurbineConfiguration cfg = configuration.getConfiguration(OverhaulTurbineConfiguration::new);
            ConfigNumberList blocks = new ConfigNumberList();
            for(int x = 0; x<turbine.design.length; x++){
                for(int y = 0; y<turbine.design[x].length; y++){
                    for(int z = 0; z<turbine.design[x][y].length; z++){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement block = turbine.design[x][y][z];
                        if(block==null)blocks.add(0);
                        else blocks.add(indexof(block, cfg.blocks)+1);
                    }
                }
            }
            config.setConfigNumberList("blocks", blocks);
            config.setInt("recipe", indexof(turbine.recipe, cfg.recipes));
        }
        return config;
    }
    private String convertElementDefinition(NCPFElementDefinition definition){
        String str = definition.toString();
        if(str.contains("["))str = str.substring(0, str.indexOf('['));
        if(str.contains("{"))str = str.substring(0, str.indexOf('{'));
        return str;
    }
}