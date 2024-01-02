package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.multiblock.configuration.TextureManager;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyBlockElement;
import net.ncplanner.plannerator.ncpf.element.NCPFOredictElement;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.ForgeConfig;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.builder.UnderhaulSFRConfigurationBuilder;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class UnderhaulNCConfigReader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> in){
        return ForgeConfig.parse(in.get()).getConfig("fission").hasProperty("fission_cooling_rate");
    }
    @Override
    public synchronized Project read(Supplier<InputStream> in, RecoveryHandler recovery){
        Config config = ForgeConfig.parse(in.get()).getConfig("fission");
        Project ncpf = new Project();
        UnderhaulSFRConfigurationBuilder builder = new UnderhaulSFRConfigurationBuilder("NuclearCraft", "Unknown");
        boolean waterCoolerRequirements = config.getBoolean("fission_water_cooler_requirement");
        double powerMult = config.getDouble("fission_power");
        double fuelUseMult = config.getDouble("fission_fuel_use");
        double heatMult = config.getDouble("fission_heat_generation");
        builder.settings.minSize = config.getInt("fission_min_size");
        builder.settings.maxSize = config.getInt("fission_max_size");
        builder.settings.neutronReach = config.getInt("fission_neutron_reach");
        builder.settings.moderatorExtraPower = (float) config.getDouble("fission_moderator_extra_power");
        builder.settings.moderatorExtraHeat = (float) config.getDouble("fission_moderator_extra_heat");
        builder.settings.activeCoolerRate = config.getInt("fission_active_cooler_max_rate");
        ConfigList coolingRates = config.getConfigList("fission_cooling_rate");
        builder.block("nuclearcraft:fission_controller_new_fixed", "Fission Controller", "underhaul/controller").controller();
        builder.block("nuclearcraft:fission_block:0", "Casing", "underhaul/casing").blockstate("type", "casing").casing();
        builder.block("nuclearcraft:reactor_casing_transparent", "Transparent Casing", "underhaul/transparent_casing").casing();
        builder.block("nuclearcraft:cell_block", "Reactor Cell", "underhaul/cell").cell().legacy("Fuel Cell");
        BlockElement water = builder.block("nuclearcraft:cooler:1", "Water Cooler", "underhaul/water").blockstate("type", "water").cooler((int)coolingRates.getDouble(0), builder.or(builder.atLeast(1, FuelCellModule::new), builder.atLeast(1, ModeratorModule::new))).block;
        if(!waterCoolerRequirements)water.cooler.rules.clear();
        BlockElement redstone = builder.block("nuclearcraft:cooler:2", "Redstone Cooler", "underhaul/redstone").blockstate("type", "redstone").cooler((int)coolingRates.getDouble(1), builder.atLeast(1, FuelCellModule::new)).block;
        BlockElement quartz = builder.block("nuclearcraft:cooler:3", "Quartz Cooler", "underhaul/quartz").blockstate("type", "quartz").cooler((int)coolingRates.getDouble(2), builder.atLeast(1, ModeratorModule::new)).block;
        BlockElement gold = builder.block("nuclearcraft:cooler:4", "Gold Cooler", "underhaul/gold").blockstate("type", "gold").cooler((int)coolingRates.getDouble(3), builder.atLeast(1, water), builder.atLeast(1, redstone)).block;
        BlockElement glowstone = builder.block("nuclearcraft:cooler:5", "Glowstone Cooler", "underhaul/glowstone").blockstate("type", "glowstone").cooler((int)coolingRates.getDouble(4), builder.atLeast(2, ModeratorModule::new)).block;
        BlockElement lapis = builder.block("nuclearcraft:cooler:6", "Lapis Cooler", "underhaul/lapis").blockstate("type", "lapis").cooler((int)coolingRates.getDouble(5), builder.atLeast(1, FuelCellModule::new),builder.atLeast(1, CasingModule::new)).block;
        BlockElement diamond = builder.block("nuclearcraft:cooler:7", "Diamond Cooler",  "underhaul/diamond").blockstate("type", "diamond").cooler((int)coolingRates.getDouble(6), builder.atLeast(1, water), builder.atLeast(1, quartz)).block;
        BlockElement helium = builder.block("nuclearcraft:cooler:8", "Liquid Helium Cooler", "underhaul/helium").blockstate("type", "helium").legacy("Helium Cooler").cooler((int)coolingRates.getDouble(7), builder.exactly(1, redstone), builder.atLeast(1, CasingModule::new)).block;
        BlockElement enderium = builder.block("nuclearcraft:cooler:9", "Enderium Cooler", "underhaul/enderium").blockstate("type", "enderium").cooler((int)coolingRates.getDouble(8), builder.and(builder.exactly(3, CasingModule::new),builder.vertex(CasingModule::new))).block;
        BlockElement cryotheum = builder.block("nuclearcraft:cooler:10", "Cryotheum Cooler", "underhaul/cryotheum").blockstate("type", "cryotheum").cooler((int)coolingRates.getDouble(9), builder.atLeast(2, FuelCellModule::new)).block;
        BlockElement iron = builder.block("nuclearcraft:cooler:11", "Iron Cooler", "underhaul/iron").blockstate("type", "iron").cooler((int)coolingRates.getDouble(10), builder.atLeast(1, gold)).block;
        BlockElement emerald = builder.block("nuclearcraft:cooler:12", "Emerald Cooler", "underhaul/emerald").blockstate("type", "emerald").cooler((int)coolingRates.getDouble(11), builder.atLeast(1, ModeratorModule::new), builder.atLeast(1, FuelCellModule::new)).block;
        BlockElement copper = builder.block("nuclearcraft:cooler:13", "Copper Cooler", "underhaul/copper").blockstate("type", "copper").cooler((int)coolingRates.getDouble(12), builder.atLeast(1, glowstone)).block;
        BlockElement tin = builder.block("nuclearcraft:cooler:14", "Tin Cooler", "underhaul/tin").blockstate("type", "tin").cooler((int)coolingRates.getDouble(13), builder.axis(lapis)).block;
        BlockElement magnesium = builder.block("nuclearcraft:cooler:15", "Magnesium Cooler", "underhaul/magnesium").blockstate("type", "magnesium").cooler((int)coolingRates.getDouble(14), builder.atLeast(1, CasingModule::new), builder.atLeast(1, ModeratorModule::new)).block;
        builder.block(new NCPFOredictElement("blockFissionModerator"), "Moderator", "underhaul/graphite").moderator()
                .legacy("Graphite", "nuclearcraft:ingot_block:8")
                .legacy("Beryllium", "nuclearcraft:ingot_block:9");
        ConfigList activeCoolingRates = config.getConfigList("fission_active_cooling_rate");
        builder.block("nuclearcraft:active_cooler", "Active Cooler", "underhaul/active").activeCooler();
        builder.activeRecipe((int)activeCoolingRates.getDouble(0), "water", "Water", builder.or(builder.atLeast(1, FuelCellModule::new), builder.atLeast(1, ModeratorModule::new))).texture("fluids/water").legacy("Water");
        builder.activeRecipe((int)activeCoolingRates.getDouble(1), "redstone", "Molten Redstone", builder.atLeast(1, FuelCellModule::new)).texture(TextureManager.MOLTEN, 0xAB1C09).legacy("Destabilized Redstone");
        builder.activeRecipe((int)activeCoolingRates.getDouble(2), "quartz", "Molten Quartz", builder.atLeast(1, ModeratorModule::new)).texture(TextureManager.MOLTEN, 0xECE9E2).legacy("Molten Quartz");
        builder.activeRecipe((int)activeCoolingRates.getDouble(3), "gold", "Molten Gold", builder.atLeast(1, water), builder.atLeast(1, redstone)).texture(TextureManager.MOLTEN, 0xE6DA3C).legacy("Molten Gold");
        builder.activeRecipe((int)activeCoolingRates.getDouble(4), "glowstone", "Molten Glowstone", builder.atLeast(2, ModeratorModule::new)).texture(TextureManager.MOLTEN, 0xA38037).legacy("Energized Glowstone");
        builder.activeRecipe((int)activeCoolingRates.getDouble(5), "lapis", "Molten Lapis", builder.atLeast(1, FuelCellModule::new),builder.atLeast(1, CasingModule::new)).texture(TextureManager.MOLTEN, 0x27438A).legacy("Molten Lapis");
        builder.activeRecipe((int)activeCoolingRates.getDouble(6), "diamond", "Molten Diamond", builder.atLeast(1, water), builder.atLeast(1, quartz)).texture(TextureManager.MOLTEN, 0x6FDFDA).legacy("Molten Diamond");
        builder.activeRecipe((int)activeCoolingRates.getDouble(7), "liquidhelium", "Liquid Helium", builder.exactly(1, redstone), builder.atLeast(1, CasingModule::new)).texture("fluids/liquidhelium").legacy("Liquid Helium");
        builder.activeRecipe((int)activeCoolingRates.getDouble(8), "ender", "Molten Ender", builder.and(builder.exactly(3, CasingModule::new),builder.vertex(CasingModule::new))).texture(TextureManager.MOLTEN, 0x14584D).legacy("Resonant Ender");
        builder.activeRecipe((int)activeCoolingRates.getDouble(9), "cryotheum", "Molten Cryotheum", builder.atLeast(2, FuelCellModule::new)).texture(TextureManager.MOLTEN, 0x0099C1).legacy("Gelid Cryotheum");
        builder.activeRecipe((int)activeCoolingRates.getDouble(10), "iron", "Molten Iron", builder.atLeast(1, gold)).texture(TextureManager.MOLTEN, 0x8D1515).legacy("Molten Iron");
        builder.activeRecipe((int)activeCoolingRates.getDouble(11), "emerald", "Molten Emerald", builder.atLeast(1, ModeratorModule::new), builder.atLeast(1, FuelCellModule::new)).texture(TextureManager.MOLTEN, 0x51D975).legacy("Molten Emerald");
        builder.activeRecipe((int)activeCoolingRates.getDouble(12), "copper", "Molten Copper", builder.atLeast(1, glowstone)).texture(TextureManager.MOLTEN, 0x5C2F1A).legacy("Molten Copper");
        builder.activeRecipe((int)activeCoolingRates.getDouble(13), "tin", "Molten Tin", builder.axis(lapis)).texture(TextureManager.MOLTEN, 0xD9DDF0).legacy("Molten Tin");
        builder.activeRecipe((int)activeCoolingRates.getDouble(14), "magnesium", "Molten Magnesium", builder.atLeast(1, CasingModule::new), builder.atLeast(1, ModeratorModule::new)).texture(TextureManager.MOLTEN, 0xEED5E1).legacy("Molten Magnesium");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "thorium", "TBU", "TBU Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "uranium", "LEU-233", "LEU-233 Oxide", "HEU-233", "HEU-233 Oxide", "LEU-235", "LEU-235 Oxide", "HEU-235", "HEU-235 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "neptunium", "LEN-236", "LEN-236 Oxide", "HEN-236", "HEN-236 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "plutonium", "LEP-239", "LEP-239 Oxide", "HEP-239", "HEP-239 Oxide", "LEP-241", "LEP-241 Oxide", "HEP-241", "HEP-241 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "mox", "MOX-239", "MOX-241");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "americium", "LEA-242", "LEA-242 Oxide", "HEA-242", "HEA-242 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "curium", "LECm-243", "LECm-243 Oxide", "HECm-243", "HECm-243 Oxide", "LECm-245", "LECm-245 Oxide", "HECm-245", "HECm-245 Oxide", "LECm-247", "LECm-247 Oxide", "HECm-247", "HECm-247 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "berkelium", "LEB-248", "LEB-248 Oxide", "HEB-248", "HEB-248 Oxide");
        addFuels(builder, config, powerMult, heatMult, fuelUseMult, "californium", "LECf-249", "LECf-249 Oxide", "HECf-249", "HECf-249 Oxide", "LECf-251", "LECf-251 Oxide", "HECf-251", "HECf-251 Oxide");
        ncpf.setConfiguration(builder.build());
        return ncpf;
    }
    private void addFuels(UnderhaulSFRConfigurationBuilder builder, Config config, double powerMult, double heatMult, double fuelUseMult, String baseName, String... fuelNames){
        ConfigList time = config.getConfigList("fission_"+baseName+"_fuel_time");
        ConfigList power = config.getConfigList("fission_"+baseName+"_power");
        ConfigList heat = config.getConfigList("fission_"+baseName+"_heat_generation");
        for(int i = 0; i<fuelNames.length; i++){
            builder.fuel("nuclearcraft:fuel_"+baseName+":"+i, fuelNames[i], (float)(power.getDouble(i)*powerMult), (float)(heat.getDouble(i)*heatMult), (int)(time.getDouble(i)/fuelUseMult), StringUtil.superReplace("underhaul/fuel/fuel_"+baseName+"_"+StringUtil.superReplace(StringUtil.toLowerCase(fuelNames[i]), " oxide", "_oxide", "-", "_", "ecm_", "ec_", "ecf_", "ec_"), "fuel_mox", "fuel_mixed_oxide"));
        }
    }
}