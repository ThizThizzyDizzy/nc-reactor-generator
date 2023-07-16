package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.config2.Config;
import net.ncplanner.plannerator.config2.ConfigList;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.ForgeConfig;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.builder.UnderhaulSFRConfigurationBuilder;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.CasingModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.FuelCellModule;
import net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR.ModeratorModule;
public class UnderhaulNCConfigReader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        return ForgeConfig.parse(in).getConfig("fission").hasProperty("fission_cooling_rate");
    }
    @Override
    public synchronized Project read(InputStream in, RecoveryHandler recovery){
        Config config = ForgeConfig.parse(in).getConfig("fission");
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
        builder.controller("nuclearcraft:fission_controller_new_fixed", "Fission Controller", "underhaul/controller");
        builder.casing("nuclearcraft:fission_block:0", "Casing", "underhaul/casing");
        builder.casing("nuclearcraft:reactor_casing_transparent", "Transparent Casing", "underhaul/transparent_casing");
        Block cell = builder.fuelCell("nuclearcraft:cell_block", "Reactor Cell", "underhaul/cell");
        cell.names.legacyNames.add("Fuel Cell");
        Block water = builder.cooler("nuclearcraft:cooler:1", "Water Cooler", (int)coolingRates.getDouble(0), "underhaul/water", builder.or(builder.atLeast(1, FuelCellModule::new), builder.atLeast(1, ModeratorModule::new)));
        if(!waterCoolerRequirements){
            water.cooler.rules.clear();
        }
        Block redstone = builder.cooler("nuclearcraft:cooler:2", "Redstone Cooler", (int)coolingRates.getDouble(1), "underhaul/redstone", builder.atLeast(1, FuelCellModule::new));
        Block quartz = builder.cooler("nuclearcraft:cooler:3", "Quartz Cooler", (int)coolingRates.getDouble(2), "underhaul/quartz", builder.atLeast(1, ModeratorModule::new));
        Block gold = builder.cooler("nuclearcraft:cooler:4", "Gold Cooler", (int)coolingRates.getDouble(3), "underhaul/gold", builder.atLeast(1, water), builder.atLeast(1, redstone));
        Block glowstone = builder.cooler("nuclearcraft:cooler:5", "Glowstone Cooler", (int)coolingRates.getDouble(4), "underhaul/glowstone", builder.atLeast(2, ModeratorModule::new));
        Block lapis = builder.cooler("nuclearcraft:cooler:6", "Lapis Cooler", (int)coolingRates.getDouble(5), "underhaul/lapis", builder.atLeast(1, FuelCellModule::new),builder.atLeast(1, CasingModule::new));
        Block diamond = builder.cooler("nuclearcraft:cooler:7", "Diamond Cooler",  (int)coolingRates.getDouble(6), "underhaul/diamond", builder.atLeast(1, water), builder.atLeast(1, quartz));
        Block helium = builder.cooler("nuclearcraft:cooler:8", "Liquid Helium Cooler", (int)coolingRates.getDouble(7), "underhaul/helium", builder.exactly(1, redstone), builder.atLeast(1, CasingModule::new));
        helium.names.legacyNames.add("Helium Cooler");
        Block enderium = builder.cooler("nuclearcraft:cooler:9", "Enderium Cooler", (int)coolingRates.getDouble(8), "underhaul/enderium", builder.and(builder.exactly(3, CasingModule::new),builder.vertex(CasingModule::new)));
        Block cryotheum = builder.cooler("nuclearcraft:cooler:10", "Cryotheum Cooler", (int)coolingRates.getDouble(9), "underhaul/cryotheum", builder.atLeast(2, FuelCellModule::new));
        Block iron = builder.cooler("nuclearcraft:cooler:11", "Iron Cooler", (int)coolingRates.getDouble(10), "underhaul/iron", builder.atLeast(1, gold));
        Block emerald = builder.cooler("nuclearcraft:cooler:12", "Emerald Cooler", (int)coolingRates.getDouble(11), "underhaul/emerald", builder.atLeast(1, ModeratorModule::new), builder.atLeast(1, FuelCellModule::new));
        Block copper = builder.cooler("nuclearcraft:cooler:13", "Copper Cooler", (int)coolingRates.getDouble(12), "underhaul/copper", builder.atLeast(1, glowstone));
        Block tin = builder.cooler("nuclearcraft:cooler:14", "Tin Cooler", (int)coolingRates.getDouble(13), "underhaul/tin", builder.axis(lapis));
        Block magnesium = builder.cooler("nuclearcraft:cooler:15", "Magnesium Cooler", (int)coolingRates.getDouble(14), "underhaul/magnesium", builder.atLeast(1, CasingModule::new), builder.atLeast(1, ModeratorModule::new));
        builder.moderator("nuclearcraft:ingot_block:8", "Graphite", "underhaul/graphite");
        builder.moderator("nuclearcraft:ingot_block:9", "Beryllium", "underhaul/beryllium");
        ConfigList activeCoolingRates = config.getConfigList("fission_active_cooling_rate");
        builder.activeCooler("nuclearcraft:active_cooler", "Active Cooler", "underhaul/active");
        builder.activeRecipe((int)activeCoolingRates.getDouble(0), "Water", "underhaul/water", builder.or(builder.atLeast(1, FuelCellModule::new), builder.atLeast(1, ModeratorModule::new)));
        builder.activeRecipe((int)activeCoolingRates.getDouble(1), "Destabilized Redstone", "underhaul/redstone", builder.atLeast(1, FuelCellModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(2), "Molten Quartz", "underhaul/quartz", builder.atLeast(1, ModeratorModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(3), "Molten Gold", "underhaul/gold", builder.atLeast(1, water), builder.atLeast(1, redstone));
        builder.activeRecipe((int)activeCoolingRates.getDouble(4), "Energized Glowstone", "underhaul/glowstone", builder.atLeast(2, ModeratorModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(5), "Molten Lapis", "underhaul/lapis", builder.atLeast(1, FuelCellModule::new),builder.atLeast(1, CasingModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(6), "Molten Diamond", "underhaul/diamond", builder.atLeast(1, water), builder.atLeast(1, quartz));
        builder.activeRecipe((int)activeCoolingRates.getDouble(7), "Liquid Helium", "underhaul/helium", builder.exactly(1, redstone), builder.atLeast(1, CasingModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(8), "Resonant Ender", "underhaul/enderium", builder.and(builder.exactly(3, CasingModule::new),builder.vertex(CasingModule::new)));
        builder.activeRecipe((int)activeCoolingRates.getDouble(9), "Gelid Cryotheum", "underhaul/cryotheum", builder.atLeast(2, FuelCellModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(10), "Molten Iron", "underhaul/iron", builder.atLeast(1, gold));
        builder.activeRecipe((int)activeCoolingRates.getDouble(11), "Molten Emerald", "underhaul/emerald", builder.atLeast(1, ModeratorModule::new), builder.atLeast(1, FuelCellModule::new));
        builder.activeRecipe((int)activeCoolingRates.getDouble(12), "Molten Copper", "underhaul/copper", builder.atLeast(1, glowstone));
        builder.activeRecipe((int)activeCoolingRates.getDouble(13), "Molten Tin", "underhaul/tin", builder.axis(lapis));
        builder.activeRecipe((int)activeCoolingRates.getDouble(14), "Molten Magnesium", "underhaul/magnesium", builder.atLeast(1, CasingModule::new), builder.atLeast(1, ModeratorModule::new));
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