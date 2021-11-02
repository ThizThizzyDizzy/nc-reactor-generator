package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.UnderhaulConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.PlacementRule;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.ForgeConfig;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class UnderhaulNCConfigReader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        return ForgeConfig.parse(in).getConfig("fission").hasProperty("fission_cooling_rate");
    }
    @Override
    public synchronized NCPFFile read(InputStream in){
        Config config = ForgeConfig.parse(in).getConfig("fission");
        NCPFFile ncpf = new NCPFFile();
        ncpf.configuration = new Configuration("NuclearCraft", null, "Unknown");
        ncpf.configuration.underhaul = new UnderhaulConfiguration();
        ncpf.configuration.underhaul.fissionSFR = new FissionSFRConfiguration();
        boolean waterCoolerRequirements = config.getBoolean("fission_water_cooler_requirement");
        double powerMult = config.getDouble("fission_power");
        double fuelUseMult = config.getDouble("fission_fuel_use");
        double heatMult = config.getDouble("fission_heat_generation");
        ncpf.configuration.underhaul.fissionSFR.minSize = config.getInt("fission_min_size");
        ncpf.configuration.underhaul.fissionSFR.maxSize = config.getInt("fission_max_size");
        ncpf.configuration.underhaul.fissionSFR.neutronReach = config.getInt("fission_neutron_reach");
        ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = (float) config.getDouble("fission_moderator_extra_power");
        ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = (float) config.getDouble("fission_moderator_extra_heat");
        ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = config.getInt("fission_active_cooler_max_rate");
        ConfigList coolingRates = config.getConfigList("fission_cooling_rate");
        Block controller = Block.controller("nuclearcraft:fission_controller_new_fixed", "Fission Controller", "underhaul/controller");
        Block casing = Block.casing("nuclearcraft:fission_block:0", "Casing", "underhaul/casing");
        Block transparentCasing = Block.casing("nuclearcraft:reactor_casing_transparent", "Transparent Casing", "underhaul/transparent_casing");
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(controller);ncpf.configuration.underhaul.fissionSFR.blocks.add(controller);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(casing);ncpf.configuration.underhaul.fissionSFR.blocks.add(casing);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(transparentCasing);ncpf.configuration.underhaul.fissionSFR.blocks.add(transparentCasing);
        Block cell = Block.fuelCell("nuclearcraft:cell_block", "Reactor Cell", "underhaul/cell");
        cell.legacyNames.add("Fuel Cell");
        Block water = Block.cooler("nuclearcraft:cooler:1", "Water Cooler", (int)coolingRates.getDouble(0), "underhaul/water", PlacementRule.or(PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL), PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR)));
        if(!waterCoolerRequirements){
            water.rules.clear();
        }
        Block redstone = Block.cooler("nuclearcraft:cooler:2", "Redstone Cooler", (int)coolingRates.getDouble(1), "underhaul/redstone", PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL));
        Block quartz = Block.cooler("nuclearcraft:cooler:3", "Quartz Cooler", (int)coolingRates.getDouble(2), "underhaul/quartz", PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR));
        Block gold = Block.cooler("nuclearcraft:cooler:4", "Gold Cooler", (int)coolingRates.getDouble(3), "underhaul/gold", PlacementRule.atLeast(1, water), PlacementRule.atLeast(1, redstone));
        Block glowstone = Block.cooler("nuclearcraft:cooler:5", "Glowstone Cooler", (int)coolingRates.getDouble(4), "underhaul/glowstone", PlacementRule.atLeast(2, PlacementRule.BlockType.MODERATOR));
        Block lapis = Block.cooler("nuclearcraft:cooler:6", "Lapis Cooler", (int)coolingRates.getDouble(5), "underhaul/lapis", PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL),PlacementRule.atLeast(1, PlacementRule.BlockType.CASING));
        Block diamond = Block.cooler("nuclearcraft:cooler:7", "Diamond Cooler",  (int)coolingRates.getDouble(6), "underhaul/diamond", PlacementRule.atLeast(1, water), PlacementRule.atLeast(1, quartz));
        Block helium = Block.cooler("nuclearcraft:cooler:8", "Liquid Helium Cooler", (int)coolingRates.getDouble(7), "underhaul/helium", PlacementRule.exactly(1, redstone), PlacementRule.atLeast(1, PlacementRule.BlockType.CASING));
        helium.legacyNames.add("Helium Cooler");
        Block enderium = Block.cooler("nuclearcraft:cooler:9", "Enderium Cooler", (int)coolingRates.getDouble(8), "underhaul/enderium", PlacementRule.and(PlacementRule.exactly(3, PlacementRule.BlockType.CASING),PlacementRule.vertex(PlacementRule.BlockType.CASING)));
        Block cryotheum = Block.cooler("nuclearcraft:cooler:10", "Cryotheum Cooler", (int)coolingRates.getDouble(9), "underhaul/cryotheum", PlacementRule.atLeast(2, PlacementRule.BlockType.FUEL_CELL));
        Block iron = Block.cooler("nuclearcraft:cooler:11", "Iron Cooler", (int)coolingRates.getDouble(10), "underhaul/iron", PlacementRule.atLeast(1, gold));
        Block emerald = Block.cooler("nuclearcraft:cooler:12", "Emerald Cooler", (int)coolingRates.getDouble(11), "underhaul/emerald", PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR), PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL));
        Block copper = Block.cooler("nuclearcraft:cooler:13", "Copper Cooler", (int)coolingRates.getDouble(12), "underhaul/copper", PlacementRule.atLeast(1, glowstone));
        Block tin = Block.cooler("nuclearcraft:cooler:14", "Tin Cooler", (int)coolingRates.getDouble(13), "underhaul/tin", PlacementRule.axis(lapis));
        Block magnesium = Block.cooler("nuclearcraft:cooler:15", "Magnesium Cooler", (int)coolingRates.getDouble(14), "underhaul/magnesium", PlacementRule.atLeast(1, PlacementRule.BlockType.CASING), PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR));
        Block graphite = Block.moderator("nuclearcraft:ingot_block:8", "Graphite", "underhaul/graphite");
        Block beryllium = Block.moderator("nuclearcraft:ingot_block:9", "Beryllium", "underhaul/beryllium");
        ConfigList activeCoolingRates = config.getConfigList("fission_active_cooling_rate");
        Block activeWater = Block.activeCooler("nuclearcraft:active_cooler:1", "Active Water Cooler", (int)activeCoolingRates.getDouble(0), "Water", "underhaul/water", PlacementRule.or(PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL), PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR)));
        Block activeRedstone = Block.activeCooler("nuclearcraft:active_cooler:2", "Active Redstone Cooler", (int)activeCoolingRates.getDouble(1), "Destabilized Redstone", "underhaul/redstone", PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL));
        Block activeQuartz = Block.activeCooler("nuclearcraft:active_cooler:3", "Active Quartz Cooler", (int)activeCoolingRates.getDouble(2), "Molten Quartz", "underhaul/quartz", PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR));
        Block activeGold = Block.activeCooler("nuclearcraft:active_cooler:4", "Active Gold Cooler", (int)activeCoolingRates.getDouble(3), "Molten Gold", "underhaul/gold", PlacementRule.atLeast(1, water), PlacementRule.atLeast(1, redstone));
        Block activeGlowstone = Block.activeCooler("nuclearcraft:active_cooler:5", "Active Glowstone Cooler", (int)activeCoolingRates.getDouble(4), "Energized Glowstone", "underhaul/glowstone", PlacementRule.atLeast(2, PlacementRule.BlockType.MODERATOR));
        Block activeLapis = Block.activeCooler("nuclearcraft:active_cooler:6", "Active Lapis Cooler", (int)activeCoolingRates.getDouble(5), "Molten Lapis", "underhaul/lapis", PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL),PlacementRule.atLeast(1, PlacementRule.BlockType.CASING));
        Block activeDiamond = Block.activeCooler("nuclearcraft:active_cooler:7", "Active Diamond Cooler", (int)activeCoolingRates.getDouble(6), "Molten Diamond", "underhaul/diamond", PlacementRule.atLeast(1, water), PlacementRule.atLeast(1, quartz));
        Block activeHelium = Block.activeCooler("nuclearcraft:active_cooler:8", "Active Helium Cooler", (int)activeCoolingRates.getDouble(7), "Liquid Helium", "underhaul/helium", PlacementRule.exactly(1, redstone), PlacementRule.atLeast(1, PlacementRule.BlockType.CASING));
        Block activeEnderium = Block.activeCooler("nuclearcraft:active_cooler:9", "Active Enderium Cooler", (int)activeCoolingRates.getDouble(8), "Resonant Ender", "underhaul/enderium", PlacementRule.and(PlacementRule.exactly(3, PlacementRule.BlockType.CASING),PlacementRule.vertex(PlacementRule.BlockType.CASING)));
        Block activeCryotheum = Block.activeCooler("nuclearcraft:active_cooler:10", "Active Cryotheum Cooler", (int)activeCoolingRates.getDouble(9), "Gelid Cryotheum", "underhaul/cryotheum", PlacementRule.atLeast(2, PlacementRule.BlockType.FUEL_CELL));
        Block activeIron = Block.activeCooler("nuclearcraft:active_cooler:11", "Active Iron Cooler", (int)activeCoolingRates.getDouble(10), "Molten Iron", "underhaul/iron", PlacementRule.atLeast(1, gold));
        Block activeEmerald = Block.activeCooler("nuclearcraft:active_cooler:12", "Active Emerald Cooler", (int)activeCoolingRates.getDouble(11), "Molten Emerald", "underhaul/emerald", PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR), PlacementRule.atLeast(1, PlacementRule.BlockType.FUEL_CELL));
        Block activeCopper = Block.activeCooler("nuclearcraft:active_cooler:13", "Active Copper Cooler", (int)activeCoolingRates.getDouble(12), "Molten Copper", "underhaul/copper", PlacementRule.atLeast(1, glowstone));
        Block activeTin = Block.activeCooler("nuclearcraft:active_cooler:14", "Active Tin Cooler", (int)activeCoolingRates.getDouble(13), "Molten Tin", "underhaul/tin", PlacementRule.axis(lapis));
        Block activeMagnesium = Block.activeCooler("nuclearcraft:active_cooler:15", "Active Magnesium Cooler", (int)activeCoolingRates.getDouble(14), "Molten Magnesium", "underhaul/magnesium", PlacementRule.atLeast(1, PlacementRule.BlockType.CASING), PlacementRule.atLeast(1, PlacementRule.BlockType.MODERATOR));
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(cell);ncpf.configuration.underhaul.fissionSFR.blocks.add(cell);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(water);ncpf.configuration.underhaul.fissionSFR.blocks.add(water);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(redstone);ncpf.configuration.underhaul.fissionSFR.blocks.add(redstone);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(quartz);ncpf.configuration.underhaul.fissionSFR.blocks.add(quartz);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(gold);ncpf.configuration.underhaul.fissionSFR.blocks.add(gold);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(glowstone);ncpf.configuration.underhaul.fissionSFR.blocks.add(glowstone);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(lapis);ncpf.configuration.underhaul.fissionSFR.blocks.add(lapis);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(diamond);ncpf.configuration.underhaul.fissionSFR.blocks.add(diamond);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(helium);ncpf.configuration.underhaul.fissionSFR.blocks.add(helium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(enderium);ncpf.configuration.underhaul.fissionSFR.blocks.add(enderium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(cryotheum);ncpf.configuration.underhaul.fissionSFR.blocks.add(cryotheum);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(iron);ncpf.configuration.underhaul.fissionSFR.blocks.add(iron);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(emerald);ncpf.configuration.underhaul.fissionSFR.blocks.add(emerald);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(copper);ncpf.configuration.underhaul.fissionSFR.blocks.add(copper);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(tin);ncpf.configuration.underhaul.fissionSFR.blocks.add(tin);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(magnesium);ncpf.configuration.underhaul.fissionSFR.blocks.add(magnesium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(graphite);ncpf.configuration.underhaul.fissionSFR.blocks.add(graphite);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(beryllium);ncpf.configuration.underhaul.fissionSFR.blocks.add(beryllium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeWater);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeWater);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeRedstone);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeRedstone);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeQuartz);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeQuartz);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeGold);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeGold);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeGlowstone);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeGlowstone);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeLapis);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeLapis);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeDiamond);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeDiamond);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeHelium);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeHelium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeEnderium);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeEnderium);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeCryotheum);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeCryotheum);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeIron);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeIron);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeEmerald);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeEmerald);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeCopper);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeCopper);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeTin);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeTin);
        ncpf.configuration.underhaul.fissionSFR.allBlocks.add(activeMagnesium);ncpf.configuration.underhaul.fissionSFR.blocks.add(activeMagnesium);
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "thorium", "TBU", "TBU Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "uranium", "LEU-233", "LEU-233 Oxide", "HEU-233", "HEU-233 Oxide", "LEU-235", "LEU-235 Oxide", "HEU-235", "HEU-235 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "neptunium", "LEN-236", "LEN-236 Oxide", "HEN-236", "HEN-236 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "plutonium", "LEP-239", "LEP-239 Oxide", "HEP-239", "HEP-239 Oxide", "LEP-241", "LEP-241 Oxide", "HEP-241", "HEP-241 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "mox", "MOX-239", "MOX-241");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "americium", "LEA-242", "LEA-242 Oxide", "HEA-242", "HEA-242 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "curium", "LECm-243", "LECm-243 Oxide", "HECm-243", "HECm-243 Oxide", "LECm-245", "LECm-245 Oxide", "HECm-245", "HECm-245 Oxide", "LECm-247", "LECm-247 Oxide", "HECm-247", "HECm-247 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "berkelium", "LEB-248", "LEB-248 Oxide", "HEB-248", "HEB-248 Oxide");
        addFuels(ncpf, config, powerMult, heatMult, fuelUseMult, "californium", "LECf-249", "LECf-249 Oxide", "HECf-249", "HECf-249 Oxide", "LECf-251", "LECf-251 Oxide", "HECf-251", "HECf-251 Oxide");
        return ncpf;
    }
    private void addFuels(NCPFFile ncpf, Config config, double powerMult, double heatMult, double fuelUseMult, String baseName, String... fuelNames){
        ConfigList time = config.getConfigList("fission_"+baseName+"_fuel_time");
        ConfigList power = config.getConfigList("fission_"+baseName+"_power");
        ConfigList heat = config.getConfigList("fission_"+baseName+"_heat_generation");
        for(int i = 0; i<fuelNames.length; i++){
            Fuel fuel = Fuel.fuel("nuclearcraft:fuel_"+baseName+":"+i, fuelNames[i], (float)(power.getDouble(i)*powerMult), (float)(heat.getDouble(i)*heatMult), (int)(time.getDouble(i)/fuelUseMult), StringUtil.superReplace("underhaul/fuel/fuel_"+baseName+"_"+StringUtil.superReplace(StringUtil.toLowerCase(fuelNames[i]), " oxide", "_oxide", "-", "_", "ecm_", "ec_", "ecf_", "ec_"), "fuel_mox", "fuel_mixed_oxide"));
            ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
        }
    }
}