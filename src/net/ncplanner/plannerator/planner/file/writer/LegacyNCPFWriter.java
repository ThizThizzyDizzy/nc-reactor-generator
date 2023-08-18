package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
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
public class LegacyNCPFWriter extends FormatWriter{
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.LEGACY_NCPF;
    }
    @Override
    public void write(Project ncpf, OutputStream stream){
        Config header = Config.newConfig();
        header.set("version", (byte)11);
        header.set("count", ncpf.designs.size());
        Config meta = Config.newConfig();
        for(String key : ncpf.metadata.metadata.keySet()){
            String value = ncpf.metadata.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.set(key,value);
        }
        if(meta.properties().length>0){
            header.set("metadata", meta);
        }
        header.save(stream);
        saveConfiguration(Config.newConfig(), ncpf.conglomeration).save(stream);
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
    public static void saveTexture(Config config, String keyName, Image texture){
        if(texture!=null){
            ConfigNumberList tex = new ConfigNumberList();
            tex.add(texture.getWidth());
            for(int x = 0; x<texture.getWidth(); x++){
                for(int y = 0; y<texture.getHeight(); y++){
                    tex.add(texture.getRGB(x, texture.getHeight()-y-1));//flip Y axis because GL
                }
            }
            config.set(keyName, tex);
        }
    }
    private Config saveConfiguration(Config config, NCPFConfigurationContainer configuration){
        config.set("partial", true);//always call it partial
        config.set("addon", false);//never save as an addon
        Config underhaul = Config.newConfig();
        Config overhaul = Config.newConfig();
        configuration.withConfiguration(UnderhaulSFRConfiguration::new, (sfr)->{
            if(sfr.metadata.version!=null)config.set("underhaulVersion", sfr.metadata.version);
            if(sfr.metadata.name!=null)config.set("name", sfr.metadata.name);
            underhaul.set("fissionSFR", saveUnderhaulSFRConfiguration(sfr));
        });
        configuration.withConfiguration(OverhaulSFRConfiguration::new, (sfr)->{
            if(sfr.metadata.version!=null)config.set("version", sfr.metadata.version);
            if(sfr.metadata.name!=null)config.set("name", sfr.metadata.name);
            overhaul.set("fissionSFR", saveOverhaulSFRConfiguration(sfr));
        });
        configuration.withConfiguration(OverhaulMSRConfiguration::new, (msr)->{
            if(msr.metadata.version!=null)config.set("version", msr.metadata.version);
            if(msr.metadata.name!=null)config.set("name", msr.metadata.name);
            overhaul.set("fissionMSR", saveOverhaulMSRConfiguration(msr));
        });
        configuration.withConfiguration(OverhaulTurbineConfiguration::new, (turbine)->{
            if(turbine.metadata.version!=null)config.set("version", turbine.metadata.version);
            if(turbine.metadata.name!=null)config.set("name", turbine.metadata.name);
            overhaul.set("turbine", saveOverhaulTurbineConfiguration(turbine));
        });
        if(configuration.hasConfiguration(UnderhaulSFRConfiguration::new))config.set("underhaul", underhaul);
        if(configuration.hasConfiguration(OverhaulSFRConfiguration::new)
         ||configuration.hasConfiguration(OverhaulMSRConfiguration::new)
         ||configuration.hasConfiguration(OverhaulTurbineConfiguration::new))config.set("overhaul", overhaul);
        return config;
    }
    private Config saveUnderhaulSFRConfiguration(UnderhaulSFRConfiguration sfr){
        Config config = Config.newConfig();
        config.set("minSize", sfr.settings.minSize);
        config.set("maxSize", sfr.settings.maxSize);
        config.set("neutronReach", sfr.settings.neutronReach);
        config.set("moderatorExtraPower", sfr.settings.moderatorExtraPower);
        config.set("moderatorExtraHeat", sfr.settings.moderatorExtraHeat);
        config.set("activeCoolerRate", sfr.settings.activeCoolerRate);
        ConfigList blocks = new ConfigList();
        for(BlockElement b : sfr.blocks){
            Config block = Config.newConfig();
            block.set("name", b.definition.toString());
            if(b.names.displayName!=null)block.set("displayName", b.names.displayName);
            if(b.cooler!=null)block.set("cooling", b.cooler.cooling);
            if(b.activeCooler!=null){
                block.set("active", b.activeCoolerRecipes.get(0).definition.toString());
                block.set("cooling", b.activeCoolerRecipes.get(0).stats.cooling);
            }
            if(b.cooler!=null||b.activeCooler!=null){
                List<NCPFPlacementRule> rules = b.cooler==null?b.activeCoolerRecipes.get(0).stats.rules:b.cooler.rules;
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : rules){
                    ruls.add(savePlacementRule(rule, sfr, underhaulSFRBlockTypes));
                }
                block.set("rules", ruls);
            }
            if(b.fuelCell!=null)block.set("fuelCell", true);
            if(b.moderator!=null)block.set("moderator", true);
            if(b.casing!=null)block.set("casing", true);
            if(b.controller!=null)block.set("controller", true);
            saveTexture(block, b.texture.texture);
            blocks.add(block);
        }
        config.set("blocks", blocks);
        ConfigList fuels = new ConfigList();
        for(Fuel f : sfr.fuels){
            Config fuel = Config.newConfig();
            fuel.set("name", f.definition.toString());//toString formats legacy metadata and whatnot
            if(f.names.displayName!=null)fuel.set("displayName", f.names.displayName);
            fuel.set("power", f.stats.power);
            fuel.set("heat", f.stats.heat);
            fuel.set("time", f.stats.time);
            saveTexture(fuel, f.texture.texture);
            fuels.add(fuel);
        }
        config.set("fuels", fuels);
        return config;
    }
    private Config saveOverhaulSFRConfiguration(OverhaulSFRConfiguration sfr){
        Config config = Config.newConfig();
        config.set("minSize", sfr.settings.minSize);
        config.set("maxSize", sfr.settings.maxSize);
        config.set("neutronReach", sfr.settings.neutronReach);
        config.set("coolingEfficiencyLeniency", sfr.settings.coolingEfficiencyLeniency);
        config.set("sparsityPenaltyMult", sfr.settings.sparsityPenaltyMultiplier);
        config.set("sparsityPenaltyThreshold", sfr.settings.sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : sfr.blocks){
            if(b.port!=null)continue;//don't save ports, because UGH
            if(b.unToggled!=null)continue;//don't save output vents, because UGGGHHHH
            Config block = Config.newConfig();
            block.set("name", b.definition.toString());
            if(b.names.displayName!=null)block.set("displayName", b.names.displayName);
            block.set("cluster", b.heatsink!=null||b.neutronShield!=null||b.conductor!=null||b.fuelCell!=null||b.irradiator!=null);
            block.set("createCluster", b.fuelCell!=null||b.irradiator!=null||b.neutronShield!=null);
            block.set("functional", b.fuelCell!=null||b.irradiator!=null||b.heatsink!=null||b.reflector!=null||b.neutronShield!=null);
            block.set("blocksLOS", b.reflector!=null||b.fuelCell!=null||b.irradiator!=null);
            block.set("casing", b.casing!=null);
            if(b.casing!=null)block.set("casingEdge", b.casing.edge);
            if(b.coolantVent!=null){
                Config coolantVentCfg = Config.newConfig();
                LegacyNCPFWriter.saveTexture(coolantVentCfg, "outTexture", b.toggled.texture.texture);
                if(b.toggled.names.displayName!=null)coolantVentCfg.set("outDisplayName", b.toggled.names.displayName);
                block.set("coolantVent", coolantVentCfg);
            }
            block.set("controller", b.controller!=null);
            if(b.fuelCell!=null){
                Config fuelCellCfg = Config.newConfig();
                fuelCellCfg.set("hasBaseStats", false);
                block.set("fuelCell", fuelCellCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel f : b.fuels){
                    Config fuel = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.set("name", f.definition.toString());
                    if(f.names.displayName!=null)inputCfg.set("displayName", f.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, f.texture.texture);
                    fuel.set("input", inputCfg);
                    fuel.set("output", inputCfg);//...don't worry about it, it's fine
                    Config fuelCfg = Config.newConfig();
                    fuelCfg.set("efficiency", f.stats.efficiency);
                    fuelCfg.set("heat", f.stats.heat);
                    fuelCfg.set("time", f.stats.time);
                    fuelCfg.set("criticality", f.stats.criticality);
                    if(f.stats.selfPriming)fuelCfg.set("selfPriming", true);
                    fuel.set("fuelCell", fuelCfg);
                    recipesCfg.add(fuel);
                }
                block.set("recipes", recipesCfg);
            }
            if(b.irradiator!=null){
                Config irradiatorCfg = Config.newConfig();
                irradiatorCfg.set("hasBaseStats", false);
                block.set("irradiator", irradiatorCfg);
                ConfigList recipesCfg = new ConfigList();
                for( IrradiatorRecipe r : b.irradiatorRecipes){
                    Config recipe = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.set("name", r.definition.toString());
                    if(r.names.displayName!=null)inputCfg.set("displayName", r.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, r.texture.texture);
                    recipe.set("input", inputCfg);
                    recipe.set("output", inputCfg);//...don't worry about it, it's fine
                    Config irrecipeCfg = Config.newConfig();
                    irrecipeCfg.set("efficiency", r.stats.efficiency);
                    irrecipeCfg.set("heat", r.stats.heat);
                    recipe.set("irradiator", irrecipeCfg);
                    recipesCfg.add(recipe);
                }
                block.set("recipes", recipesCfg);
            }
            if(b.reflector!=null){
                Config reflectorCfg = Config.newConfig();
                reflectorCfg.set("hasBaseStats", true);
                reflectorCfg.set("efficiency", b.reflector.efficiency);
                reflectorCfg.set("reflectivity", b.reflector.reflectivity);
                block.set("reflector", reflectorCfg);
            }
            if(b.moderator!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.set("hasBaseStats", true);
                moderatorCfg.set("flux", b.moderator.flux);
                moderatorCfg.set("efficiency", b.moderator.efficiency);
                moderatorCfg.set("active", true);
                block.set("moderator", moderatorCfg);
            }
            if(b.neutronShield!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.set("hasBaseStats", true);
                moderatorCfg.set("flux", 0);
                moderatorCfg.set("efficiency", b.neutronShield.efficiency);
                moderatorCfg.set("active", false);
                block.set("moderator", moderatorCfg);
                Config shieldCfg = Config.newConfig();
                shieldCfg.set("hasBaseStats", true);
                shieldCfg.set("heat", b.neutronShield.heatPerFlux);
                shieldCfg.set("efficiency", b.neutronShield.efficiency);
                LegacyNCPFWriter.saveTexture(shieldCfg, "closedTexture", b.toggled.texture.texture);
                block.set("shield", shieldCfg);
            }
            if(b.heatsink!=null){
                Config heatsinkCfg = Config.newConfig();
                heatsinkCfg.set("hasBaseStats", true);
                heatsinkCfg.set("cooling", b.heatsink.cooling);
                block.set("heatsink", heatsinkCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.heatsink.rules){
                    ruls.add(savePlacementRule(rule, sfr, overhaulSFRBlockTypes));
                }
                block.set("rules", ruls);
            }
            if(b.neutronSource!=null){
                Config sourceCfg = Config.newConfig();
                sourceCfg.set("efficiency", b.neutronSource.efficiency);
                block.set("source", sourceCfg);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture.texture);
            if(b.recipePorts!=null){
                Config portCfg = Config.newConfig();
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement in = b.recipePorts.input.block;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement out = b.recipePorts.output.block;
                portCfg.set("name", in.definition.toString());
                if(in.names.displayName!=null)portCfg.set("inputDisplayName", in.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "inputTexture", in.texture.texture);
                if(out.names.displayName!=null)portCfg.set("outputDisplayName", out.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "outputTexture", out.texture.texture);
                block.set("port", portCfg);
            }
            blocks.add(block);
        }
        config.set("blocks", blocks);
        ConfigList coolantRecipes = new ConfigList();
        for(CoolantRecipe r : sfr.coolantRecipes){
            Config recipe = Config.newConfig();
            Config inputCfg = Config.newConfig();
            inputCfg.set("name", r.definition.toString());
            if(r.names.displayName!=null)inputCfg.set("displayName", r.names.displayName);
            LegacyNCPFWriter.saveTexture(inputCfg, r.texture.texture);
            recipe.set("input", inputCfg);
            recipe.set("output", inputCfg);//...don't worry about it, it's fine
            recipe.set("heat", r.stats.heat);
            recipe.set("outputRatio", r.stats.outputRatio);
            coolantRecipes.add(recipe);
        }
        config.set("coolantRecipes", coolantRecipes);
        return config;
    }
    private Config saveOverhaulMSRConfiguration(OverhaulMSRConfiguration msr){
        Config config = Config.newConfig();
        config.set("minSize", msr.settings.minSize);
        config.set("maxSize", msr.settings.maxSize);
        config.set("neutronReach", msr.settings.neutronReach);
        config.set("coolingEfficiencyLeniency", msr.settings.coolingEfficiencyLeniency);
        config.set("sparsityPenaltyMult", msr.settings.sparsityPenaltyMultiplier);
        config.set("sparsityPenaltyThreshold", msr.settings.sparsityPenaltyThreshold);
        ConfigList blocks = new ConfigList();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement b : msr.blocks){
            if(b.port!=null)continue;//don't save ports, because UGH
            Config block = Config.newConfig();
            block.set("name", b.definition.toString());
            if(b.names.displayName!=null)block.set("displayName", b.names.displayName);
            block.set("cluster", b.heater!=null||b.neutronShield!=null||b.conductor!=null||b.fuelVessel!=null||b.irradiator!=null);
            block.set("createCluster", b.fuelVessel!=null||b.irradiator!=null||b.neutronShield!=null);
            block.set("functional", b.fuelVessel!=null||b.irradiator!=null||b.heater!=null||b.reflector!=null||b.neutronShield!=null);
            block.set("blocksLOS", b.reflector!=null||b.fuelVessel!=null||b.irradiator!=null);
            block.set("casing", b.casing!=null);
            if(b.casing!=null)block.set("casingEdge", b.casing.edge);
            block.set("controller", b.controller!=null);
            if(b.fuelVessel!=null){
                Config fuelVesselCfg = Config.newConfig();
                fuelVesselCfg.set("hasBaseStats", false);
                block.set("fuelVessel", fuelVesselCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel f : b.fuels){
                    Config fuel = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.set("name", f.definition.toString());
                    if(f.names.displayName!=null)inputCfg.set("displayName", f.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, f.texture.texture);
                    inputCfg.set("rate", 1);
                    fuel.set("input", inputCfg);
                    fuel.set("output", inputCfg);//...don't worry about it, it's fine
                    Config fuelCfg = Config.newConfig();
                    fuelCfg.set("efficiency", f.stats.efficiency);
                    fuelCfg.set("heat", f.stats.heat);
                    fuelCfg.set("time", f.stats.time);
                    fuelCfg.set("criticality", f.stats.criticality);
                    if(f.stats.selfPriming)fuelCfg.set("selfPriming", true);
                    fuel.set("fuelVessel", fuelCfg);
                    recipesCfg.add(fuel);
                }
                block.set("recipes", recipesCfg);
            }
            if(b.irradiator!=null){
                Config irradiatorCfg = Config.newConfig();
                irradiatorCfg.set("hasBaseStats", false);
                block.set("irradiator", irradiatorCfg);
                ConfigList recipesCfg = new ConfigList();
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe r : b.irradiatorRecipes){
                    Config recipe = Config.newConfig();
                    Config inputCfg = Config.newConfig();
                    inputCfg.set("name", r.definition.toString());
                    if(r.names.displayName!=null)inputCfg.set("displayName", r.names.displayName);
                    LegacyNCPFWriter.saveTexture(inputCfg, r.texture.texture);
                    recipe.set("input", inputCfg);
                    recipe.set("output", inputCfg);//...don't worry about it, it's fine
                    Config irrecipeCfg = Config.newConfig();
                    irrecipeCfg.set("efficiency", r.stats.efficiency);
                    irrecipeCfg.set("heat", r.stats.heat);
                    recipe.set("irradiator", irrecipeCfg);
                    recipesCfg.add(recipe);
                }
                block.set("recipes", recipesCfg);
            }
            if(b.reflector!=null){
                Config reflectorCfg = Config.newConfig();
                reflectorCfg.set("hasBaseStats", true);
                reflectorCfg.set("efficiency", b.reflector.efficiency);
                reflectorCfg.set("reflectivity", b.reflector.reflectivity);
                block.set("reflector", reflectorCfg);
            }
            if(b.moderator!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.set("hasBaseStats", true);
                moderatorCfg.set("flux", b.moderator.flux);
                moderatorCfg.set("efficiency", b.moderator.efficiency);
                moderatorCfg.set("active", true);
                block.set("moderator", moderatorCfg);
            }
            if(b.neutronShield!=null){
                Config moderatorCfg = Config.newConfig();
                moderatorCfg.set("hasBaseStats", true);
                moderatorCfg.set("flux", 0);
                moderatorCfg.set("efficiency", b.neutronShield.efficiency);
                moderatorCfg.set("active", false);
                block.set("moderator", moderatorCfg);
                Config shieldCfg = Config.newConfig();
                shieldCfg.set("hasBaseStats", true);
                shieldCfg.set("heat", b.neutronShield.heatPerFlux);
                shieldCfg.set("efficiency", b.neutronShield.efficiency);
                LegacyNCPFWriter.saveTexture(shieldCfg, "closedTexture", b.toggled.texture.texture);
                block.set("shield", shieldCfg);
            }
            if(b.heater!=null){
                Config heatsinkCfg = Config.newConfig();
                heatsinkCfg.set("hasBaseStats", true);
                heatsinkCfg.set("cooling", b.heaterRecipes.get(0).stats.cooling);
                block.set("heater", heatsinkCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.heater.rules){
                    ruls.add(savePlacementRule(rule, msr, overhaulMSRBlockTypes));
                }
                block.set("rules", ruls);
            }
            if(b.neutronSource!=null){
                Config sourceCfg = Config.newConfig();
                sourceCfg.set("efficiency", b.neutronSource.efficiency);
                block.set("source", sourceCfg);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture.texture);
            if(b.recipePorts!=null){
                Config portCfg = Config.newConfig();
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement in = b.recipePorts.input.block;
                net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement out = b.recipePorts.output.block;
                portCfg.set("name", in.definition.toString());
                if(in.names.displayName!=null)portCfg.set("inputDisplayName", in.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "inputTexture", in.texture.texture);
                if(out.names.displayName!=null)portCfg.set("outputDisplayName", out.names.displayName);
                LegacyNCPFWriter.saveTexture(portCfg, "outputTexture", out.texture.texture);
                block.set("port", portCfg);
            }
            blocks.add(block);
        }
        config.set("blocks", blocks);
        return config;
    }
    private Config saveOverhaulTurbineConfiguration(OverhaulTurbineConfiguration turbine){
        Config config = Config.newConfig();
        config.set("minWidth", turbine.settings.minWidth);
        config.set("minLength", turbine.settings.minLength);
        config.set("maxSize", turbine.settings.maxSize);
        config.set("fluidPerBlade", turbine.settings.fluidPerBlade);
        config.set("throughputEfficiencyLeniencyMult", turbine.settings.throughputEfficiencyLeniencyMultiplier);
        config.set("throughputEfficiencyLeniencyThreshold", turbine.settings.throughputEfficiencyLeniencyThreshold);
        config.set("throughputFactor", turbine.settings.throughputFactor);
        config.set("powerBonus", turbine.settings.powerBonus);
        ConfigList blocks = new ConfigList();
        for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulTurbine.BlockElement b : turbine.blocks){
            Config block = Config.newConfig();
            block.set("name", b.definition.toString());
            if(b.names.displayName!=null)block.set("displayName", b.names.displayName);
            if(b.bearing!=null)block.set("bearing", true);
            if(b.shaft!=null)block.set("shaft", true);
            if(b.connector!=null){
                block.set("connector", true);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.connector.rules){
                    ruls.add(savePlacementRule(rule, turbine, overhaulMSRBlockTypes));
                }
                block.set("rules", ruls);
            }
            if(b.controller!=null)block.set("controller", true);
            if(b.casing!=null){
                block.set("casing", true);
                block.set("casingEdge", b.casing.edge);
            }
            if(b.inlet!=null)block.set("inlet", true);
            if(b.outlet!=null)block.set("outlet", true);
            if(b.blade!=null){
                Config bladeCfg = Config.newConfig();
                bladeCfg.set("efficiency", b.blade.efficiency);
                bladeCfg.set("expansion", b.blade.expansion);
                bladeCfg.set("stator", false);
                block.set("blade", bladeCfg);
            }
            if(b.stator!=null){
                Config bladeCfg = Config.newConfig();
                bladeCfg.set("efficiency", 0);
                bladeCfg.set("expansion", b.stator.expansion);
                bladeCfg.set("stator", true);
                block.set("blade", bladeCfg);
            }
            if(b.coil!=null){
                Config coilCfg = Config.newConfig();
                coilCfg.set("efficiency", b.coil.efficiency);
                block.set("coil", coilCfg);
                ConfigList ruls = new ConfigList();
                for(NCPFPlacementRule rule : b.coil.rules){
                    ruls.add(savePlacementRule(rule, turbine, overhaulMSRBlockTypes));
                }
                block.set("rules", ruls);
            }
            LegacyNCPFWriter.saveTexture(block, b.texture.texture);
            blocks.add(block);
        }
        config.set("blocks", blocks);
        ConfigList recipes = new ConfigList();
        for(Recipe r : turbine.recipes){
            Config recipe = Config.newConfig();
            Config inputCfg = Config.newConfig();
            inputCfg.set("name", r.definition.toString());
            if(r.names.displayName!=null)inputCfg.set("displayName", r.names.displayName);
            LegacyNCPFWriter.saveTexture(inputCfg, r.texture.texture);
            recipe.set("input", inputCfg);
            recipe.set("output", inputCfg);//...don't worry about it, it's fine
            recipe.set("power", r.stats.power);
            recipe.set("coefficient", r.stats.coefficient);
            recipes.add(recipe);
        }
        config.set("recipes", recipes);
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
        config.set("type", (byte) indexof(rule.rule, ruleTypes));
        switch (rule.rule) {
            case BETWEEN:
            case AXIAL:
                saveRuleTarget(rule, config, cfg, blockTypes);
                config.set("min", (byte)rule.min);
                config.set("max", (byte)rule.max);
                break;
            case VERTEX:
            case EDGE:
                saveRuleTarget(rule, config, cfg, blockTypes);
                break;
            case OR:
            case AND:
                ConfigList ruls = new ConfigList();
                for (NCPFPlacementRule rul : rule.rules) {
                    ruls.add(savePlacementRule(rul, cfg, blockTypes));
                }
                config.set("rules", ruls);
                break;
        }
        return config;
    }
    private void saveRuleTarget(NCPFPlacementRule rule, Config config, NCPFConfiguration cfg, Supplier<NCPFModule>[] blockTypes) {
        boolean isSpecificBlock = !rule.target.definition.typeMatches(NCPFModuleElement::new);
        config.set("isSpecificBlock", isSpecificBlock);
        if (isSpecificBlock) {
            config.set("block", indexof(rule.target, cfg.getElements()) + 1);
        } else {
            config.set("blockType", (byte)mindexof(((NCPFModuleElement)rule.target.definition).name, blockTypes));
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
    private int indexof(NCPFElementReference elem, List<NCPFElement>[] arr){
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
        config.set("id", id);
        Config meta = Config.newConfig();
        for(String key : design.metadata.metadata.keySet()){
            String value = design.metadata.get(key);
            if(value.trim().isEmpty())continue;
            meta.set(key,value);
        }
        if(meta.properties().length>0){
            config.set("metadata", meta);
        }
        ConfigNumberList dimensions = new ConfigNumberList();
        dimensions.add(definition.design.length-2);
        dimensions.add(definition.design[0].length-2);
        dimensions.add(definition.design[0][0].length-2);
        config.set("dimensions", dimensions);
        if(design instanceof UnderhaulSFRDesign){
            UnderhaulSFRDesign sfr = (UnderhaulSFRDesign)design;
            UnderhaulSFRConfiguration cfg = configuration.getConfiguration(UnderhaulSFRConfiguration::new);
            config.set("fuel", cfg.fuels.indexOf(sfr.fuel));
            config.set("compact", true);
            ConfigNumberList blox = new ConfigNumberList();
            for(int x = 0; x<sfr.design.length; x++){
                for(int y = 0; y<sfr.design[x].length; y++){
                    for(int z = 0; z<sfr.design[x][y].length; z++){
                        BlockElement block = sfr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else blox.add(cfg.blocks.indexOf(block)+1);
                    }
                }
            }
            config.set("blocks", blox);
        }
        if(design instanceof OverhaulSFRDesign){
            OverhaulSFRDesign sfr = (OverhaulSFRDesign)design;
            config.set("compact", true);
            OverhaulSFRConfiguration cfg = configuration.getConfiguration(OverhaulSFRConfiguration::new);
            int cr = cfg.coolantRecipes.indexOf(sfr.coolantRecipe);
            config.set("coolantRecipe", Math.max(cr, 0));//give a default if it's none
            ConfigNumberList blox = new ConfigNumberList();
            ConfigNumberList blockRecipes = new ConfigNumberList();
            ConfigNumberList ports = new ConfigNumberList();
            for(int x = 0; x<sfr.design.length; x++){
                for(int y = 0; y<sfr.design[x].length; y++){
                    for(int z = 0; z<sfr.design[x][y].length; z++){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement block = sfr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else{
                            if(block.port!=null||block.coolantVent!=null)ports.add(block.unToggled!=null?1:0);
                            if(block.port!=null)block = block.parent;
                            if(block.coolantVent!=null&&block.unToggled!=null)block = block.unToggled;
                            blox.add(cfg.blocks.indexOf(block)+1);
                            if(block.fuelCell!=null){
                                blockRecipes.add(block.fuels.indexOf(sfr.fuels[x][y][z])+1);
                            }
                            if(block.irradiator!=null){
                                blockRecipes.add(block.irradiatorRecipes.indexOf(sfr.irradiatorRecipes[x][y][z])+1);
                            }
                        }
                    }
                }
            }
            config.set("blocks", blox);
            config.set("blockRecipes", blockRecipes);
            config.set("ports", ports);
        }
        if(design instanceof OverhaulMSRDesign){
            OverhaulMSRDesign msr = (OverhaulMSRDesign)design;
            config.set("compact", true);
            OverhaulMSRConfiguration cfg = configuration.getConfiguration(OverhaulMSRConfiguration::new);
            ConfigNumberList blox = new ConfigNumberList();
            ConfigNumberList blockRecipes = new ConfigNumberList();
            ConfigNumberList ports = new ConfigNumberList();
            for(int x = 0; x<msr.design.length; x++){
                for(int y = 0; y<msr.design[x].length; y++){
                    for(int z = 0; z<msr.design[x][y].length; z++){
                        net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement block = msr.design[x][y][z];
                        if(block==null)blox.add(0);
                        else{
                            if(block.port!=null){
                                ports.add(block.unToggled!=null?1:0);
                                block = block.parent;
                            }
                            blox.add(cfg.blocks.indexOf(block)+1);
                            if(block.fuelVessel!=null){
                                blockRecipes.add(block.fuels.indexOf(msr.fuels[x][y][z])+1);
                            }
                            if(block.irradiator!=null){
                                blockRecipes.add(block.irradiatorRecipes.indexOf(msr.irradiatorRecipes[x][y][z])+1);
                            }
                        }
                    }
                }
            }
            config.set("blocks", blox);
            config.set("blockRecipes", blockRecipes);
            config.set("ports", ports);
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
                        else blocks.add(cfg.blocks.indexOf(block)+1);
                    }
                }
            }
            config.set("blocks", blocks);
            config.set("recipe", cfg.recipes.indexOf(turbine.recipe));
        }
        return config;
    }
}