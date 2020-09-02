package planner.file;
import planner.JSON;
import planner.JSON.JSONArray;
import planner.JSON.JSONObject;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import planner.Core;
import simplelibrary.config2.Config;
import multiblock.configuration.Configuration;
import multiblock.configuration.PartialConfiguration;
import multiblock.configuration.overhaul.OverhaulConfiguration;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import multiblock.configuration.underhaul.UnderhaulConfiguration;
import multiblock.Multiblock;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    static{
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){//There's probably a better way of detecting the format...
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    String line;
                    while((line = reader.readLine())!=null){
                        if(line.contains("D:fission_cooling_rate"))return true;
                    }
                }catch(IOException ex){}
                return false;
            }
            String s = "";
            @Override
            public synchronized NCPFFile read(InputStream in){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    NCPFFile ncpf = new NCPFFile();
                    s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    ncpf.configuration = new Configuration(null, null, null);
                    ncpf.configuration.underhaul = new UnderhaulConfiguration();
                    ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                    boolean waterCoolerRequirements = getBoolean("fission_water_cooler_requirement");
                    double powerMult = getDouble("fission_power");
                    double fuelUseMult = getDouble("fission_fuel_use");
                    double heatMult = getDouble("fission_heat_generation");
                    ncpf.configuration.underhaul.fissionSFR.minSize = getInt("fission_min_size");
                    ncpf.configuration.underhaul.fissionSFR.maxSize = getInt("fission_max_size");
                    ncpf.configuration.underhaul.fissionSFR.neutronReach = getInt("fission_neutron_reach");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = (float) getDouble("fission_moderator_extra_power");
                    ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = (float) getDouble("fission_moderator_extra_heat");
                    ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = getInt("fission_active_cooler_max_rate");
                    int[] coolingRates = getDoublesAsInts("fission_cooling_rate");
                    multiblock.configuration.underhaul.fissionsfr.Block cell = multiblock.configuration.underhaul.fissionsfr.Block.fuelCell("Fuel Cell", "underhaul/cell");
                    multiblock.configuration.underhaul.fissionsfr.Block water = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Water Cooler", coolingRates[0], "underhaul/water", multiblock.configuration.underhaul.fissionsfr.PlacementRule.or(multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR)));
                    if(!waterCoolerRequirements){
                        water.rules.clear();
                    }
                    multiblock.configuration.underhaul.fissionsfr.Block redstone = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Redstone Cooler", coolingRates[1], "underhaul/redstone", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block quartz = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Quartz Cooler", coolingRates[2], "underhaul/quartz", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    multiblock.configuration.underhaul.fissionsfr.Block gold = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Gold Cooler", coolingRates[3], "underhaul/gold", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, redstone));
                    multiblock.configuration.underhaul.fissionsfr.Block glowstone = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Glowstone Cooler", coolingRates[4], "underhaul/glowstone", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    multiblock.configuration.underhaul.fissionsfr.Block lapis = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Lapis Cooler", coolingRates[5], "underhaul/lapis", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL),multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    multiblock.configuration.underhaul.fissionsfr.Block diamond = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Diamond Cooler", coolingRates[6], "underhaul/diamond", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, quartz));
                    multiblock.configuration.underhaul.fissionsfr.Block helium = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Helium Cooler", coolingRates[7], "underhaul/helium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.exactly(1, redstone), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    multiblock.configuration.underhaul.fissionsfr.Block enderium = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Enderium Cooler", coolingRates[8], "underhaul/enderium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.and(multiblock.configuration.underhaul.fissionsfr.PlacementRule.exactly(3, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING),multiblock.configuration.underhaul.fissionsfr.PlacementRule.vertex(multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING)));
                    multiblock.configuration.underhaul.fissionsfr.Block cryotheum = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Cryotheum Cooler", coolingRates[9], "underhaul/cryotheum", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block iron = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Iron Cooler", coolingRates[10], "underhaul/iron", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, gold));
                    multiblock.configuration.underhaul.fissionsfr.Block emerald = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Emerald Cooler", coolingRates[11], "underhaul/emerald", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block copper = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Copper Cooler", coolingRates[12], "underhaul/copper", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, glowstone));
                    multiblock.configuration.underhaul.fissionsfr.Block tin = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Tin Cooler", coolingRates[13], "underhaul/tin", multiblock.configuration.underhaul.fissionsfr.PlacementRule.axis(lapis));
                    multiblock.configuration.underhaul.fissionsfr.Block magnesium = multiblock.configuration.underhaul.fissionsfr.Block.cooler("Magnesium Cooler", coolingRates[14], "underhaul/magnesium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    multiblock.configuration.underhaul.fissionsfr.Block graphite = multiblock.configuration.underhaul.fissionsfr.Block.moderator("Graphite", "underhaul/graphite");
                    multiblock.configuration.underhaul.fissionsfr.Block beryllium = multiblock.configuration.underhaul.fissionsfr.Block.moderator("Beryllium", "underhaul/beryllium");
                    int[] activeCoolingRates = getDoublesAsInts("fission_active_cooling_rate");
                    multiblock.configuration.underhaul.fissionsfr.Block activeWater = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Water Cooler", activeCoolingRates[0], "Water", "underhaul/water", multiblock.configuration.underhaul.fissionsfr.PlacementRule.or(multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR)));
                    multiblock.configuration.underhaul.fissionsfr.Block activeRedstone = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Redstone Cooler", activeCoolingRates[1], "Destabilized Redstone", "underhaul/redstone", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block activeQuartz = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Quartz Cooler", activeCoolingRates[2], "Molten Quartz", "underhaul/quartz", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    multiblock.configuration.underhaul.fissionsfr.Block activeGold = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Gold Cooler", activeCoolingRates[3], "Molten Gold", "underhaul/gold", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, redstone));
                    multiblock.configuration.underhaul.fissionsfr.Block activeGlowstone = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Glowstone Cooler", activeCoolingRates[4], "Energized Glowstone", "underhaul/glowstone", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
                    multiblock.configuration.underhaul.fissionsfr.Block activeLapis = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Lapis Cooler", activeCoolingRates[5], "Molten Lapis", "underhaul/lapis", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL),multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    multiblock.configuration.underhaul.fissionsfr.Block activeDiamond = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Diamond Cooler", activeCoolingRates[6], "Molten Diamond", "underhaul/diamond", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, water), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, quartz));
                    multiblock.configuration.underhaul.fissionsfr.Block activeHelium = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Helium Cooler", activeCoolingRates[7], "Liquid Helium", "underhaul/helium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.exactly(1, redstone), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING));
                    multiblock.configuration.underhaul.fissionsfr.Block activeEnderium = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Enderium Cooler", activeCoolingRates[8], "Resonant Ender", "underhaul/enderium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.and(multiblock.configuration.underhaul.fissionsfr.PlacementRule.exactly(3, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING),multiblock.configuration.underhaul.fissionsfr.PlacementRule.vertex(multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING)));
                    multiblock.configuration.underhaul.fissionsfr.Block activeCryotheum = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Cryotheum Cooler", activeCoolingRates[9], "Gelid Cryotheum", "underhaul/cryotheum", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(2, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block activeIron = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Iron Cooler", activeCoolingRates[10], "Molten Iron", "underhaul/iron", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, gold));
                    multiblock.configuration.underhaul.fissionsfr.Block activeEmerald = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Emerald Cooler", activeCoolingRates[11], "Molten Emerald", "underhaul/emerald", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL));
                    multiblock.configuration.underhaul.fissionsfr.Block activeCopper = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Copper Cooler", activeCoolingRates[12], "Molten Copper", "underhaul/copper", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, glowstone));
                    multiblock.configuration.underhaul.fissionsfr.Block activeTin = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Tin Cooler", activeCoolingRates[13], "Molten Tin", "underhaul/tin", multiblock.configuration.underhaul.fissionsfr.PlacementRule.axis(lapis));
                    multiblock.configuration.underhaul.fissionsfr.Block activeMagnesium = multiblock.configuration.underhaul.fissionsfr.Block.activeCooler("Active Magnesium Cooler", activeCoolingRates[14], "Molten Magnesium", "underhaul/magnesium", multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING), multiblock.configuration.underhaul.fissionsfr.PlacementRule.atLeast(1, multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR));
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
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "thorium", "TBU", "TBU Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "uranium", "LEU-233", "LEU-233 Oxide", "HEU-233", "HEU-233 Oxide", "LEU-235", "LEU-235 Oxide", "HEU-235", "HEU-235 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "neptunium", "LEN-236", "LEN-236 Oxide", "HEN-236", "HEN-236 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "plutonium", "LEP-239", "LEP-239 Oxide", "HEP-239", "HEP-239 Oxide", "LEP-241", "LEP-241 Oxide", "HEP-241", "HEP-241 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "mox", "MOX-239", "MOX-241");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "americium", "LEA-242", "LEA-242 Oxide", "HEA-242", "HEA-242 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "curium", "LECm-243", "LECm-243 Oxide", "HECm-243", "HECm-243 Oxide", "LECm-245", "LECm-245 Oxide", "HECm-245", "HECm-245 Oxide", "LECm-247", "LECm-247 Oxide", "HECm-247", "HECm-247 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "berkelium", "LEB-248", "LEB-248 Oxide", "HEB-248", "HEB-248 Oxide");
                    addFuels(ncpf, powerMult, heatMult, fuelUseMult, "californium", "LECf-249", "LECf-249 Oxide", "HECf-249", "HECf-249 Oxide", "LECf-251", "LECf-251 Oxide", "HECf-251", "HECf-251 Oxide");
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private void addFuels(NCPFFile ncpf, double powerMult, double heatMult, double fuelUseMult, String baseName, String... fuelNames){
                double[] time = getDoubles("fission_"+baseName+"_fuel_time");
                double[] power = getDoubles("fission_"+baseName+"_power");
                double[] heat = getDoubles("fission_"+baseName+"_heat_generation");
                for(int i = 0; i<fuelNames.length; i++){
                    multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelNames[i], (float)(power[i]*powerMult), (float)(heat[i]*heatMult), (int)(time[i]/fuelUseMult));
                    ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                }
            }
            private double getDouble(String name){
                String str = s.substring(s.indexOf("D:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Double.parseDouble(str);
            }
            private int getInt(String name){
                String str = s.substring(s.indexOf("I:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Integer.parseInt(str);
            }
            private boolean getBoolean(String name){
                String str = s.substring(s.indexOf("B:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Boolean.parseBoolean(str);
            }
            private double[] getDoubles(String name){
                ArrayList<Double> doubles = new ArrayList<>();
                String str = s.substring(s.indexOf("D:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    doubles.add(Double.parseDouble(st.trim()));
                }
                double[] ret = new double[doubles.size()];
                for(int i = 0; i<doubles.size(); i++){
                    ret[i] = doubles.get(i);
                }
                return ret;
            }
            private int[] getDoublesAsInts(String name){
                double[] ds = getDoubles(name);
                int[] is = new int[ds.length];
                for(int i = 0; i<is.length; i++){
                    is[i] = (int)ds[i];
                }
                return is;
            }
            private int[] getInts(String name){
                ArrayList<Integer> ints = new ArrayList<>();
                String str = s.substring(s.indexOf("I:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    ints.add(Integer.parseInt(st.trim()));
                }
                int[] ret = new int[ints.size()];
                for(int i = 0; i<ints.size(); i++){
                    ret[i] = ints.get(i);
                }
                return ret;
            }
            private boolean[] getBooleans(String name){
                ArrayList<Boolean> booleans = new ArrayList<>();
                String str = s.substring(s.indexOf("B:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    booleans.add(Boolean.parseBoolean(st.trim()));
                }
                boolean[] ret = new boolean[booleans.size()];
                for(int i = 0; i<booleans.size(); i++){
                    ret[i] = booleans.get(i);
                }
                return ret;
            }
        });// UNDERHAUL nuclearcraft.cfg
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){//There's probably a better way of detecting the format...
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    String line;
                    while((line = reader.readLine())!=null){
                        if(line.contains("I:fission_sink_cooling_rate"))return true;
                    }
                }catch(IOException ex){}
                return false;
            }
            String s = "";
            @Override
            public synchronized NCPFFile read(InputStream in){
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(in))){
                    NCPFFile ncpf = new NCPFFile();
                    s = "";
                    String line;
                    while((line = reader.readLine())!=null)s+=line+"\n";
                    ncpf.configuration = new Configuration(null, null, null);
                    ncpf.configuration.overhaul = new OverhaulConfiguration();
                    //<editor-fold defaultstate="collapsed" desc="Fission SFR">
                    ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                    ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = getInt("fission_cooling_efficiency_leniency");
                    ncpf.configuration.overhaul.fissionSFR.minSize = getInt("fission_min_size");
                    ncpf.configuration.overhaul.fissionSFR.maxSize = getInt("fission_max_size");
                    ncpf.configuration.overhaul.fissionSFR.neutronReach = getInt("fission_neutron_reach");
                    double fuelTimeMult = getDouble("fission_fuel_time_multiplier");
                    double[] sparsity = getDoubles("fission_sparsity_penalty_params");
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = (float) sparsity[0];
                    ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = (float) sparsity[1];
                    double[] sourceEfficiency = getDoubles("fission_source_efficiency");
                    multiblock.configuration.overhaul.fissionsfr.Source rabe = new multiblock.configuration.overhaul.fissionsfr.Source("Ra-Be", (float) sourceEfficiency[0]);
                    multiblock.configuration.overhaul.fissionsfr.Source pobe = new multiblock.configuration.overhaul.fissionsfr.Source("Po-Be", (float) sourceEfficiency[1]);
                    multiblock.configuration.overhaul.fissionsfr.Source cf252 = new multiblock.configuration.overhaul.fissionsfr.Source("Cf-252", (float) sourceEfficiency[2]);
                    ncpf.configuration.overhaul.fissionSFR.allSources.add(rabe);ncpf.configuration.overhaul.fissionSFR.sources.add(rabe);
                    ncpf.configuration.overhaul.fissionSFR.allSources.add(pobe);ncpf.configuration.overhaul.fissionSFR.sources.add(pobe);
                    ncpf.configuration.overhaul.fissionSFR.allSources.add(cf252);ncpf.configuration.overhaul.fissionSFR.sources.add(cf252);
                    int[] coolingRates = getInts("fission_sink_cooling_rate");
                    String[] rules = getStrings("fission_sink_rule");
                    multiblock.configuration.overhaul.fissionsfr.Block water = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Water Heat Sink", coolingRates[0], "overhaul/water");
                    multiblock.configuration.overhaul.fissionsfr.Block iron = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Iron Heat Sink", coolingRates[1], "overhaul/iron");
                    multiblock.configuration.overhaul.fissionsfr.Block redstone = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Redstone Heat Sink", coolingRates[2], "overhaul/redstone");
                    multiblock.configuration.overhaul.fissionsfr.Block quartz = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Quartz Heat Sink", coolingRates[3], "overhaul/quartz");
                    multiblock.configuration.overhaul.fissionsfr.Block obsidian = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Obsidian Heat Sink", coolingRates[4], "overhaul/obsidian");
                    multiblock.configuration.overhaul.fissionsfr.Block netherBrick = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Nether Brick Heat Sink", coolingRates[5], "overhaul/nether brick");
                    multiblock.configuration.overhaul.fissionsfr.Block glowstone = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Glowstone Heat Sink", coolingRates[6], "overhaul/glowstone");
                    multiblock.configuration.overhaul.fissionsfr.Block lapis = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Lapis Heat Sink", coolingRates[7], "overhaul/lapis");
                    multiblock.configuration.overhaul.fissionsfr.Block gold = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Gold Heat Sink", coolingRates[8], "overhaul/gold");
                    multiblock.configuration.overhaul.fissionsfr.Block prismarine = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Prismarine Heat Sink", coolingRates[9], "overhaul/prismarine");
                    multiblock.configuration.overhaul.fissionsfr.Block slime = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Slime Heat Sink", coolingRates[10], "overhaul/slime");
                    multiblock.configuration.overhaul.fissionsfr.Block endStone = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("End Stone Heat Sink", coolingRates[11], "overhaul/end stone");
                    multiblock.configuration.overhaul.fissionsfr.Block purpur = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Purpur Heat Sink", coolingRates[12], "overhaul/purpur");
                    multiblock.configuration.overhaul.fissionsfr.Block diamond = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Diamond Heat Sink", coolingRates[13], "overhaul/diamond");
                    multiblock.configuration.overhaul.fissionsfr.Block emerald = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Emerald Heat Sink", coolingRates[14], "overhaul/emerald");
                    multiblock.configuration.overhaul.fissionsfr.Block copper = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Copper Heat Sink", coolingRates[15], "overhaul/copper");
                    multiblock.configuration.overhaul.fissionsfr.Block tin = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Tin Heat Sink", coolingRates[16], "overhaul/tin");
                    multiblock.configuration.overhaul.fissionsfr.Block lead = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Lead Heat Sink", coolingRates[17], "overhaul/lead");
                    multiblock.configuration.overhaul.fissionsfr.Block boron = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Boron Heat Sink", coolingRates[18], "overhaul/boron");
                    multiblock.configuration.overhaul.fissionsfr.Block lithium = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Lithium Heat Sink", coolingRates[19], "overhaul/lithium");
                    multiblock.configuration.overhaul.fissionsfr.Block magnesium = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Magnesium Heat Sink", coolingRates[20], "overhaul/magnesium");
                    multiblock.configuration.overhaul.fissionsfr.Block manganese = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Manganese Heat Sink", coolingRates[21], "overhaul/manganese");
                    multiblock.configuration.overhaul.fissionsfr.Block aluminum = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Aluminum Heat Sink", coolingRates[22], "overhaul/aluminum");
                    multiblock.configuration.overhaul.fissionsfr.Block silver = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Silver Heat Sink", coolingRates[23], "overhaul/silver");
                    multiblock.configuration.overhaul.fissionsfr.Block fluorite = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Fluorite Heat Sink", coolingRates[24], "overhaul/fluorite");
                    multiblock.configuration.overhaul.fissionsfr.Block villiaumite = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Villiaumite Heat Sink", coolingRates[25], "overhaul/villiaumite");
                    multiblock.configuration.overhaul.fissionsfr.Block carobbiite = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Carobbiite Heat Sink", coolingRates[26], "overhaul/carobbiite");
                    multiblock.configuration.overhaul.fissionsfr.Block arsenic = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Arsenic Heat Sink", coolingRates[27], "overhaul/arsenic");
                    multiblock.configuration.overhaul.fissionsfr.Block nitrogen = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Liquid Nitrogen Heat Sink", coolingRates[28], "overhaul/nitrogen");
                    multiblock.configuration.overhaul.fissionsfr.Block helium = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Liquid Helium Heat Sink", coolingRates[29], "overhaul/helium");
                    multiblock.configuration.overhaul.fissionsfr.Block enderium = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Enderium Heat Sink", coolingRates[30], "overhaul/enderium");
                    multiblock.configuration.overhaul.fissionsfr.Block cryotheum = multiblock.configuration.overhaul.fissionsfr.Block.heatsink("Cryotheum Heat Sink", coolingRates[31], "overhaul/cryotheum");
                    multiblock.configuration.overhaul.fissionsfr.Block cell = multiblock.configuration.overhaul.fissionsfr.Block.cell("Fuel Cell", "overhaul/cell");
                    multiblock.configuration.overhaul.fissionsfr.Block irradiator = multiblock.configuration.overhaul.fissionsfr.Block.irradiator("Neutron Irradiator", "overhaul/irradiator");
                    multiblock.configuration.overhaul.fissionsfr.Block conductor = multiblock.configuration.overhaul.fissionsfr.Block.conductor("Conductor", "overhaul/conductor");
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(cell);ncpf.configuration.overhaul.fissionSFR.blocks.add(cell);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(irradiator);ncpf.configuration.overhaul.fissionSFR.blocks.add(irradiator);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(conductor);ncpf.configuration.overhaul.fissionSFR.blocks.add(conductor);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(water);ncpf.configuration.overhaul.fissionSFR.blocks.add(water);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(iron);ncpf.configuration.overhaul.fissionSFR.blocks.add(iron);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(redstone);ncpf.configuration.overhaul.fissionSFR.blocks.add(redstone);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(quartz);ncpf.configuration.overhaul.fissionSFR.blocks.add(quartz);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(obsidian);ncpf.configuration.overhaul.fissionSFR.blocks.add(obsidian);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(netherBrick);ncpf.configuration.overhaul.fissionSFR.blocks.add(netherBrick);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(glowstone);ncpf.configuration.overhaul.fissionSFR.blocks.add(glowstone);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(lapis);ncpf.configuration.overhaul.fissionSFR.blocks.add(lapis);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(gold);ncpf.configuration.overhaul.fissionSFR.blocks.add(gold);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(prismarine);ncpf.configuration.overhaul.fissionSFR.blocks.add(prismarine);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(slime);ncpf.configuration.overhaul.fissionSFR.blocks.add(slime);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(endStone);ncpf.configuration.overhaul.fissionSFR.blocks.add(endStone);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(purpur);ncpf.configuration.overhaul.fissionSFR.blocks.add(purpur);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(diamond);ncpf.configuration.overhaul.fissionSFR.blocks.add(diamond);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(emerald);ncpf.configuration.overhaul.fissionSFR.blocks.add(emerald);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(copper);ncpf.configuration.overhaul.fissionSFR.blocks.add(copper);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(tin);ncpf.configuration.overhaul.fissionSFR.blocks.add(tin);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(lead);ncpf.configuration.overhaul.fissionSFR.blocks.add(lead);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(boron);ncpf.configuration.overhaul.fissionSFR.blocks.add(boron);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(lithium);ncpf.configuration.overhaul.fissionSFR.blocks.add(lithium);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(magnesium);ncpf.configuration.overhaul.fissionSFR.blocks.add(magnesium);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(manganese);ncpf.configuration.overhaul.fissionSFR.blocks.add(manganese);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(aluminum);ncpf.configuration.overhaul.fissionSFR.blocks.add(aluminum);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(silver);ncpf.configuration.overhaul.fissionSFR.blocks.add(silver);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(fluorite);ncpf.configuration.overhaul.fissionSFR.blocks.add(fluorite);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(villiaumite);ncpf.configuration.overhaul.fissionSFR.blocks.add(villiaumite);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(carobbiite);ncpf.configuration.overhaul.fissionSFR.blocks.add(carobbiite);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(arsenic);ncpf.configuration.overhaul.fissionSFR.blocks.add(arsenic);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(nitrogen);ncpf.configuration.overhaul.fissionSFR.blocks.add(nitrogen);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(helium);ncpf.configuration.overhaul.fissionSFR.blocks.add(helium);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(enderium);ncpf.configuration.overhaul.fissionSFR.blocks.add(enderium);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(cryotheum);ncpf.configuration.overhaul.fissionSFR.blocks.add(cryotheum);
                    water.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[0]));
                    iron.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[1]));
                    redstone.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[2]));
                    quartz.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[3]));
                    obsidian.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[4]));
                    netherBrick.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[5]));
                    glowstone.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[6]));
                    lapis.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[7]));
                    gold.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[8]));
                    prismarine.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[9]));
                    slime.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[10]));
                    endStone.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[11]));
                    purpur.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[12]));
                    diamond.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[13]));
                    emerald.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[14]));
                    copper.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[15]));
                    tin.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[16]));
                    lead.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[17]));
                    boron.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[18]));
                    lithium.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[19]));
                    magnesium.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[20]));
                    manganese.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[21]));
                    aluminum.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[22]));
                    silver.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[23]));
                    fluorite.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[24]));
                    villiaumite.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[25]));
                    carobbiite.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[26]));
                    arsenic.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[27]));
                    nitrogen.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[28]));
                    helium.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[29]));
                    enderium.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[30]));
                    cryotheum.rules.add(multiblock.configuration.overhaul.fissionsfr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionSFR, rules[31]));
                    int[] fluxFac = getInts("fission_moderator_flux_factor");
                    double[] modEff = getDoubles("fission_moderator_efficiency");
                    multiblock.configuration.overhaul.fissionsfr.Block graphite = multiblock.configuration.overhaul.fissionsfr.Block.moderator("Graphite Moderator", "overhaul/graphite", fluxFac[0], (float) modEff[0]);
                    multiblock.configuration.overhaul.fissionsfr.Block beryllium = multiblock.configuration.overhaul.fissionsfr.Block.moderator("Beryllium Moderator", "overhaul/beryllium", fluxFac[1], (float) modEff[1]);
                    multiblock.configuration.overhaul.fissionsfr.Block heavyWater = multiblock.configuration.overhaul.fissionsfr.Block.moderator("Heavy Water Moderator", "overhaul/heavy water", fluxFac[2], (float) modEff[2]);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(graphite);ncpf.configuration.overhaul.fissionSFR.blocks.add(graphite);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(beryllium);ncpf.configuration.overhaul.fissionSFR.blocks.add(beryllium);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(heavyWater);ncpf.configuration.overhaul.fissionSFR.blocks.add(heavyWater);
                    double[] refEff = getDoubles("fission_reflector_efficiency");
                    double[] refRef = getDoubles("fission_reflector_reflectivity");
                    multiblock.configuration.overhaul.fissionsfr.Block bec = multiblock.configuration.overhaul.fissionsfr.Block.reflector("Beryllium-Carbon Reflector", "overhaul/beryllium-carbon", (float) refEff[0], (float) refRef[0]);
                    multiblock.configuration.overhaul.fissionsfr.Block pbs = multiblock.configuration.overhaul.fissionsfr.Block.reflector("Lead-Steel Reflector", "overhaul/lead-steel", (float) refEff[1], (float) refRef[1]);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(bec);ncpf.configuration.overhaul.fissionSFR.blocks.add(bec);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(pbs);ncpf.configuration.overhaul.fissionSFR.blocks.add(pbs);
                    double[] shieldHeat = getDoubles("fission_shield_heat_per_flux");
                    double[] shieldEff = getDoubles("fission_shield_efficiency");
                    multiblock.configuration.overhaul.fissionsfr.Block bag = multiblock.configuration.overhaul.fissionsfr.Block.shield("Boron-Silver Neutron Shield", "overhaul/boron-silver", "overhaul/boron-silver_closed", (int) shieldHeat[0], (float) shieldEff[0]);
                    ncpf.configuration.overhaul.fissionSFR.allBlocks.add(bag);ncpf.configuration.overhaul.fissionSFR.blocks.add(bag);
                    double[] irrHeat = getDoubles("fission_irradiator_heat_per_flux");
                    double[] irrEff = getDoubles("fission_irradiator_efficiency");
                    multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrec1 = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe("Thorium to Protactinium-Enriched Thorium", (float)irrEff[0], (float)irrHeat[0]);
                    multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrec2 = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe("Protactinium-Enriched Thorium to Protactinium-233", (float)irrEff[1], (float)irrHeat[1]);
                    multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrec3 = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe("Bismuth Dust to Polonium Dust", (float)irrEff[2], (float)irrHeat[2]);
                    multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolant1 = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe("Water to High Pressure Steam", "Water", "High Pressure Steam", 64, 4);
                    multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolant2 = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe("Preheated Water to High Pressure Steam", "Preheated Water", "High Pressure Steam", 32, 4);
                    multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolant3 = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe("IC2 Coolant to Hot IC2 Coolant", "IC2 Coolant", "Hot IC2 Coolant", 160, 1);
                    ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrec1);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrec1);
                    ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrec2);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrec2);
                    ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrec3);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrec3);
                    ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolant1);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolant1);
                    ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolant2);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolant2);
                    ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolant3);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolant3);
                    addSFRFuels(ncpf, fuelTimeMult, "thorium", null, "TBU Oxide", "TBU Nitride", "TBU-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "uranium", null, "LEU-233 Oxide", "LEU-233 Nitride", "LEU-233-Zirconium Alloy", null, null, "HEU-233 Oxide", "HEU-233 Nitride", "HEU-233-Zirconium Alloy", null, null, "LEU-235 Oxide", "LEU-235 Nitride", "LEU-235-Zirconium Alloy", null, null, "HEU-235 Oxide", "HEU-235 Nitride", "HEU-235-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "neptunium", null, "LEN-236 Oxide", "LEN-236 Nitride", "LEN-236-Zirconium Alloy", null, null, "HEN-236 Oxide", "HEN-236 Nitride", "HEN-236-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "plutonium", null, "LEP-239 Oxide", "LEP-239 Nitride", "LEP-239-Zirconium Alloy", null, null, "HEP-239 Oxide", "HEP-239 Nitride", "HEP-239-Zirconium Alloy", null, null, "LEP-241 Oxide", "LEP-241 Nitride", "LEP-241-Zirconium Alloy", null, null, "HEP-241 Oxide", "HEP-241 Nitride", "HEP-241-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "mixed", null, "MOX-239", "MNI-239", "MZA-239", null, null, "MOX-241", "MNI-241", "MZA-241", null);
                    addSFRFuels(ncpf, fuelTimeMult, "americium", null, "LEA-242 Oxide", "LEA-242 Nitride", "LEA-242-Zirconium Alloy", null, null, "HEA-242 Oxide", "HEA-242 Nitride", "HEA-242-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "curium", null, "LECm-243 Oxide", "LECm-243 Nitride", "LECm-243-Zirconium Alloy", null, null, "HECm-243 Oxide", "HECm-243 Nitride", "HECm-243-Zirconium Alloy", null, null, "LECm-245 Oxide", "LECm-245 Nitride", "LECm-245-Zirconium Alloy", null, null, "HECm-245 Oxide", "HECm-245 Nitride", "HECm-245-Zirconium Alloy", null, null, "LECm-247 Oxide", "LECm-247 Nitride", "LECm-247-Zirconium Alloy", null, null, "HECm-247 Oxide", "HECm-247 Nitride", "HECm-247-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "berkelium", null, "LEB-248 Oxide", "LEB-248 Nitride", "LEB-248-Zirconium Alloy", null, null, "HEB-248 Oxide", "HEB-248 Nitride", "HEB-248-Zirconium Alloy", null);
                    addSFRFuels(ncpf, fuelTimeMult, "californium", null, "LECf-249 Oxide", "LECf-249 Nitride", "LECf-249-Zirconium Alloy", null, null, "HECf-249 Oxide", "HECf-249 Nitride", "HECf-249-Zirconium Alloy", null, null, "LECf-251 Oxide", "LECf-251 Nitride", "LECf-251-Zirconium Alloy", null, null, "HECf-251 Oxide", "HECf-251 Nitride", "HECf-251-Zirconium Alloy", null);
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Fission MSR">
                    ncpf.configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                    ncpf.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = getInt("fission_cooling_efficiency_leniency");
                    ncpf.configuration.overhaul.fissionMSR.minSize = getInt("fission_min_size");
                    ncpf.configuration.overhaul.fissionMSR.maxSize = getInt("fission_max_size");
                    ncpf.configuration.overhaul.fissionMSR.neutronReach = getInt("fission_neutron_reach");
                    ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyMult = (float) sparsity[0];
                    ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = (float) sparsity[1];
                    multiblock.configuration.overhaul.fissionmsr.Source mrabe = new multiblock.configuration.overhaul.fissionmsr.Source("Ra-Be", (float) sourceEfficiency[0]);
                    multiblock.configuration.overhaul.fissionmsr.Source mpobe = new multiblock.configuration.overhaul.fissionmsr.Source("Po-Be", (float) sourceEfficiency[1]);
                    multiblock.configuration.overhaul.fissionmsr.Source mcf252 = new multiblock.configuration.overhaul.fissionmsr.Source("Cf-252", (float) sourceEfficiency[2]);
                    ncpf.configuration.overhaul.fissionMSR.sources.add(mrabe);
                    ncpf.configuration.overhaul.fissionMSR.sources.add(mpobe);
                    ncpf.configuration.overhaul.fissionMSR.sources.add(mcf252);
                    coolingRates = getInts("fission_heater_cooling_rate");
                    rules = getStrings("fission_heater_rule");
                    multiblock.configuration.overhaul.fissionmsr.Block mstandard = multiblock.configuration.overhaul.fissionmsr.Block.heater("Standard Coolant Heater", coolingRates[0], "Eutectic NaK Alloy", "overhaul/msr/standard");
                    multiblock.configuration.overhaul.fissionmsr.Block miron = multiblock.configuration.overhaul.fissionmsr.Block.heater("Iron Coolant Heater", coolingRates[1], "Eutectic NaK-Iron Mixture", "overhaul/msr/iron");
                    multiblock.configuration.overhaul.fissionmsr.Block mredstone = multiblock.configuration.overhaul.fissionmsr.Block.heater("Redstone Coolant Heater", coolingRates[2], "Eutectic NaK-Redstone Mixture", "overhaul/msr/redstone");
                    multiblock.configuration.overhaul.fissionmsr.Block mquartz = multiblock.configuration.overhaul.fissionmsr.Block.heater("Quartz Coolant Heater", coolingRates[3], "Eutectic NaK-Quartz Mixture", "overhaul/msr/quartz");
                    multiblock.configuration.overhaul.fissionmsr.Block mobsidian = multiblock.configuration.overhaul.fissionmsr.Block.heater("Obsidian Coolant Heater", coolingRates[4], "Eutectic NaK-Obsidian Mixture", "overhaul/msr/obsidian");
                    multiblock.configuration.overhaul.fissionmsr.Block mnetherBrick = multiblock.configuration.overhaul.fissionmsr.Block.heater("Nether Brick Coolant Heater", coolingRates[5], "Eutectic NaK-Nether Brick Mixture", "overhaul/msr/nether brick");
                    multiblock.configuration.overhaul.fissionmsr.Block mglowstone = multiblock.configuration.overhaul.fissionmsr.Block.heater("Glowstone Coolant Heater", coolingRates[6], "Eutectic NaK-Glowstone Mixture", "overhaul/msr/glowstone");
                    multiblock.configuration.overhaul.fissionmsr.Block mlapis = multiblock.configuration.overhaul.fissionmsr.Block.heater("Lapis Coolant Heater", coolingRates[7], "Eutectic NaK-Lapis Mixture", "overhaul/msr/lapis");
                    multiblock.configuration.overhaul.fissionmsr.Block mgold = multiblock.configuration.overhaul.fissionmsr.Block.heater("Gold Coolant Heater", coolingRates[8], "Eutectic NaK-Gold Mixture", "overhaul/msr/gold");
                    multiblock.configuration.overhaul.fissionmsr.Block mprismarine = multiblock.configuration.overhaul.fissionmsr.Block.heater("Prismarine Coolant Heater", coolingRates[9], "Eutectic NaK-Prismarine Mixture", "overhaul/msr/prismarine");
                    multiblock.configuration.overhaul.fissionmsr.Block mslime = multiblock.configuration.overhaul.fissionmsr.Block.heater("Slime Coolant Heater", coolingRates[10], "Eutectic NaK-Slime Mixture", "overhaul/msr/slime");
                    multiblock.configuration.overhaul.fissionmsr.Block mendStone = multiblock.configuration.overhaul.fissionmsr.Block.heater("End Stone Coolant Heater", coolingRates[11], "Eutectic NaK-End Stone Mixture", "overhaul/msr/end stone");
                    multiblock.configuration.overhaul.fissionmsr.Block mPurpur = multiblock.configuration.overhaul.fissionmsr.Block.heater("Purpur Coolant Heater", coolingRates[12], "Eutectic NaK-Purpur Mixture", "overhaul/msr/purpur");
                    multiblock.configuration.overhaul.fissionmsr.Block mDiamond = multiblock.configuration.overhaul.fissionmsr.Block.heater("Diamond Coolant Heater", coolingRates[13], "Eutectic NaK-Diamond Mixture", "overhaul/msr/diamond");
                    multiblock.configuration.overhaul.fissionmsr.Block mEmerald = multiblock.configuration.overhaul.fissionmsr.Block.heater("Emerald Coolant Heater", coolingRates[14], "Eutectic NaK-Emerald Mixture", "overhaul/msr/emerald");
                    multiblock.configuration.overhaul.fissionmsr.Block mCopper = multiblock.configuration.overhaul.fissionmsr.Block.heater("Copper Coolant Heater", coolingRates[15], "Eutectic NaK-Copper Mixture", "overhaul/msr/copper");
                    multiblock.configuration.overhaul.fissionmsr.Block mTin = multiblock.configuration.overhaul.fissionmsr.Block.heater("Tin Coolant Heater", coolingRates[16], "Eutectic NaK-Tin Mixture", "overhaul/msr/tin");
                    multiblock.configuration.overhaul.fissionmsr.Block mLead = multiblock.configuration.overhaul.fissionmsr.Block.heater("Lead Coolant Heater", coolingRates[17], "Eutectic NaK-Lead Mixture", "overhaul/msr/lead");
                    multiblock.configuration.overhaul.fissionmsr.Block mBoron = multiblock.configuration.overhaul.fissionmsr.Block.heater("Boron Coolant Heater", coolingRates[18], "Eutectic NaK-Boron Mixture", "overhaul/msr/boron");
                    multiblock.configuration.overhaul.fissionmsr.Block mLithium = multiblock.configuration.overhaul.fissionmsr.Block.heater("Lithium Coolant Heater", coolingRates[19], "Eutectic NaK-Lithium Mixture", "overhaul/msr/lithium");
                    multiblock.configuration.overhaul.fissionmsr.Block mMagnesium = multiblock.configuration.overhaul.fissionmsr.Block.heater("Magnesium Coolant Heater", coolingRates[20], "Eutectic NaK-Magnesium Mixture", "overhaul/msr/magnesium");
                    multiblock.configuration.overhaul.fissionmsr.Block mManganese = multiblock.configuration.overhaul.fissionmsr.Block.heater("Manganese Coolant Heater", coolingRates[21], "Eutectic NaK-Manganese Mixture", "overhaul/msr/manganese");
                    multiblock.configuration.overhaul.fissionmsr.Block mAluminum = multiblock.configuration.overhaul.fissionmsr.Block.heater("Aluminum Coolant Heater", coolingRates[22], "Eutectic NaK-Aluminum Mixture", "overhaul/msr/aluminum");
                    multiblock.configuration.overhaul.fissionmsr.Block mSilver = multiblock.configuration.overhaul.fissionmsr.Block.heater("Silver Coolant Heater", coolingRates[23], "Eutectic NaK-Silver Mixture", "overhaul/msr/silver");
                    multiblock.configuration.overhaul.fissionmsr.Block mFluorite = multiblock.configuration.overhaul.fissionmsr.Block.heater("Fluorite Coolant Heater", coolingRates[24], "Eutectic NaK-Fluorite Mixture", "overhaul/msr/fluorite");
                    multiblock.configuration.overhaul.fissionmsr.Block mVilliaumite = multiblock.configuration.overhaul.fissionmsr.Block.heater("Villiaumite Coolant Heater", coolingRates[25], "Eutectic NaK-Villiaumite Mixture", "overhaul/msr/villiaumite");
                    multiblock.configuration.overhaul.fissionmsr.Block mCarobbiite = multiblock.configuration.overhaul.fissionmsr.Block.heater("Carobbiite Coolant Heater", coolingRates[26], "Eutectic NaK-Carobbiite Mixture", "overhaul/msr/carobbiite");
                    multiblock.configuration.overhaul.fissionmsr.Block mArsenic = multiblock.configuration.overhaul.fissionmsr.Block.heater("Arsenic Coolant Heater", coolingRates[27], "Eutectic NaK-Arsenic Mixture", "overhaul/msr/arsenic");
                    multiblock.configuration.overhaul.fissionmsr.Block mNitrogen = multiblock.configuration.overhaul.fissionmsr.Block.heater("Liquid Nitrogen Coolant Heater", coolingRates[28], "Eutectic NaK-Nitrogen Mixture", "overhaul/msr/nitrogen");
                    multiblock.configuration.overhaul.fissionmsr.Block mHelium = multiblock.configuration.overhaul.fissionmsr.Block.heater("Liquid Helium Coolant Heater", coolingRates[29], "Eutectic NaK-Helium Mixture", "overhaul/msr/helium");
                    multiblock.configuration.overhaul.fissionmsr.Block mEnderium = multiblock.configuration.overhaul.fissionmsr.Block.heater("Enderium Coolant Heater", coolingRates[30], "Eutectic NaK-Enderium Mixture", "overhaul/msr/enderium");
                    multiblock.configuration.overhaul.fissionmsr.Block mCryotheum = multiblock.configuration.overhaul.fissionmsr.Block.heater("Cryotheum Coolant Heater", coolingRates[31], "Eutectic NaK-Cryotheum Mixture", "overhaul/msr/cryotheum");
                    multiblock.configuration.overhaul.fissionmsr.Block vessel = multiblock.configuration.overhaul.fissionmsr.Block.vessel("Fuel Vessel", "overhaul/msr/vessel");
                    multiblock.configuration.overhaul.fissionmsr.Block mirradiator = multiblock.configuration.overhaul.fissionmsr.Block.irradiator("Neutron Irradiator", "overhaul/irradiator");
                    multiblock.configuration.overhaul.fissionmsr.Block mconductor = multiblock.configuration.overhaul.fissionmsr.Block.conductor("Conductor", "overhaul/conductor");
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(vessel);ncpf.configuration.overhaul.fissionMSR.blocks.add(vessel);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mirradiator);ncpf.configuration.overhaul.fissionMSR.blocks.add(mirradiator);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mconductor);ncpf.configuration.overhaul.fissionMSR.blocks.add(mconductor);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mstandard);ncpf.configuration.overhaul.fissionMSR.blocks.add(mstandard);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(miron);ncpf.configuration.overhaul.fissionMSR.blocks.add(miron);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mredstone);ncpf.configuration.overhaul.fissionMSR.blocks.add(mredstone);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mquartz);ncpf.configuration.overhaul.fissionMSR.blocks.add(mquartz);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mobsidian);ncpf.configuration.overhaul.fissionMSR.blocks.add(mobsidian);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mnetherBrick);ncpf.configuration.overhaul.fissionMSR.blocks.add(mnetherBrick);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mglowstone);ncpf.configuration.overhaul.fissionMSR.blocks.add(mglowstone);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mlapis);ncpf.configuration.overhaul.fissionMSR.blocks.add(mlapis);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mgold);ncpf.configuration.overhaul.fissionMSR.blocks.add(mgold);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mprismarine);ncpf.configuration.overhaul.fissionMSR.blocks.add(mprismarine);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mslime);ncpf.configuration.overhaul.fissionMSR.blocks.add(mslime);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mendStone);ncpf.configuration.overhaul.fissionMSR.blocks.add(mendStone);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mPurpur);ncpf.configuration.overhaul.fissionMSR.blocks.add(mPurpur);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mDiamond);ncpf.configuration.overhaul.fissionMSR.blocks.add(mDiamond);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mEmerald);ncpf.configuration.overhaul.fissionMSR.blocks.add(mEmerald);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mCopper);ncpf.configuration.overhaul.fissionMSR.blocks.add(mCopper);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mTin);ncpf.configuration.overhaul.fissionMSR.blocks.add(mTin);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mLead);ncpf.configuration.overhaul.fissionMSR.blocks.add(mLead);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mBoron);ncpf.configuration.overhaul.fissionMSR.blocks.add(mBoron);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mLithium);ncpf.configuration.overhaul.fissionMSR.blocks.add(mLithium);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mMagnesium);ncpf.configuration.overhaul.fissionMSR.blocks.add(mMagnesium);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mManganese);ncpf.configuration.overhaul.fissionMSR.blocks.add(mManganese);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mAluminum);ncpf.configuration.overhaul.fissionMSR.blocks.add(mAluminum);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mSilver);ncpf.configuration.overhaul.fissionMSR.blocks.add(mSilver);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mFluorite);ncpf.configuration.overhaul.fissionMSR.blocks.add(mFluorite);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mVilliaumite);ncpf.configuration.overhaul.fissionMSR.blocks.add(mVilliaumite);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mCarobbiite);ncpf.configuration.overhaul.fissionMSR.blocks.add(mCarobbiite);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mArsenic);ncpf.configuration.overhaul.fissionMSR.blocks.add(mArsenic);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mNitrogen);ncpf.configuration.overhaul.fissionMSR.blocks.add(mNitrogen);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mHelium);ncpf.configuration.overhaul.fissionMSR.blocks.add(mHelium);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mEnderium);ncpf.configuration.overhaul.fissionMSR.blocks.add(mEnderium);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mCryotheum);ncpf.configuration.overhaul.fissionMSR.blocks.add(mCryotheum);
                    mstandard.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[0]));
                    miron.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[1]));
                    mredstone.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[2]));
                    mquartz.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[3]));
                    mobsidian.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[4]));
                    mnetherBrick.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[5]));
                    mglowstone.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[6]));
                    mlapis.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[7]));
                    mgold.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[8]));
                    mprismarine.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[9]));
                    mslime.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[10]));
                    mendStone.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[11]));
                    mPurpur.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[12]));
                    mDiamond.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[13]));
                    mEmerald.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[14]));
                    mCopper.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[15]));
                    mTin.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[16]));
                    mLead.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[17]));
                    mBoron.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[18]));
                    mLithium.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[19]));
                    mMagnesium.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[20]));
                    mManganese.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[21]));
                    mAluminum.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[22]));
                    mSilver.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[23]));
                    mFluorite.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[24]));
                    mVilliaumite.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[25]));
                    mCarobbiite.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[26]));
                    mArsenic.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[27]));
                    mNitrogen.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[28]));
                    mHelium.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[29]));
                    mEnderium.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[30]));
                    mCryotheum.rules.add(multiblock.configuration.overhaul.fissionmsr.PlacementRule.parseNC(ncpf.configuration.overhaul.fissionMSR, rules[31]));
                    multiblock.configuration.overhaul.fissionmsr.Block mgraphite = multiblock.configuration.overhaul.fissionmsr.Block.moderator("Graphite Moderator", "overhaul/graphite", fluxFac[0], (float) modEff[0]);
                    multiblock.configuration.overhaul.fissionmsr.Block mberyllium = multiblock.configuration.overhaul.fissionmsr.Block.moderator("Beryllium Moderator", "overhaul/beryllium", fluxFac[1], (float) modEff[1]);
                    multiblock.configuration.overhaul.fissionmsr.Block mheavyWater = multiblock.configuration.overhaul.fissionmsr.Block.moderator("Heavy Water Moderator", "overhaul/heavy water", fluxFac[2], (float) modEff[2]);
                    multiblock.configuration.overhaul.fissionmsr.Block mbec = multiblock.configuration.overhaul.fissionmsr.Block.reflector("Beryllium-Carbon Reflector", "overhaul/beryllium-carbon", (float) refEff[0], (float) refRef[0]);
                    multiblock.configuration.overhaul.fissionmsr.Block mpbs = multiblock.configuration.overhaul.fissionmsr.Block.reflector("Lead-Steel Reflector", "overhaul/lead-steel", (float) refEff[1], (float) refRef[1]);
                    multiblock.configuration.overhaul.fissionmsr.Block mbag = multiblock.configuration.overhaul.fissionmsr.Block.shield("Boron-Silver Neutron Shield", "overhaul/boron-silver", "overhaul/boron-silver_closed", (int) shieldHeat[0], (float) shieldEff[0]);
                    multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe mirrec1 = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe("Thorium to Protactinium-Enriched Thorium", (float)irrEff[0], (float)irrHeat[0]);
                    multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe mirrec2 = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe("Protactinium-Enriched Thorium to Protactinium-233", (float)irrEff[1], (float)irrHeat[1]);
                    multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe mirrec3 = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe("Bismuth Dust to Polonium Dust", (float)irrEff[2], (float)irrHeat[2]);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mgraphite);ncpf.configuration.overhaul.fissionMSR.blocks.add(mgraphite);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mberyllium);ncpf.configuration.overhaul.fissionMSR.blocks.add(mberyllium);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mheavyWater);ncpf.configuration.overhaul.fissionMSR.blocks.add(mheavyWater);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mbec);ncpf.configuration.overhaul.fissionMSR.blocks.add(mbec);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mpbs);ncpf.configuration.overhaul.fissionMSR.blocks.add(mpbs);
                    ncpf.configuration.overhaul.fissionMSR.allBlocks.add(mbag);ncpf.configuration.overhaul.fissionMSR.blocks.add(mbag);
                    ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(mirrec1);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(mirrec1);
                    ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(mirrec2);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(mirrec2);
                    ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(mirrec3);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(mirrec3);
                    addMSRFuels(ncpf, fuelTimeMult, "thorium", null, null, null, null, "TBU Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "uranium", null, null, null, null, "LEU-233 Fluoride", null, null, null, null, "HEU-233 Fluoride", null, null, null, null, "LEU-235 Fluoride", null, null, null, null, "HEU-235 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "neptunium", null, null, null, null, "LEN-236 Fluoride", null, null, null, null, "HEN-236 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "plutonium", null, null, null, null, "LEP-239 Fluoride", null, null, null, null, "HEP-239 Fluoride", null, null, null, null, "LEP-241 Fluoride", null, null, null, null, "HEP-241 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "mixed", null, null, null, null, "MF4-239", null, null, null, null, "MF4-241");
                    addMSRFuels(ncpf, fuelTimeMult, "americium", null, null, null, null, "LEA-242 Fluoride", null, null, null, null, "HEA-242 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "curium", null, null, null, null, "LECm-243 Fluoride", null, null, null, null, "HECm-243 Fluoride", null, null, null, null, "LECm-245 Fluoride", null, null, null, null, "HECm-245 Fluoride", null, null, null, null, "LECm-247 Fluoride", null, null, null, null, "HECm-247 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "berkelium", null, null, null, null, "LEB-248 Fluoride", null, null, null, null, "HEB-248 Fluoride");
                    addMSRFuels(ncpf, fuelTimeMult, "californium", null, null, null, null, "LECf-249 Fluoride", null, null, null, null, "HECf-249 Fluoride", null, null, null, null, "LECf-251 Fluoride", null, null, null, null, "HECf-251 Fluoride");
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Turbine">
                    ncpf.configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
                    ncpf.configuration.overhaul.turbine.fluidPerBlade = getInt("turbine_mb_per_blade");
                    ncpf.configuration.overhaul.turbine.minLength = getInt("turbine_min_size");
                    ncpf.configuration.overhaul.turbine.minWidth = Math.max(3, getInt("turbine_min_size"));
                    ncpf.configuration.overhaul.turbine.maxSize = getInt("turbine_max_size");
                    ncpf.configuration.overhaul.turbine.throughputEfficiencyLeniency = (float)getDouble("turbine_throughput_efficiency_leniency");
                    ncpf.configuration.overhaul.turbine.throughputFactor = (float)getDouble("turbine_tension_throughput_factor");
                    ncpf.configuration.overhaul.turbine.powerBonus = (float)getDouble("turbine_power_bonus_multiplier");
                    double[] bladeEffs = getDoubles("turbine_blade_efficiency");
                    double[] bladeExps = getDoubles("turbine_blade_expansion");
                    multiblock.configuration.overhaul.turbine.Blade steel = multiblock.configuration.overhaul.turbine.Blade.blade("Steel Rotor Blade", (float)bladeEffs[0], (float)bladeExps[0], "overhaul/turbine/steel");
                    multiblock.configuration.overhaul.turbine.Blade extreme = multiblock.configuration.overhaul.turbine.Blade.blade("Extreme Alloy Rotor Blade", (float)bladeEffs[1], (float)bladeExps[1], "overhaul/turbine/extreme");
                    multiblock.configuration.overhaul.turbine.Blade sic = multiblock.configuration.overhaul.turbine.Blade.blade("SiC-SiC CMC Rotor Blade", (float)bladeEffs[2], (float)bladeExps[2], "overhaul/turbine/sic");
                    multiblock.configuration.overhaul.turbine.Blade stator = multiblock.configuration.overhaul.turbine.Blade.stator("Rotor Stator", (float)getDouble("turbine_stator_expansion"), "overhaul/turbine/stator");
                    ncpf.configuration.overhaul.turbine.allBlades.add(steel);ncpf.configuration.overhaul.turbine.blades.add(steel);
                    ncpf.configuration.overhaul.turbine.allBlades.add(extreme);ncpf.configuration.overhaul.turbine.blades.add(extreme);
                    ncpf.configuration.overhaul.turbine.allBlades.add(sic);ncpf.configuration.overhaul.turbine.blades.add(sic);
                    ncpf.configuration.overhaul.turbine.allBlades.add(stator);ncpf.configuration.overhaul.turbine.blades.add(stator);
                    double[] coilEffs = getDoubles("turbine_coil_conductivity");
                    String[] coilRules = getStrings("turbine_coil_rule");
                    multiblock.configuration.overhaul.turbine.Coil cmagnesium = multiblock.configuration.overhaul.turbine.Coil.coil("Magnesium Dynamo Coil", (float)coilEffs[0], "overhaul/turbine/magnesium");
                    multiblock.configuration.overhaul.turbine.Coil cberyllium = multiblock.configuration.overhaul.turbine.Coil.coil("Beryllium Dynamo Coil", (float)coilEffs[1], "overhaul/turbine/beryllium");
                    multiblock.configuration.overhaul.turbine.Coil caluminum = multiblock.configuration.overhaul.turbine.Coil.coil("Aluminum Dynamo Coil", (float)coilEffs[2], "overhaul/turbine/aluminum");
                    multiblock.configuration.overhaul.turbine.Coil cgold = multiblock.configuration.overhaul.turbine.Coil.coil("Gold Dynamo Coil", (float)coilEffs[3], "overhaul/turbine/gold");
                    multiblock.configuration.overhaul.turbine.Coil ccopper = multiblock.configuration.overhaul.turbine.Coil.coil("Copper Dynamo Coil", (float)coilEffs[4], "overhaul/turbine/copper");
                    multiblock.configuration.overhaul.turbine.Coil csilver = multiblock.configuration.overhaul.turbine.Coil.coil("Silver Dynamo Coil", (float)coilEffs[5], "overhaul/turbine/silver");
                    multiblock.configuration.overhaul.turbine.Coil connector = multiblock.configuration.overhaul.turbine.Coil.connector("Dynamo Coil Connector", "overhaul/turbine/connector");
                    multiblock.configuration.overhaul.turbine.Coil bearing = multiblock.configuration.overhaul.turbine.Coil.bearing("Rotor Bearing", "overhaul/turbine/bearing");
                    ncpf.configuration.overhaul.turbine.allCoils.add(bearing);ncpf.configuration.overhaul.turbine.coils.add(bearing);
                    ncpf.configuration.overhaul.turbine.allCoils.add(cmagnesium);ncpf.configuration.overhaul.turbine.coils.add(cmagnesium);
                    ncpf.configuration.overhaul.turbine.allCoils.add(cberyllium);ncpf.configuration.overhaul.turbine.coils.add(cberyllium);
                    ncpf.configuration.overhaul.turbine.allCoils.add(caluminum);ncpf.configuration.overhaul.turbine.coils.add(caluminum);
                    ncpf.configuration.overhaul.turbine.allCoils.add(cgold);ncpf.configuration.overhaul.turbine.coils.add(cgold);
                    ncpf.configuration.overhaul.turbine.allCoils.add(ccopper);ncpf.configuration.overhaul.turbine.coils.add(ccopper);
                    ncpf.configuration.overhaul.turbine.allCoils.add(csilver);ncpf.configuration.overhaul.turbine.coils.add(csilver);
                    ncpf.configuration.overhaul.turbine.allCoils.add(connector);ncpf.configuration.overhaul.turbine.coils.add(connector);
                    connector.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, getStrings("turbine_connector_rule")[0]));
                    cmagnesium.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[0]));
                    cberyllium.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[1]));
                    caluminum.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[2]));
                    cgold.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[3]));
                    ccopper.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[4]));
                    csilver.rules.add(multiblock.configuration.overhaul.turbine.PlacementRule.parseNC(ncpf.configuration.overhaul.turbine, coilRules[5]));
                    double[] rPows = getDoubles("turbine_power_per_mb");
                    double[] rCoeffs = getDoubles("turbine_expansion_level");
                    multiblock.configuration.overhaul.turbine.Recipe hps = new multiblock.configuration.overhaul.turbine.Recipe("High Pressure Steam", "High Pressure Steam", "Exhaust Steam", rPows[0], rCoeffs[0]);
                    multiblock.configuration.overhaul.turbine.Recipe lps = new multiblock.configuration.overhaul.turbine.Recipe("Low Pressure Steam", "Low Pressure Steam", "Low Quality Steam", rPows[1], rCoeffs[1]);
                    multiblock.configuration.overhaul.turbine.Recipe ste = new multiblock.configuration.overhaul.turbine.Recipe("Steam", "Steam", "Low Quality Steam", rPows[2], rCoeffs[2]);
                    ncpf.configuration.overhaul.turbine.allRecipes.add(hps);ncpf.configuration.overhaul.turbine.recipes.add(hps);
                    ncpf.configuration.overhaul.turbine.allRecipes.add(lps);ncpf.configuration.overhaul.turbine.recipes.add(lps);
                    ncpf.configuration.overhaul.turbine.allRecipes.add(ste);ncpf.configuration.overhaul.turbine.recipes.add(ste);
//</editor-fold>
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private void addSFRFuels(NCPFFile ncpf, double timeMult, String baseName, String... fuelNames){
                int[] time = getInts("fission_"+baseName+"_fuel_time");
                int[] heat = getInts("fission_"+baseName+"_heat_generation");
                double[] efficiency = getDoubles("fission_"+baseName+"_efficiency");
                int[] criticality = getInts("fission_"+baseName+"_criticality");
                boolean[] selfPriming = getBooleans("fission_"+baseName+"_self_priming");
                for(int i = 0; i<fuelNames.length; i++){
                    if(fuelNames[i]==null)continue;
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelNames[i], (float)efficiency[i], heat[i], (int)(time[i]*timeMult), criticality[i], selfPriming[i]);
                    ncpf.configuration.overhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionSFR.fuels.add(fuel);
                }
            }
            private void addMSRFuels(NCPFFile ncpf, double timeMult, String baseName, String... fuelNames){
                int[] time = getInts("fission_"+baseName+"_fuel_time");
                int[] heat = getInts("fission_"+baseName+"_heat_generation");
                double[] efficiency = getDoubles("fission_"+baseName+"_efficiency");
                int[] criticality = getInts("fission_"+baseName+"_criticality");
                boolean[] selfPriming = getBooleans("fission_"+baseName+"_self_priming");
                for(int i = 0; i<fuelNames.length; i++){
                    if(fuelNames[i]==null)continue;
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelNames[i], (float)efficiency[i], heat[i], (int)(time[i]*timeMult), criticality[i], selfPriming[i]);
                    ncpf.configuration.overhaul.fissionMSR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionMSR.fuels.add(fuel);
                }
            }
            private double getDouble(String name){
                String str = s.substring(s.indexOf("D:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Double.parseDouble(str);
            }
            private int getInt(String name){
                String str = s.substring(s.indexOf("I:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Integer.parseInt(str);
            }
            private boolean getBoolean(String name){
                String str = s.substring(s.indexOf("B:"+name+"=")+(name.length()+3));
                str = str.substring(0, Math.min(str.indexOf('\n'), str.indexOf(' ')));
                return Boolean.parseBoolean(str);
            }
            private double[] getDoubles(String name){
                ArrayList<Double> doubles = new ArrayList<>();
                String str = s.substring(s.indexOf("D:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    doubles.add(Double.parseDouble(st.trim()));
                }
                double[] ret = new double[doubles.size()];
                for(int i = 0; i<doubles.size(); i++){
                    ret[i] = doubles.get(i);
                }
                return ret;
            }
            private int[] getDoublesAsInts(String name){
                double[] ds = getDoubles(name);
                int[] is = new int[ds.length];
                for(int i = 0; i<is.length; i++){
                    is[i] = (int)ds[i];
                }
                return is;
            }
            private int[] getInts(String name){
                ArrayList<Integer> ints = new ArrayList<>();
                String str = s.substring(s.indexOf("I:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    ints.add(Integer.parseInt(st.trim()));
                }
                int[] ret = new int[ints.size()];
                for(int i = 0; i<ints.size(); i++){
                    ret[i] = ints.get(i);
                }
                return ret;
            }
            private boolean[] getBooleans(String name){
                ArrayList<Boolean> booleans = new ArrayList<>();
                String str = s.substring(s.indexOf("B:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    booleans.add(Boolean.parseBoolean(st.trim()));
                }
                boolean[] ret = new boolean[booleans.size()];
                for(int i = 0; i<booleans.size(); i++){
                    ret[i] = booleans.get(i);
                }
                return ret;
            }
            private String[] getStrings(String name){
                ArrayList<String> strings = new ArrayList<>();
                String str = s.substring(s.indexOf("S:"+name+" <")+(name.length()+4));
                str = str.substring(0, str.indexOf('>'));
                for(String st : str.split("\n")){
                    if(st.trim().isEmpty())continue;
                    strings.add(st.trim());
                }
                String[] ret = new String[strings.size()];
                for(int i = 0; i<strings.size(); i++){
                    ret[i] = strings.get(i);
                }
                return ret;
            }
        });// OVERHAUL nuclearcraft.cfg
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                return major==1&&minor==2&&build>=5&&build<=22;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
                String fuelName = usedFuel.getString("Name");
                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = null;
                for(multiblock.configuration.underhaul.fissionsfr.Fuel fool : Core.configuration.underhaul.fissionSFR.allFuels){
                    if(fool.name.equalsIgnoreCase(fuelName))fuel = fool;
                }
                if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+fuelName);
                UnderhaulSFR sfr = new UnderhaulSFR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), fuel);
                JSON.JSONArray compressedReactor = hellrage.getJSONArray("CompressedReactor");
                for(Object o : compressedReactor){
                    JSONObject ob = (JSONObject) o;
                    String name = ob.keySet().iterator().next();
                    multiblock.configuration.underhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.underhaul.fissionsfr.Block blok : Core.configuration.underhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace("cooler", "").replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSONArray blocks = ob.getJSONArray(name);
                    for(Object blok : blocks){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage .json 1.2.5-1.2.22
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                return major==1&&minor==2&&build>=23;//&&build<=25;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
                JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
                String fuelName = usedFuel.getString("Name");
                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = null;
                for(multiblock.configuration.underhaul.fissionsfr.Fuel fool : Core.configuration.underhaul.fissionSFR.allFuels){
                    if(fool.name.equalsIgnoreCase(fuelName))fuel = fool;
                }
                if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+fuelName);
                UnderhaulSFR sfr = new UnderhaulSFR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), fuel);
                JSON.JSONObject compressedReactor = hellrage.getJSONObject("CompressedReactor");
                for(String name : compressedReactor.keySet()){
                    multiblock.configuration.underhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.underhaul.fissionsfr.Block blok : Core.configuration.underhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace("cooler", "").replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSONArray blocks = compressedReactor.getJSONArray(name);
                    for(Object blok : blocks){
                        JSONObject blokLoc = (JSONObject) blok;
                        int x = blokLoc.getInt("X")-1;
                        int y = blokLoc.getInt("Y")-1;
                        int z = blokLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage .json 1.2.23-1.2.25 (present)
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==0&&build>=1&&build<=6;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                OverhaulSFR sfr = new OverhaulSFR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(0));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean source = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    float highest = 0;
                    for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                        if(scr.efficiency>highest){
                            src = scr;
                            highest = src.efficiency;
                        }
                    }
                    if(src==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(source)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.0.1-2.0.6
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==0&&build>=7&&build<=29;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                OverhaulSFR sfr = new OverhaulSFR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(0));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    String blokLoc = (String) blok;
                    String[] blockLoc = blokLoc.split(",");
                    int x = Integer.parseInt(blockLoc[0])-1;
                    int y = Integer.parseInt(blockLoc[1])-1;
                    int z = Integer.parseInt(blockLoc[2])-1;
                    sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean source = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    float highest = 0;
                    for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                        if(scr.efficiency>highest){
                            src = scr;
                            highest = src.efficiency;
                        }
                    }
                    if(src==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(source)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.0.7-2.0.29
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==0&&build==30;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                String coolantRecipeName = hellrage.getString("CoolantRecipeName").replace("Hight", "High");
                CoolantRecipe coolantRecipe = null;
                for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
                    if(recipe.name.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
                }
                if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
                OverhaulSFR sfr = new OverhaulSFR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), coolantRecipe);
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    String blokLoc = (String) blok;
                    String[] blockLoc = blokLoc.split(",");
                    int x = Integer.parseInt(blockLoc[0])-1;
                    int y = Integer.parseInt(blockLoc[1])-1;
                    int z = Integer.parseInt(blockLoc[2])-1;
                    sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.0.30
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==0&&build==31;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
                String coolantRecipeName = hellrage.getString("CoolantRecipeName").replace("Hight", "High");
                CoolantRecipe coolantRecipe = null;
                for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
                    if(recipe.name.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
                }
                if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
                OverhaulSFR sfr = new OverhaulSFR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), coolantRecipe);
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    JSONObject blockLoc = (JSONObject) blok;
                    int x = blockLoc.getInt("X")-1;
                    int y = blockLoc.getInt("Y")-1;
                    int z = blockLoc.getInt("Z")-1;
                    sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.0.31
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==0&&build>=32&&build<=37;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
                String coolantRecipeName = hellrage.getString("CoolantRecipeName");
                CoolantRecipe coolantRecipe = null;
                for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
                    if(recipe.name.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
                }
                if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
                OverhaulSFR sfr = new OverhaulSFR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), coolantRecipe);
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                JSON.JSONObject reflectors = hellrage.getJSONObject("Reflectors");
                for(String name : reflectors.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("reflector", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = reflectors.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.0.32-2.0.37
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSONObject data = hellrage.getJSONObject("Data");
                JSON.JSONObject fuelCells = data.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    if(name.startsWith("[F4]"))return false;//that's an MSR!
                }
                return major==2&&minor==1&&build>=1;//&&build<=7;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject data = hellrage.getJSONObject("Data");
                JSONObject dims = data.getJSONObject("InteriorDimensions");
                String coolantRecipeName = data.getString("CoolantRecipeName");
                CoolantRecipe coolantRecipe = null;
                for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
                    if(recipe.name.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
                }
                if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
                OverhaulSFR sfr = new OverhaulSFR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), coolantRecipe);
                JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = data.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = data.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, conductor));
                    }
                }
                JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
                for(String name : reflectors.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("reflector", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = reflectors.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
                for(String name : neutronShields.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("neutronshield", "").replace("shield", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = neutronShields.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block irradiator = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.irradiator)irradiator = blok;
                }
                if(irradiator==null)throw new IllegalArgumentException("Unknown block: Irradiator");
                JSON.JSONObject irradiators = data.getJSONObject("Irradiators");
                for(String name : irradiators.keySet()){
                    multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = null;
                    try{
                        JSON.JSONObject recipe = JSON.parse(name);
                        for(multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irr : Core.configuration.overhaul.fissionSFR.allIrradiatorRecipes){
                            if(irr.heat==recipe.getFloat("HeatPerFlux")&&irr.efficiency==recipe.getFloat("EfficiencyMultiplier"))irrecipe = irr;
                        }
                    }catch(IOException ex){
                        throw new IllegalArgumentException("Invalid irradiator recipe: "+name);
                    }
                    JSON.JSONArray array = irradiators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, irradiator));
                        sfr.getBlock(x, y, z).irradiatorRecipe = irrecipe;
                    }
                }
                multiblock.configuration.overhaul.fissionsfr.Block cell = null;
                for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(blok.fuelCell)cell = blok;
                }
                if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
                JSON.JSONObject fuelCells = data.getJSONObject("FuelCells");
                for(String name : fuelCells.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionsfr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
                    if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
                    if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
                    for(multiblock.configuration.overhaul.fissionsfr.Fuel feul : Core.configuration.overhaul.fissionSFR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionsfr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        if(sourceName.equals("Self"))hasSource = false;
                        else{
                            for(multiblock.configuration.overhaul.fissionsfr.Source scr : Core.configuration.overhaul.fissionSFR.allSources){
                                if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                            }
                            if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                        }
                    }
                    JSON.JSONArray array = fuelCells.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, cell));
                        sfr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)sfr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(sfr);
                return file;
            }
        });// hellrage SFR .json 2.1.1-2.1.7 (present)
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==0&&build>=1&&build<=6;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                OverhaulMSR msr = new OverhaulMSR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean source = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    float highest = 0;
                    for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                        if(scr.efficiency>highest){
                            src = scr;
                            highest = src.efficiency;
                        }
                    }
                    if(src==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(source)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.0.1-2.0.6
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==0&&build>=7&&build<=29;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                OverhaulMSR msr = new OverhaulMSR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    String blokLoc = (String) blok;
                    String[] blockLoc = blokLoc.split(",");
                    int x = Integer.parseInt(blockLoc[0])-1;
                    int y = Integer.parseInt(blockLoc[1])-1;
                    int z = Integer.parseInt(blockLoc[2])-1;
                    msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean source = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    float highest = 0;
                    for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                        if(scr.efficiency>highest){
                            src = scr;
                            highest = src.efficiency;
                        }
                    }
                    if(src==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(source)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.0.7-2.0.29
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==0&&build==30;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                String dimS = hellrage.getString("InteriorDimensions");
                String[] dims = dimS.split(",");
                OverhaulMSR msr = new OverhaulMSR(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    String blokLoc = (String) blok;
                    String[] blockLoc = blokLoc.split(",");
                    int x = Integer.parseInt(blockLoc[0])-1;
                    int y = Integer.parseInt(blockLoc[1])-1;
                    int z = Integer.parseInt(blockLoc[2])-1;
                    msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        String blokLoc = (String) blok;
                        String[] blockLoc = blokLoc.split(",");
                        int x = Integer.parseInt(blockLoc[0])-1;
                        int y = Integer.parseInt(blockLoc[1])-1;
                        int z = Integer.parseInt(blockLoc[2])-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.0.30
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==0&&build==31;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
                OverhaulMSR msr = new OverhaulMSR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block reflector = null;
                float best = 0;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.reflector&&blok.reflectivity>best){
                        reflector = blok;
                        best = blok.reflectivity;
                    }
                }
                if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
                JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
                for(Object blok : reflectors){
                    JSONObject blockLoc = (JSONObject) blok;
                    int x = blockLoc.getInt("X")-1;
                    int y = blockLoc.getInt("Y")-1;
                    int z = blockLoc.getInt("Z")-1;
                    msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, reflector));
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.0.31
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==0&&build>=32&&build<=37;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
                OverhaulMSR msr = new OverhaulMSR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
                JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                JSON.JSONObject reflectors = hellrage.getJSONObject("Reflectors");
                for(String name : reflectors.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("reflector", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = reflectors.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                            if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                        }
                        if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                    }
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.0.32-2.0.37
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
                int major = saveVersion.getInt("Major");
                int minor = saveVersion.getInt("Minor");
                int build = saveVersion.getInt("Build");
                JSONObject data = hellrage.getJSONObject("Data");
                JSON.JSONObject fuelVessels = data.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    if(!name.startsWith("[F4]"))return false;//that's not an MSR!
                }
                return major==2&&minor==1&&build>=1;//&&build<=7;
            }
            @Override
            public synchronized NCPFFile read(InputStream in){
                JSONObject hellrage = JSON.parse(in);
                JSONObject data = hellrage.getJSONObject("Data");
                JSONObject dims = data.getJSONObject("InteriorDimensions");
                OverhaulMSR msr = new OverhaulMSR(dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
                JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
                for(String name : heatSinks.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = heatSinks.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject moderators = data.getJSONObject("Moderators");
                for(String name : moderators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = moderators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.name.equalsIgnoreCase("conductor"))conductor = blok;
                }
                if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
                JSON.JSONArray conductors = data.getJSONArray("Conductors");
                if(conductors!=null){
                    for(Object blok : conductors){
                            JSONObject blockLoc = (JSONObject) blok;
                            int x = blockLoc.getInt("X")-1;
                            int y = blockLoc.getInt("Y")-1;
                            int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, conductor));
                    }
                }
                JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
                for(String name : reflectors.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("reflector", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = reflectors.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
                for(String name : neutronShields.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.Block block = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                        if(blok.name.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("neutronshield", "").replace("shield", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                    }
                    if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                    JSON.JSONArray array = neutronShields.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, block));
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block irradiator = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.irradiator)irradiator = blok;
                }
                if(irradiator==null)throw new IllegalArgumentException("Unknown block: Irradiator");
                JSON.JSONObject irradiators = data.getJSONObject("Irradiators");
                for(String name : irradiators.keySet()){
                    multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = null;
                    try{
                        JSON.JSONObject recipe = JSON.parse(name);
                        for(multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irr : Core.configuration.overhaul.fissionMSR.allIrradiatorRecipes){
                            if(irr.heat==recipe.getFloat("HeatPerFlux")&&irr.efficiency==recipe.getFloat("EfficiencyMultiplier"))irrecipe = irr;
                        }
                    }catch(IOException ex){
                        throw new IllegalArgumentException("Invalid irradiator recipe: "+name);
                    }
                    JSON.JSONArray array = irradiators.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, irradiator));
                        msr.getBlock(x, y, z).irradiatorRecipe = irrecipe;
                    }
                }
                multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
                for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(blok.fuelVessel)vessel = blok;
                }
                if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
                JSON.JSONObject fuelVessels = data.getJSONObject("FuelCells");
                for(String name : fuelVessels.keySet()){
                    String[] fuelSettings = name.split(";");
                    String fuelName = fuelSettings[0];
                    boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
                    multiblock.configuration.overhaul.fissionmsr.Fuel fuel = null;
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
                    }
                    if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
                    for(multiblock.configuration.overhaul.fissionmsr.Fuel feul : Core.configuration.overhaul.fissionMSR.allFuels){
                        if(feul.name.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
                    }
                    if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
                    multiblock.configuration.overhaul.fissionmsr.Source src = null;
                    if(hasSource){
                        String sourceName = fuelSettings[2];
                        if(sourceName.equals("Self"))hasSource = false;
                        else{
                            for(multiblock.configuration.overhaul.fissionmsr.Source scr : Core.configuration.overhaul.fissionMSR.allSources){
                                if(scr.name.equalsIgnoreCase(sourceName))src = scr;
                            }
                            if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                        }
                    }
                    JSON.JSONArray array = fuelVessels.getJSONArray(name);
                    for(Object blok : array){
                        JSONObject blockLoc = (JSONObject) blok;
                        int x = blockLoc.getInt("X")-1;
                        int y = blockLoc.getInt("Y")-1;
                        int z = blockLoc.getInt("Z")-1;
                        msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, vessel));
                        msr.getBlock(x, y, z).fuel = fuel;
                        if(hasSource)msr.getBlock(x, y, z).source = src;
                    }
                }
                NCPFFile file = new NCPFFile();
                file.multiblocks.add(msr);
                return file;
            }
        });// hellrage MSR .json 2.1.1-2.1.7 (present)
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                try{
                    Config header = Config.newConfig();
                    header.load(in);
                    in.close();
                    return header.get("version", (byte)0)==(byte)1;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulPostLoadMap = new HashMap<>();
            @Override
            public synchronized NCPFFile read(InputStream in){
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
                    boolean partial = config.get("partial");
                    if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
                    else ncpf.configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
                    ncpf.configuration.addon = false;
                    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                    if(config.hasProperty("underhaul")){
                        ncpf.configuration.underhaul = new UnderhaulConfiguration();
                        Config underhaul = config.get("underhaul");
                        if(underhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = underhaul.get("fissionSFR");
                            ncpf.configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                            ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                            ConfigList blocks = fissionSFR.get("blocks");
                            underhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.active = blockCfg.get("active");
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readUnderRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.underhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                                byte index = underhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                                ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                            }
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                    if(config.hasProperty("overhaul")){
                        ncpf.configuration.overhaul = new OverhaulConfiguration();
                        Config overhaul = config.get("overhaul");
                        if(overhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = overhaul.get("fissionSFR");
                            ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionSFR.get("blocks");
                            overhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulPostLoadMap.keySet()){
                                byte index = overhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionSFR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionSFR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionSFR.allSources.add(source);ncpf.configuration.overhaul.fissionSFR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                            }
                            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                                Config coolantRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                                ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                            }
                        }
                    }
//</editor-fold>
                    for(int i = 0; i<multiblocks; i++){
                        Config data = Config.newConfig();
                        data.load(in);
                        Multiblock multiblock;
                        int id = data.get("id");
                        switch(id){
                            case 0:
                                ConfigNumberList size = data.get("size");
                                UnderhaulSFR underhaulSFR = new UnderhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                                boolean compact = data.get("compact");
                                ConfigNumberList blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<underhaulSFR.getX(); x++){
                                        for(int y = 0; y<underhaulSFR.getY(); y++){
                                            for(int z = 0; z<underhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                multiblock = underhaulSFR;
                                break;
                            case 1:
                                size = data.get("size");
                                OverhaulSFR overhaulSFR = new OverhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulSFR.getX(); x++){
                                        for(int y = 0; y<overhaulSFR.getY(); y++){
                                            for(int z = 0; z<overhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                ConfigNumberList fuels = data.get("fuels");
                                ConfigNumberList sources = data.get("sources");
                                ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                                int fuelIndex = 0;
                                int sourceIndex = 0;
                                int recipeIndex = 0;
                                for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                                    if(block.template.fuelCell){
                                        block.fuel = ncpf.configuration.overhaul.fissionSFR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionSFR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulSFR;
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
                        ncpf.multiblocks.add(multiblock);
                    }
                    in.close();
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        multiblock.configuration.underhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                        vert.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        vert.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        rule.rules.add(vert);
                        multiblock.configuration.underhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                        exact.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        exact.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        exact.min = exact.max = 3;
                        rule.rules.add(exact);
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        multiblock.configuration.overhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                        vert.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        vert.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        rule.rules.add(vert);
                        multiblock.configuration.overhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                        exact.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        exact.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        exact.min = exact.max = 3;
                        rule.rules.add(exact);
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverRule(rulC));
                        }
                        break;
                }
                return rule;
            }
        });// .ncpf version 1
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                try{
                    Config header = Config.newConfig();
                    header.load(in);
                    in.close();
                    return header.get("version", (byte)0)==(byte)2;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulSFRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Byte> overhaulMSRPostLoadMap = new HashMap<>();
            @Override
            public synchronized NCPFFile read(InputStream in){
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
                    boolean partial = config.get("partial");
                    if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
                    else ncpf.configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("underhaul")?config.get("version"):null);
                    ncpf.configuration.addon = false;
                    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                    if(config.hasProperty("underhaul")){
                        ncpf.configuration.underhaul = new UnderhaulConfiguration();
                        Config underhaul = config.get("underhaul");
                        if(underhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = underhaul.get("fissionSFR");
                            ncpf.configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                            ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                            ConfigList blocks = fissionSFR.get("blocks");
                            underhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.active = blockCfg.get("active");
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readUnderRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.underhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                                byte index = underhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                                ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                            }
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                    if(config.hasProperty("overhaul")){
                        ncpf.configuration.overhaul = new OverhaulConfiguration();
                        Config overhaul = config.get("overhaul");
                        //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                        if(overhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = overhaul.get("fissionSFR");
                            ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionSFR.get("blocks");
                            overhaulSFRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverSFRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                                byte index = overhaulSFRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionSFR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionSFR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionSFR.allSources.add(source);ncpf.configuration.overhaul.fissionSFR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                            }
                            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                                Config coolantRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                                ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                            }
                        }
//</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                        if(overhaul.hasProperty("fissionMSR")){
                            ncpf.configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                            Config fissionMSR = overhaul.get("fissionMSR");
                            ncpf.configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                            ncpf.configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                            ncpf.configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionMSR.get("blocks");
                            overhaulMSRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                                block.cooling = blockCfg.get("cooling", 0);
                                block.input = blockCfg.get("input");
                                block.output = blockCfg.get("output");
                                block.cluster = blockCfg.get("cluster", false);
                                block.createCluster = blockCfg.get("createCluster", false);
                                block.conductor = blockCfg.get("conductor", false);
                                block.fuelVessel = blockCfg.get("fuelVessel", false);
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverMSRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionMSR.allBlocks.add(block);ncpf.configuration.overhaul.fissionMSR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                                byte index = overhaulMSRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionMSR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionMSR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionMSR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionMSR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionmsr.Source source = new multiblock.configuration.overhaul.fissionmsr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionMSR.allSources.add(source);ncpf.configuration.overhaul.fissionMSR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                               multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(irrecipe);
                            }
                        }
//</editor-fold>
                    }
//</editor-fold>
                    for(int i = 0; i<multiblocks; i++){
                        Config data = Config.newConfig();
                        data.load(in);
                        Multiblock multiblock;
                        int id = data.get("id");
                        switch(id){
                            case 0:
                                ConfigNumberList size = data.get("size");
                                UnderhaulSFR underhaulSFR = new UnderhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                                boolean compact = data.get("compact");
                                ConfigNumberList blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<underhaulSFR.getX(); x++){
                                        for(int y = 0; y<underhaulSFR.getY(); y++){
                                            for(int z = 0; z<underhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                multiblock = underhaulSFR;
                                break;
                            case 1:
                                size = data.get("size");
                                OverhaulSFR overhaulSFR = new OverhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulSFR.getX(); x++){
                                        for(int y = 0; y<overhaulSFR.getY(); y++){
                                            for(int z = 0; z<overhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                ConfigNumberList fuels = data.get("fuels");
                                ConfigNumberList sources = data.get("sources");
                                ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                                int fuelIndex = 0;
                                int sourceIndex = 0;
                                int recipeIndex = 0;
                                for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                                    if(block.template.fuelCell){
                                        block.fuel = ncpf.configuration.overhaul.fissionSFR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionSFR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulSFR;
                                break;
                            case 2:
                                size = data.get("size");
                                OverhaulMSR overhaulMSR = new OverhaulMSR((int)size.get(0),(int)size.get(1),(int)size.get(2));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulMSR.getX(); x++){
                                        for(int y = 0; y<overhaulMSR.getY(); y++){
                                            for(int z = 0; z<overhaulMSR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                    }
                                }
                                fuels = data.get("fuels");
                                sources = data.get("sources");
                                irradiatorRecipes = data.get("irradiatorRecipes");
                                fuelIndex = 0;
                                sourceIndex = 0;
                                recipeIndex = 0;
                                for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
                                    if(block.template.fuelVessel){
                                        block.fuel = ncpf.configuration.overhaul.fissionMSR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionMSR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulMSR;
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
                        ncpf.multiblocks.add(multiblock);
                    }
                    in.close();
                    return ncpf;
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        multiblock.configuration.underhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                        vert.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        vert.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        rule.rules.add(vert);
                        multiblock.configuration.underhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                        exact.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        exact.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        exact.min = exact.max = 3;
                        rule.rules.add(exact);
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        multiblock.configuration.overhaul.fissionsfr.PlacementRule vert = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                        vert.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        vert.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        rule.rules.add(vert);
                        multiblock.configuration.overhaul.fissionsfr.PlacementRule exact = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                        exact.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        exact.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                        exact.min = exact.max = 3;
                        rule.rules.add(exact);
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionmsr.PlacementRule rule = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                        multiblock.configuration.overhaul.fissionmsr.PlacementRule vert = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                        vert.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX_GROUP;
                        vert.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                        rule.rules.add(vert);
                        multiblock.configuration.overhaul.fissionmsr.PlacementRule exact = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                        exact.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                        exact.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                        exact.min = exact.max = 3;
                        rule.rules.add(exact);
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
        });// .ncpf version 2
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                try{
                    Config header = Config.newConfig();
                    header.load(in);
                    in.close();
                    return header.get("version", (byte)0)==(byte)3;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulSFRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Byte> overhaulMSRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.turbine.PlacementRule, Byte> overhaulTurbinePostLoadMap = new HashMap<>();
            HashMap<OverhaulTurbine, ArrayList<Integer>> overhaulTurbinePostLoadInputsMap = new HashMap<>();
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
                    boolean partial = config.get("partial");
                    if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
                    else ncpf.configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
                    ncpf.configuration.addon = false;
                    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                    if(config.hasProperty("underhaul")){
                        ncpf.configuration.underhaul = new UnderhaulConfiguration();
                        Config underhaul = config.get("underhaul");
                        if(underhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = underhaul.get("fissionSFR");
                            ncpf.configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                            ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                            ConfigList blocks = fissionSFR.get("blocks");
                            underhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.active = blockCfg.get("active");
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readUnderRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.underhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                                byte index = underhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                                ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                            }
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                    if(config.hasProperty("overhaul")){
                        ncpf.configuration.overhaul = new OverhaulConfiguration();
                        Config overhaul = config.get("overhaul");
                        //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                        if(overhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = overhaul.get("fissionSFR");
                            ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionSFR.get("blocks");
                            overhaulSFRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverSFRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                                byte index = overhaulSFRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionSFR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionSFR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionSFR.allSources.add(source);ncpf.configuration.overhaul.fissionSFR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                            }
                            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                                Config coolantRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                                ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                            }
                        }
//</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                        if(overhaul.hasProperty("fissionMSR")){
                            ncpf.configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                            Config fissionMSR = overhaul.get("fissionMSR");
                            ncpf.configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                            ncpf.configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                            ncpf.configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionMSR.get("blocks");
                            overhaulMSRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                                block.cooling = blockCfg.get("cooling", 0);
                                block.input = blockCfg.get("input");
                                block.output = blockCfg.get("output");
                                block.cluster = blockCfg.get("cluster", false);
                                block.createCluster = blockCfg.get("createCluster", false);
                                block.conductor = blockCfg.get("conductor", false);
                                block.fuelVessel = blockCfg.get("fuelVessel", false);
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverMSRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionMSR.allBlocks.add(block);ncpf.configuration.overhaul.fissionMSR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                                byte index = overhaulMSRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionMSR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionMSR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionMSR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionMSR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionmsr.Source source = new multiblock.configuration.overhaul.fissionmsr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionMSR.allSources.add(source);ncpf.configuration.overhaul.fissionMSR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(irrecipe);
                            }
                        }
//</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Turbine Configuration">
                        if(overhaul.hasProperty("turbine")){
                            ncpf.configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
                            Config turbine = overhaul.get("turbine");
                            ncpf.configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                            ncpf.configuration.overhaul.turbine.minLength = turbine.get("minLength");
                            ncpf.configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                            ncpf.configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                            ncpf.configuration.overhaul.turbine.throughputEfficiencyLeniency = turbine.get("throughputEfficiencyLeniency");
                            ncpf.configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                            ncpf.configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
                            ConfigList coils = turbine.get("coils");
                            overhaulTurbinePostLoadMap.clear();
                            for(Iterator bit = coils.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.turbine.Coil coil = new multiblock.configuration.overhaul.turbine.Coil(blockCfg.get("name"));
                                coil.bearing = blockCfg.get("bearing", false);
                                coil.connector = blockCfg.get("connector", false);
                                coil.efficiency = blockCfg.get("efficiency");
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    coil.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        coil.rules.add(readOverTurbineRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.turbine.allCoils.add(coil);ncpf.configuration.overhaul.turbine.coils.add(coil);
                            }
                            ConfigList blades = turbine.get("blades");
                            for(Iterator bit = blades.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.turbine.Blade blade = new multiblock.configuration.overhaul.turbine.Blade(blockCfg.get("name"));
                                blade.expansion = blockCfg.get("expansion");
                                blade.efficiency = blockCfg.get("efficiency");
                                blade.stator = blade.expansion<1;
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    blade.setTexture(image);
                                }
                                ncpf.configuration.overhaul.turbine.allBlades.add(blade);ncpf.configuration.overhaul.turbine.blades.add(blade);
                            }
                            for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                                byte index = overhaulTurbinePostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                }else{
                                    rule.coil = ncpf.configuration.overhaul.turbine.allCoils.get(index-1);
                                }
                            }
                            ConfigList recipes = turbine.get("recipes");
                            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                                Config recipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(recipeCfg.get("name"), recipeCfg.get("input"), recipeCfg.get("output"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                                ncpf.configuration.overhaul.turbine.allRecipes.add(recipe);ncpf.configuration.overhaul.turbine.recipes.add(recipe);
                            }
                        }
//</editor-fold>
                    }
//</editor-fold>
                    for(int i = 0; i<multiblocks; i++){
                        Config data = Config.newConfig();
                        data.load(in);
                        Multiblock multiblock;
                        int id = data.get("id");
                        switch(id){
                            case 0:
                                //<editor-fold defaultstate="collapsed" desc="Underhaul SFR">
                                ConfigNumberList size = data.get("size");
                                UnderhaulSFR underhaulSFR = new UnderhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                                boolean compact = data.get("compact");
                                ConfigNumberList blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<underhaulSFR.getX(); x++){
                                        for(int y = 0; y<underhaulSFR.getY(); y++){
                                            for(int z = 0; z<underhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                multiblock = underhaulSFR;
//</editor-fold>
                                break;
                            case 1:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul SFR">
                                size = data.get("size");
                                OverhaulSFR overhaulSFR = new OverhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulSFR.getX(); x++){
                                        for(int y = 0; y<overhaulSFR.getY(); y++){
                                            for(int z = 0; z<overhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                ConfigNumberList fuels = data.get("fuels");
                                ConfigNumberList sources = data.get("sources");
                                ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                                int fuelIndex = 0;
                                int sourceIndex = 0;
                                int recipeIndex = 0;
                                for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                                    if(block.template.fuelCell){
                                        block.fuel = ncpf.configuration.overhaul.fissionSFR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionSFR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulSFR;
//</editor-fold>
                                break;
                            case 2:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul MSR">
                                size = data.get("size");
                                OverhaulMSR overhaulMSR = new OverhaulMSR((int)size.get(0),(int)size.get(1),(int)size.get(2));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulMSR.getX(); x++){
                                        for(int y = 0; y<overhaulMSR.getY(); y++){
                                            for(int z = 0; z<overhaulMSR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                    }
                                }
                                fuels = data.get("fuels");
                                sources = data.get("sources");
                                irradiatorRecipes = data.get("irradiatorRecipes");
                                fuelIndex = 0;
                                sourceIndex = 0;
                                recipeIndex = 0;
                                for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
                                    if(block.template.fuelVessel){
                                        block.fuel = ncpf.configuration.overhaul.fissionMSR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionMSR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulMSR;
//</editor-fold>
                                break;
                            case 3:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine">
                                size = data.get("size");
                                OverhaulTurbine overhaulTurbine = new OverhaulTurbine((int)size.get(0), (int)size.get(1), (int)size.get(2), ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", (byte)-1)));
                                if(data.hasProperty("inputs")){
                                    overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
                                    ConfigNumberList inputs = data.get("inputs");
                                    for(Number number : inputs.iterable()){
                                        overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
                                    }
                                }
                                ConfigNumberList coils = data.get("coils");
                                int index = 0;
                                for(int z = 0; z<2; z++){
                                    for(int x = 0; x<overhaulTurbine.getX(); x++){
                                        for(int y = 0; y<overhaulTurbine.getY(); y++){
                                            int bid = (int) coils.get(index);
                                            if(bid>0){
                                                overhaulTurbine.setCoilExact(x, y, z, new multiblock.overhaul.turbine.Block(x, y, z, ncpf.configuration.overhaul.turbine.allCoils.get(bid-1)));
                                            }
                                            index++;
                                        }
                                    }
                                }
                                ConfigNumberList blades = data.get("blades");
                                index = 0;
                                for(int z = 1; z<overhaulTurbine.getZ()-1; z++){
                                    int bid = (int) blades.get(index);
                                    if(bid>0){
                                        overhaulTurbine.setBladeExact(z, new multiblock.overhaul.turbine.Block(z, ncpf.configuration.overhaul.turbine.allBlades.get(bid-1)));
                                    }
                                    index++;
                                }
                                multiblock = overhaulTurbine;
//</editor-fold>
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
                        ncpf.multiblocks.add(multiblock);
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
            private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionmsr.PlacementRule rule = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.turbine.PlacementRule readOverTurbineRule(Config ruleCfg){
                multiblock.configuration.overhaul.turbine.PlacementRule rule = new multiblock.configuration.overhaul.turbine.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                }
                return rule;
            }
        });// .ncpf version 3
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                try{
                    Config header = Config.newConfig();
                    header.load(in);
                    in.close();
                    return header.get("version", (byte)0)==(byte)4;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulSFRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Byte> overhaulMSRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.turbine.PlacementRule, Byte> overhaulTurbinePostLoadMap = new HashMap<>();
            HashMap<OverhaulTurbine, ArrayList<Integer>> overhaulTurbinePostLoadInputsMap = new HashMap<>();
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
                    boolean partial = config.get("partial");
                    if(partial)ncpf.configuration = new PartialConfiguration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
                    else ncpf.configuration = new Configuration(config.get("name"), config.hasProperty("overhaul")?config.get("version"):null, config.hasProperty("overhaul")?config.get("underhaulVersion"):config.get("version"));
                    ncpf.configuration.addon = false;
                    //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                    if(config.hasProperty("underhaul")){
                        ncpf.configuration.underhaul = new UnderhaulConfiguration();
                        Config underhaul = config.get("underhaul");
                        if(underhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = underhaul.get("fissionSFR");
                            ncpf.configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                            ncpf.configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                            ncpf.configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                            ConfigList blocks = fissionSFR.get("blocks");
                            underhaulPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                                block.active = blockCfg.get("active");
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readUnderRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.underhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.underhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                                byte index = underhaulPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                                ncpf.configuration.underhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.underhaul.fissionSFR.fuels.add(fuel);
                            }
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                    if(config.hasProperty("overhaul")){
                        ncpf.configuration.overhaul = new OverhaulConfiguration();
                        Config overhaul = config.get("overhaul");
                        //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                        if(overhaul.hasProperty("fissionSFR")){
                            ncpf.configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                            Config fissionSFR = overhaul.get("fissionSFR");
                            ncpf.configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                            ncpf.configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                            ncpf.configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionSFR.get("blocks");
                            overhaulSFRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverSFRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionSFR.allBlocks.add(block);ncpf.configuration.overhaul.fissionSFR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                                byte index = overhaulSFRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionSFR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionSFR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionSFR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionSFR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionSFR.allSources.add(source);ncpf.configuration.overhaul.fissionSFR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                            }
                            ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                            for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                                Config coolantRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                                ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);ncpf.configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                            }
                        }
//</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                        if(overhaul.hasProperty("fissionMSR")){
                            ncpf.configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                            Config fissionMSR = overhaul.get("fissionMSR");
                            ncpf.configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                            ncpf.configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                            ncpf.configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                            ncpf.configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                            ncpf.configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                            ConfigList blocks = fissionMSR.get("blocks");
                            overhaulMSRPostLoadMap.clear();
                            for(Iterator bit = blocks.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                                block.cooling = blockCfg.get("cooling", 0);
                                block.input = blockCfg.get("input");
                                block.output = blockCfg.get("output");
                                block.cluster = blockCfg.get("cluster", false);
                                block.createCluster = blockCfg.get("createCluster", false);
                                block.conductor = blockCfg.get("conductor", false);
                                block.fuelVessel = blockCfg.get("fuelVessel", false);
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
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setTexture(image);
                                }
                                if(blockCfg.hasProperty("closedTexture")){
                                    ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                    int size = (int) closedTexture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)closedTexture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    block.setClosedTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        block.rules.add(readOverMSRRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.fissionMSR.allBlocks.add(block);ncpf.configuration.overhaul.fissionMSR.blocks.add(block);
                            }
                            for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                                byte index = overhaulMSRPostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                }else{
                                    rule.block = ncpf.configuration.overhaul.fissionMSR.allBlocks.get(index-1);
                                }
                            }
                            ConfigList fuels = fissionMSR.get("fuels");
                            for(Iterator fit = fuels.iterator(); fit.hasNext();){
                                Config fuelCfg = (Config)fit.next();
                                multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                                ncpf.configuration.overhaul.fissionMSR.allFuels.add(fuel);ncpf.configuration.overhaul.fissionMSR.fuels.add(fuel);
                            }
                            ConfigList sources = fissionMSR.get("sources");
                            for(Iterator sit = sources.iterator(); sit.hasNext();){
                                Config sourceCfg = (Config)sit.next();
                                multiblock.configuration.overhaul.fissionmsr.Source source = new multiblock.configuration.overhaul.fissionmsr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                                ncpf.configuration.overhaul.fissionMSR.allSources.add(source);ncpf.configuration.overhaul.fissionMSR.sources.add(source);
                            }
                            ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                            for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                                Config irradiatorRecipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                                ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(irrecipe);ncpf.configuration.overhaul.fissionMSR.irradiatorRecipes.add(irrecipe);
                            }
                        }
//</editor-fold>
                        //<editor-fold defaultstate="collapsed" desc="Turbine Configuration">
                        if(overhaul.hasProperty("turbine")){
                            ncpf.configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
                            Config turbine = overhaul.get("turbine");
                            ncpf.configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                            ncpf.configuration.overhaul.turbine.minLength = turbine.get("minLength");
                            ncpf.configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                            ncpf.configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                            ncpf.configuration.overhaul.turbine.throughputEfficiencyLeniency = turbine.get("throughputEfficiencyLeniency");
                            ncpf.configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                            ncpf.configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
                            ConfigList coils = turbine.get("coils");
                            overhaulTurbinePostLoadMap.clear();
                            for(Iterator bit = coils.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.turbine.Coil coil = new multiblock.configuration.overhaul.turbine.Coil(blockCfg.get("name"));
                                coil.bearing = blockCfg.get("bearing", false);
                                coil.connector = blockCfg.get("connector", false);
                                coil.efficiency = blockCfg.get("efficiency");
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    coil.setTexture(image);
                                }
                                if(blockCfg.hasProperty("rules")){
                                    ConfigList rules = blockCfg.get("rules");
                                    for(Iterator rit = rules.iterator(); rit.hasNext();){
                                        Config ruleCfg = (Config)rit.next();
                                        coil.rules.add(readOverTurbineRule(ruleCfg));
                                    }
                                }
                                ncpf.configuration.overhaul.turbine.allCoils.add(coil);ncpf.configuration.overhaul.turbine.coils.add(coil);
                            }
                            ConfigList blades = turbine.get("blades");
                            for(Iterator bit = blades.iterator(); bit.hasNext();){
                                Config blockCfg = (Config)bit.next();
                                multiblock.configuration.overhaul.turbine.Blade blade = new multiblock.configuration.overhaul.turbine.Blade(blockCfg.get("name"));
                                blade.expansion = blockCfg.get("expansion");
                                blade.efficiency = blockCfg.get("efficiency");
                                blade.stator = blockCfg.get("stator");
                                if(blockCfg.hasProperty("texture")){
                                    ConfigNumberList texture = blockCfg.get("texture");
                                    int size = (int) texture.get(0);
                                    BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                    int index = 1;
                                    for(int x = 0; x<image.getWidth(); x++){
                                        for(int y = 0; y<image.getHeight(); y++){
                                            Color color = new Color((int)texture.get(index));
                                            image.setRGB(x, y, color.getRGB());
                                            index++;
                                        }
                                    }
                                    blade.setTexture(image);
                                }
                                ncpf.configuration.overhaul.turbine.allBlades.add(blade);ncpf.configuration.overhaul.turbine.blades.add(blade);
                            }
                            for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                                byte index = overhaulTurbinePostLoadMap.get(rule);
                                if(index==0){
                                    if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                                    if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                                    rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                }else{
                                    rule.coil = ncpf.configuration.overhaul.turbine.allCoils.get(index-1);
                                }
                            }
                            ConfigList recipes = turbine.get("recipes");
                            for(Iterator irit = recipes.iterator(); irit.hasNext();){
                                Config recipeCfg = (Config)irit.next();
                                multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(recipeCfg.get("name"), recipeCfg.get("input"), recipeCfg.get("output"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                                ncpf.configuration.overhaul.turbine.allRecipes.add(recipe);ncpf.configuration.overhaul.turbine.recipes.add(recipe);
                            }
                        }
//</editor-fold>
                    }
//</editor-fold>
                    for(int i = 0; i<multiblocks; i++){
                        Config data = Config.newConfig();
                        data.load(in);
                        Multiblock multiblock;
                        int id = data.get("id");
                        switch(id){
                            case 0:
                                //<editor-fold defaultstate="collapsed" desc="Underhaul SFR">
                                ConfigNumberList size = data.get("size");
                                UnderhaulSFR underhaulSFR = new UnderhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                                boolean compact = data.get("compact");
                                ConfigNumberList blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<underhaulSFR.getX(); x++){
                                        for(int y = 0; y<underhaulSFR.getY(); y++){
                                            for(int z = 0; z<underhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                multiblock = underhaulSFR;
//</editor-fold>
                                break;
                            case 1:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul SFR">
                                size = data.get("size");
                                OverhaulSFR overhaulSFR = new OverhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulSFR.getX(); x++){
                                        for(int y = 0; y<overhaulSFR.getY(); y++){
                                            for(int z = 0; z<overhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                ConfigNumberList fuels = data.get("fuels");
                                ConfigNumberList sources = data.get("sources");
                                ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                                int fuelIndex = 0;
                                int sourceIndex = 0;
                                int recipeIndex = 0;
                                for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                                    if(block.template.fuelCell){
                                        block.fuel = ncpf.configuration.overhaul.fissionSFR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionSFR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulSFR;
//</editor-fold>
                                break;
                            case 2:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul MSR">
                                size = data.get("size");
                                OverhaulMSR overhaulMSR = new OverhaulMSR((int)size.get(0),(int)size.get(1),(int)size.get(2));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulMSR.getX(); x++){
                                        for(int y = 0; y<overhaulMSR.getY(); y++){
                                            for(int z = 0; z<overhaulMSR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                    }
                                }
                                fuels = data.get("fuels");
                                sources = data.get("sources");
                                irradiatorRecipes = data.get("irradiatorRecipes");
                                fuelIndex = 0;
                                sourceIndex = 0;
                                recipeIndex = 0;
                                for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
                                    if(block.template.fuelVessel){
                                        block.fuel = ncpf.configuration.overhaul.fissionMSR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionMSR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulMSR;
//</editor-fold>
                                break;
                            case 3:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine">
                                size = data.get("size");
                                OverhaulTurbine overhaulTurbine = new OverhaulTurbine((int)size.get(0), (int)size.get(1), (int)size.get(2), ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", (byte)-1)));
                                if(data.hasProperty("inputs")){
                                    overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
                                    ConfigNumberList inputs = data.get("inputs");
                                    for(Number number : inputs.iterable()){
                                        overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
                                    }
                                }
                                ConfigNumberList coils = data.get("coils");
                                int index = 0;
                                for(int z = 0; z<2; z++){
                                    for(int x = 0; x<overhaulTurbine.getX(); x++){
                                        for(int y = 0; y<overhaulTurbine.getY(); y++){
                                            int bid = (int) coils.get(index);
                                            if(bid>0){
                                                overhaulTurbine.setCoilExact(x, y, z, new multiblock.overhaul.turbine.Block(x, y, z, ncpf.configuration.overhaul.turbine.allCoils.get(bid-1)));
                                            }
                                            index++;
                                        }
                                    }
                                }
                                ConfigNumberList blades = data.get("blades");
                                index = 0;
                                for(int z = 1; z<overhaulTurbine.getZ()-1; z++){
                                    int bid = (int) blades.get(index);
                                    if(bid>0){
                                        overhaulTurbine.setBladeExact(z, new multiblock.overhaul.turbine.Block(z, ncpf.configuration.overhaul.turbine.allBlades.get(bid-1)));
                                    }
                                    index++;
                                }
                                multiblock = overhaulTurbine;
//</editor-fold>
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
                        ncpf.multiblocks.add(multiblock);
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
            private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionmsr.PlacementRule rule = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.turbine.PlacementRule readOverTurbineRule(Config ruleCfg){
                multiblock.configuration.overhaul.turbine.PlacementRule rule = new multiblock.configuration.overhaul.turbine.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                }
                return rule;
            }
        });// .ncpf version 4
        formats.add(new FormatReader(){
            @Override
            public boolean formatMatches(InputStream in){
                try{
                    Config header = Config.newConfig();
                    header.load(in);
                    in.close();
                    return header.get("version", (byte)0)==(byte)5;
                }catch(Throwable t){
                    return false;
                }
            }
            HashMap<multiblock.configuration.underhaul.fissionsfr.PlacementRule, Byte> underhaulPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionsfr.PlacementRule, Byte> overhaulSFRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.fissionmsr.PlacementRule, Byte> overhaulMSRPostLoadMap = new HashMap<>();
            HashMap<multiblock.configuration.overhaul.turbine.PlacementRule, Byte> overhaulTurbinePostLoadMap = new HashMap<>();
            HashMap<OverhaulTurbine, ArrayList<Integer>> overhaulTurbinePostLoadInputsMap = new HashMap<>();
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
                        Config data = Config.newConfig();
                        data.load(in);
                        Multiblock multiblock;
                        int id = data.get("id");
                        switch(id){
                            case 0:
                                //<editor-fold defaultstate="collapsed" desc="Underhaul SFR">
                                ConfigNumberList size = data.get("size");
                                UnderhaulSFR underhaulSFR = new UnderhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.underhaul.fissionSFR.allFuels.get(data.get("fuel", (byte)-1)));
                                boolean compact = data.get("compact");
                                ConfigNumberList blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<underhaulSFR.getX(); x++){
                                        for(int y = 0; y<underhaulSFR.getY(); y++){
                                            for(int z = 0; z<underhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0)underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        underhaulSFR.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(x, y, z, ncpf.configuration.underhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                multiblock = underhaulSFR;
//</editor-fold>
                                break;
                            case 1:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul SFR">
                                size = data.get("size");
                                OverhaulSFR overhaulSFR = new OverhaulSFR((int)size.get(0),(int)size.get(1),(int)size.get(2),ncpf.configuration.overhaul.fissionSFR.allCoolantRecipes.get(data.get("coolantRecipe", (byte)-1)));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulSFR.getX(); x++){
                                        for(int y = 0; y<overhaulSFR.getY(); y++){
                                            for(int z = 0; z<overhaulSFR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulSFR.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(x, y, z, ncpf.configuration.overhaul.fissionSFR.allBlocks.get(bid-1)));
                                    }
                                }
                                ConfigNumberList fuels = data.get("fuels");
                                ConfigNumberList sources = data.get("sources");
                                ConfigNumberList irradiatorRecipes = data.get("irradiatorRecipes");
                                int fuelIndex = 0;
                                int sourceIndex = 0;
                                int recipeIndex = 0;
                                for(multiblock.overhaul.fissionsfr.Block block : overhaulSFR.getBlocks()){
                                    if(block.template.fuelCell){
                                        block.fuel = ncpf.configuration.overhaul.fissionSFR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionSFR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionSFR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulSFR;
//</editor-fold>
                                break;
                            case 2:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul MSR">
                                size = data.get("size");
                                OverhaulMSR overhaulMSR = new OverhaulMSR((int)size.get(0),(int)size.get(1),(int)size.get(2));
                                compact = data.get("compact");
                                blocks = data.get("blocks");
                                if(compact){
                                    int index = 0;
                                    for(int x = 0; x<overhaulMSR.getX(); x++){
                                        for(int y = 0; y<overhaulMSR.getY(); y++){
                                            for(int z = 0; z<overhaulMSR.getZ(); z++){
                                                int bid = (int) blocks.get(index);
                                                if(bid>0){
                                                    overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                                }
                                                index++;
                                            }
                                        }
                                    }
                                }else{
                                    for(int j = 0; j<blocks.size(); j+=4){
                                        int x = (int) blocks.get(j);
                                        int y = (int) blocks.get(j+1);
                                        int z = (int) blocks.get(j+2);
                                        int bid = (int) blocks.get(j+3);
                                        overhaulMSR.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(x, y, z, ncpf.configuration.overhaul.fissionMSR.allBlocks.get(bid-1)));
                                    }
                                }
                                fuels = data.get("fuels");
                                sources = data.get("sources");
                                irradiatorRecipes = data.get("irradiatorRecipes");
                                fuelIndex = 0;
                                sourceIndex = 0;
                                recipeIndex = 0;
                                for(multiblock.overhaul.fissionmsr.Block block : overhaulMSR.getBlocks()){
                                    if(block.template.fuelVessel){
                                        block.fuel = ncpf.configuration.overhaul.fissionMSR.allFuels.get((int)fuels.get(fuelIndex));
                                        fuelIndex++;
                                        int sid = (int) sources.get(sourceIndex);
                                        if(sid>0)block.source = ncpf.configuration.overhaul.fissionMSR.allSources.get(sid-1);
                                        sourceIndex++;
                                    }
                                    if(block.template.irradiator){
                                        int rid = (int) irradiatorRecipes.get(recipeIndex);
                                        if(rid>0)block.irradiatorRecipe = ncpf.configuration.overhaul.fissionMSR.allIrradiatorRecipes.get(rid-1);
                                        recipeIndex++;
                                    }
                                }
                                multiblock = overhaulMSR;
//</editor-fold>
                                break;
                            case 3:
                                //<editor-fold defaultstate="collapsed" desc="Overhaul Turbine">
                                size = data.get("size");
                                OverhaulTurbine overhaulTurbine = new OverhaulTurbine((int)size.get(0), (int)size.get(1), (int)size.get(2), ncpf.configuration.overhaul.turbine.allRecipes.get(data.get("recipe", (byte)-1)));
                                if(data.hasProperty("inputs")){
                                    overhaulTurbinePostLoadInputsMap.put(overhaulTurbine, new ArrayList<>());
                                    ConfigNumberList inputs = data.get("inputs");
                                    for(Number number : inputs.iterable()){
                                        overhaulTurbinePostLoadInputsMap.get(overhaulTurbine).add(number.intValue());
                                    }
                                }
                                ConfigNumberList coils = data.get("coils");
                                int index = 0;
                                for(int z = 0; z<2; z++){
                                    for(int x = 0; x<overhaulTurbine.getX(); x++){
                                        for(int y = 0; y<overhaulTurbine.getY(); y++){
                                            int bid = (int) coils.get(index);
                                            if(bid>0){
                                                overhaulTurbine.setCoilExact(x, y, z, new multiblock.overhaul.turbine.Block(x, y, z, ncpf.configuration.overhaul.turbine.allCoils.get(bid-1)));
                                            }
                                            index++;
                                        }
                                    }
                                }
                                ConfigNumberList blades = data.get("blades");
                                index = 0;
                                for(int z = 1; z<overhaulTurbine.getZ()-1; z++){
                                    int bid = (int) blades.get(index);
                                    if(bid>0){
                                        overhaulTurbine.setBladeExact(z, new multiblock.overhaul.turbine.Block(z, ncpf.configuration.overhaul.turbine.allBlades.get(bid-1)));
                                    }
                                    index++;
                                }
                                multiblock = overhaulTurbine;
//</editor-fold>
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
                        ncpf.multiblocks.add(multiblock);
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
            private multiblock.configuration.underhaul.fissionsfr.PlacementRule readUnderRule(Config ruleCfg){
                multiblock.configuration.underhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.underhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        underhaulPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.COOLER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readUnderRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionsfr.PlacementRule readOverSFRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionsfr.PlacementRule rule = new multiblock.configuration.overhaul.fissionsfr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX;
                        overhaulSFRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.HEATSINK;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.FUEL_CELL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverSFRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.fissionmsr.PlacementRule readOverMSRRule(Config ruleCfg){
                multiblock.configuration.overhaul.fissionmsr.PlacementRule rule = new multiblock.configuration.overhaul.fissionmsr.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX;
                        overhaulMSRPostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.VERTEX_GROUP;
                        blockType = ruleCfg.get("block");
                        switch(blockType){
                            case 0:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                                break;
                            case 1:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CASING;
                                break;
                            case 2:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.HEATER;
                                break;
                            case 3:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.VESSEL;
                                break;
                            case 4:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.MODERATOR;
                                break;
                            case 5:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.REFLECTOR;
                                break;
                            case 6:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.SHIELD;
                                break;
                            case 7:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.IRRADIATOR;
                                break;
                            case 8:
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.CONDUCTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverMSRRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private multiblock.configuration.overhaul.turbine.PlacementRule readOverTurbineRule(Config ruleCfg){
                multiblock.configuration.overhaul.turbine.PlacementRule rule = new multiblock.configuration.overhaul.turbine.PlacementRule();
                byte type = ruleCfg.get("type");
                switch(type){
                    case 0:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 1:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 2:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE;
                        overhaulTurbinePostLoadMap.put(rule, ruleCfg.get("block"));
                        break;
                    case 3:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                        byte coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 4:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        rule.min = ruleCfg.get("min");
                        rule.max = ruleCfg.get("max");
                        break;
                    case 5:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.EDGE_GROUP;
                        coilType = ruleCfg.get("block");
                        switch(coilType){
                            case 0:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                                break;
                            case 1:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.COIL;
                                break;
                            case 2:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.BEARING;
                                break;
                            case 3:
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CONNECTOR;
                                break;
                        }
                        break;
                    case 6:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.OR;
                        ConfigList rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                    case 7:
                        rule.ruleType = multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AND;
                        rules = ruleCfg.get("rules");
                        for(Iterator rit = rules.iterator(); rit.hasNext();){
                            Config rulC = (Config)rit.next();
                            rule.rules.add(readOverTurbineRule(rulC));
                        }
                        break;
                }
                return rule;
            }
            private Configuration loadConfiguration(Config config){
                boolean partial = config.get("partial");
                Configuration configuration;
                if(partial)configuration = new PartialConfiguration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
                else configuration = new Configuration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
                configuration.addon = config.get("addon");
                //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                if(config.hasProperty("underhaul")){
                    configuration.underhaul = new UnderhaulConfiguration();
                    Config underhaul = config.get("underhaul");
                    if(underhaul.hasProperty("fissionSFR")){
                        configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                        Config fissionSFR = underhaul.get("fissionSFR");
                        configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                        configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                        configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                        configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                        configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                        configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                        ConfigList blocks = fissionSFR.get("blocks");
                        underhaulPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                            block.active = blockCfg.get("active");
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
                            if(blockCfg.hasProperty("rules")){
                                ConfigList rules = blockCfg.get("rules");
                                for(Iterator rit = rules.iterator(); rit.hasNext();){
                                    Config ruleCfg = (Config)rit.next();
                                    block.rules.add(readUnderRule(ruleCfg));
                                }
                            }
                            configuration.underhaul.fissionSFR.allBlocks.add(block);configuration.underhaul.fissionSFR.blocks.add(block);
                        }
                        for(multiblock.configuration.underhaul.fissionsfr.PlacementRule rule : underhaulPostLoadMap.keySet()){
                            byte index = underhaulPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.blockType = multiblock.configuration.underhaul.fissionsfr.PlacementRule.BlockType.AIR;
                            }else{
                                rule.block = configuration.underhaul.fissionSFR.allBlocks.get(index-1);
                            }
                        }
                        ConfigList fuels = fissionSFR.get("fuels");
                        for(Iterator fit = fuels.iterator(); fit.hasNext();){
                            Config fuelCfg = (Config)fit.next();
                            multiblock.configuration.underhaul.fissionsfr.Fuel fuel = new multiblock.configuration.underhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("power"), fuelCfg.get("heat"), fuelCfg.get("time"));
                            configuration.underhaul.fissionSFR.allFuels.add(fuel);configuration.underhaul.fissionSFR.fuels.add(fuel);
                        }
                    }
                }
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                if(config.hasProperty("overhaul")){
                    configuration.overhaul = new OverhaulConfiguration();
                    Config overhaul = config.get("overhaul");
                    //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                    if(overhaul.hasProperty("fissionSFR")){
                        configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                        Config fissionSFR = overhaul.get("fissionSFR");
                        configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                        configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                        configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                        configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                        configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                        configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                        ConfigList blocks = fissionSFR.get("blocks");
                        overhaulSFRPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
                            if(blockCfg.hasProperty("closedTexture")){
                                ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                int size = (int) closedTexture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)closedTexture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setClosedTexture(image);
                            }
                            if(blockCfg.hasProperty("rules")){
                                ConfigList rules = blockCfg.get("rules");
                                for(Iterator rit = rules.iterator(); rit.hasNext();){
                                    Config ruleCfg = (Config)rit.next();
                                    block.rules.add(readOverSFRRule(ruleCfg));
                                }
                            }
                            configuration.overhaul.fissionSFR.allBlocks.add(block);configuration.overhaul.fissionSFR.blocks.add(block);
                        }
                        for(multiblock.configuration.overhaul.fissionsfr.PlacementRule rule : overhaulSFRPostLoadMap.keySet()){
                            byte index = overhaulSFRPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                            }else{
                                rule.block = configuration.overhaul.fissionSFR.allBlocks.get(index-1);
                            }
                        }
                        ConfigList fuels = fissionSFR.get("fuels");
                        for(Iterator fit = fuels.iterator(); fit.hasNext();){
                            Config fuelCfg = (Config)fit.next();
                            multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                            configuration.overhaul.fissionSFR.allFuels.add(fuel);configuration.overhaul.fissionSFR.fuels.add(fuel);
                        }
                        ConfigList sources = fissionSFR.get("sources");
                        for(Iterator sit = sources.iterator(); sit.hasNext();){
                            Config sourceCfg = (Config)sit.next();
                            multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                            configuration.overhaul.fissionSFR.allSources.add(source);configuration.overhaul.fissionSFR.sources.add(source);
                        }
                        ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                        for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                            Config irradiatorRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                            configuration.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                        }
                        ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                        for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                            Config coolantRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                            configuration.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                    if(overhaul.hasProperty("fissionMSR")){
                        configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                        Config fissionMSR = overhaul.get("fissionMSR");
                        configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                        configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                        configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                        configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                        configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                        configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                        ConfigList blocks = fissionMSR.get("blocks");
                        overhaulMSRPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                            block.cooling = blockCfg.get("cooling", 0);
                            block.input = blockCfg.get("input");
                            block.output = blockCfg.get("output");
                            block.cluster = blockCfg.get("cluster", false);
                            block.createCluster = blockCfg.get("createCluster", false);
                            block.conductor = blockCfg.get("conductor", false);
                            block.fuelVessel = blockCfg.get("fuelVessel", false);
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
                            if(blockCfg.hasProperty("closedTexture")){
                                ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                int size = (int) closedTexture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)closedTexture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setClosedTexture(image);
                            }
                            if(blockCfg.hasProperty("rules")){
                                ConfigList rules = blockCfg.get("rules");
                                for(Iterator rit = rules.iterator(); rit.hasNext();){
                                    Config ruleCfg = (Config)rit.next();
                                    block.rules.add(readOverMSRRule(ruleCfg));
                                }
                            }
                            configuration.overhaul.fissionMSR.allBlocks.add(block);configuration.overhaul.fissionMSR.blocks.add(block);
                        }
                        for(multiblock.configuration.overhaul.fissionmsr.PlacementRule rule : overhaulMSRPostLoadMap.keySet()){
                            byte index = overhaulMSRPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                            }else{
                                rule.block = configuration.overhaul.fissionMSR.allBlocks.get(index-1);
                            }
                        }
                        ConfigList fuels = fissionMSR.get("fuels");
                        for(Iterator fit = fuels.iterator(); fit.hasNext();){
                            Config fuelCfg = (Config)fit.next();
                            multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                            configuration.overhaul.fissionMSR.allFuels.add(fuel);configuration.overhaul.fissionMSR.fuels.add(fuel);
                        }
                        ConfigList sources = fissionMSR.get("sources");
                        for(Iterator sit = sources.iterator(); sit.hasNext();){
                            Config sourceCfg = (Config)sit.next();
                            multiblock.configuration.overhaul.fissionmsr.Source source = new multiblock.configuration.overhaul.fissionmsr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                            configuration.overhaul.fissionMSR.allSources.add(source);configuration.overhaul.fissionMSR.sources.add(source);
                        }
                        ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                        for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                            Config irradiatorRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                            configuration.overhaul.fissionMSR.allIrradiatorRecipes.add(irrecipe);configuration.overhaul.fissionMSR.irradiatorRecipes.add(irrecipe);
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Turbine Configuration">
                    if(overhaul.hasProperty("turbine")){
                        configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
                        Config turbine = overhaul.get("turbine");
                        configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                        configuration.overhaul.turbine.minLength = turbine.get("minLength");
                        configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                        configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                        configuration.overhaul.turbine.throughputEfficiencyLeniency = turbine.get("throughputEfficiencyLeniency");
                        configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                        configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
                        ConfigList coils = turbine.get("coils");
                        overhaulTurbinePostLoadMap.clear();
                        for(Iterator bit = coils.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.turbine.Coil coil = new multiblock.configuration.overhaul.turbine.Coil(blockCfg.get("name"));
                            coil.bearing = blockCfg.get("bearing", false);
                            coil.connector = blockCfg.get("connector", false);
                            coil.efficiency = blockCfg.get("efficiency");
                            if(blockCfg.hasProperty("texture")){
                                ConfigNumberList texture = blockCfg.get("texture");
                                int size = (int) texture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                coil.setTexture(image);
                            }
                            if(blockCfg.hasProperty("rules")){
                                ConfigList rules = blockCfg.get("rules");
                                for(Iterator rit = rules.iterator(); rit.hasNext();){
                                    Config ruleCfg = (Config)rit.next();
                                    coil.rules.add(readOverTurbineRule(ruleCfg));
                                }
                            }
                            configuration.overhaul.turbine.allCoils.add(coil);configuration.overhaul.turbine.coils.add(coil);
                        }
                        ConfigList blades = turbine.get("blades");
                        for(Iterator bit = blades.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.turbine.Blade blade = new multiblock.configuration.overhaul.turbine.Blade(blockCfg.get("name"));
                            blade.expansion = blockCfg.get("expansion");
                            blade.efficiency = blockCfg.get("efficiency");
                            blade.stator = blockCfg.get("stator");
                            if(blockCfg.hasProperty("texture")){
                                ConfigNumberList texture = blockCfg.get("texture");
                                int size = (int) texture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                blade.setTexture(image);
                            }
                            configuration.overhaul.turbine.allBlades.add(blade);configuration.overhaul.turbine.blades.add(blade);
                        }
                        for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                            byte index = overhaulTurbinePostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                            }else{
                                rule.coil = configuration.overhaul.turbine.allCoils.get(index-1);
                            }
                        }
                        ConfigList recipes = turbine.get("recipes");
                        for(Iterator irit = recipes.iterator(); irit.hasNext();){
                            Config recipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(recipeCfg.get("name"), recipeCfg.get("input"), recipeCfg.get("output"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                            configuration.overhaul.turbine.allRecipes.add(recipe);configuration.overhaul.turbine.recipes.add(recipe);
                        }
                    }
//</editor-fold>
                }
//</editor-fold>
                if(config.hasProperty("addons")){
                    ConfigList addons = config.get("addons");
                    for(int i = 0; i<addons.size(); i++){
                        configuration.addons.add(loadAddon(configuration, addons.get(i)));
                    }
                }
                return configuration;
            }
            private Configuration loadAddon(Configuration parent, Config config){
                boolean partial = config.get("partial");
                Configuration configuration;
                if(partial)configuration = new PartialConfiguration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
                else configuration = new Configuration(config.get("name"), config.get("version"), config.get("underhaulVersion"));
                configuration.addon = config.get("addon");
                //<editor-fold defaultstate="collapsed" desc="Underhaul Configuration">
                if(config.hasProperty("underhaul")){
                    configuration.underhaul = new UnderhaulConfiguration();
                    Config underhaul = config.get("underhaul");
                    if(underhaul.hasProperty("fissionSFR")){
                        configuration.underhaul.fissionSFR = new multiblock.configuration.underhaul.fissionsfr.FissionSFRConfiguration();
                        Config fissionSFR = underhaul.get("fissionSFR");
                        configuration.underhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                        configuration.underhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                        configuration.underhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                        configuration.underhaul.fissionSFR.moderatorExtraPower = fissionSFR.get("moderatorExtraPower");
                        configuration.underhaul.fissionSFR.moderatorExtraHeat = fissionSFR.get("moderatorExtraHeat");
                        configuration.underhaul.fissionSFR.activeCoolerRate = fissionSFR.get("activeCoolerRate");
                        ConfigList blocks = fissionSFR.get("blocks");
                        underhaulPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.underhaul.fissionsfr.Block block = new multiblock.configuration.underhaul.fissionsfr.Block(blockCfg.get("name"));
                            block.active = blockCfg.get("active");
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
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
                            byte index = underhaulPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.underhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
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
//</editor-fold>
                //<editor-fold defaultstate="collapsed" desc="Overhaul Configuration">
                if(config.hasProperty("overhaul")){
                    configuration.overhaul = new OverhaulConfiguration();
                    Config overhaul = config.get("overhaul");
                    //<editor-fold defaultstate="collapsed" desc="Fission SFR Configuration">
                    if(overhaul.hasProperty("fissionSFR")){
                        configuration.overhaul.fissionSFR = new multiblock.configuration.overhaul.fissionsfr.FissionSFRConfiguration();
                        Config fissionSFR = overhaul.get("fissionSFR");
                        configuration.overhaul.fissionSFR.minSize = fissionSFR.get("minSize");
                        configuration.overhaul.fissionSFR.maxSize = fissionSFR.get("maxSize");
                        configuration.overhaul.fissionSFR.neutronReach = fissionSFR.get("neutronReach");
                        configuration.overhaul.fissionSFR.coolingEfficiencyLeniency = fissionSFR.get("coolingEfficiencyLeniency");
                        configuration.overhaul.fissionSFR.sparsityPenaltyMult = fissionSFR.get("sparsityPenaltyMult");
                        configuration.overhaul.fissionSFR.sparsityPenaltyThreshold = fissionSFR.get("sparsityPenaltyThreshold");
                        ConfigList blocks = fissionSFR.get("blocks");
                        overhaulSFRPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.fissionsfr.Block block = new multiblock.configuration.overhaul.fissionsfr.Block(blockCfg.get("name"));
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
                            if(blockCfg.hasProperty("closedTexture")){
                                ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                int size = (int) closedTexture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)closedTexture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setClosedTexture(image);
                            }
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
                            byte index = overhaulSFRPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionsfr.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.blockType = multiblock.configuration.overhaul.fissionsfr.PlacementRule.BlockType.AIR;
                            }else{
                                rule.block = parent.overhaul.fissionSFR.allBlocks.get(index-1);
                            }
                        }
                        ConfigList fuels = fissionSFR.get("fuels");
                        for(Iterator fit = fuels.iterator(); fit.hasNext();){
                            Config fuelCfg = (Config)fit.next();
                            multiblock.configuration.overhaul.fissionsfr.Fuel fuel = new multiblock.configuration.overhaul.fissionsfr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                            parent.overhaul.fissionSFR.allFuels.add(fuel);configuration.overhaul.fissionSFR.fuels.add(fuel);
                        }
                        ConfigList sources = fissionSFR.get("sources");
                        for(Iterator sit = sources.iterator(); sit.hasNext();){
                            Config sourceCfg = (Config)sit.next();
                            multiblock.configuration.overhaul.fissionsfr.Source source = new multiblock.configuration.overhaul.fissionsfr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                            parent.overhaul.fissionSFR.allSources.add(source);configuration.overhaul.fissionSFR.sources.add(source);
                        }
                        ConfigList irradiatorRecipes = fissionSFR.get("irradiatorRecipes");
                        for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                            Config irradiatorRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                            parent.overhaul.fissionSFR.allIrradiatorRecipes.add(irrecipe);configuration.overhaul.fissionSFR.irradiatorRecipes.add(irrecipe);
                        }
                        ConfigList coolantRecipes = fissionSFR.get("coolantRecipes");
                        for(Iterator irit = coolantRecipes.iterator(); irit.hasNext();){
                            Config coolantRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionsfr.CoolantRecipe coolRecipe = new multiblock.configuration.overhaul.fissionsfr.CoolantRecipe(coolantRecipeCfg.get("name"), coolantRecipeCfg.get("input"), coolantRecipeCfg.get("output"), coolantRecipeCfg.get("heat"), coolantRecipeCfg.get("outputRatio"));
                            parent.overhaul.fissionSFR.allCoolantRecipes.add(coolRecipe);configuration.overhaul.fissionSFR.coolantRecipes.add(coolRecipe);
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Fission MSR Configuration">
                    if(overhaul.hasProperty("fissionMSR")){
                        configuration.overhaul.fissionMSR = new multiblock.configuration.overhaul.fissionmsr.FissionMSRConfiguration();
                        Config fissionMSR = overhaul.get("fissionMSR");
                        configuration.overhaul.fissionMSR.minSize = fissionMSR.get("minSize");
                        configuration.overhaul.fissionMSR.maxSize = fissionMSR.get("maxSize");
                        configuration.overhaul.fissionMSR.neutronReach = fissionMSR.get("neutronReach");
                        configuration.overhaul.fissionMSR.coolingEfficiencyLeniency = fissionMSR.get("coolingEfficiencyLeniency");
                        configuration.overhaul.fissionMSR.sparsityPenaltyMult = fissionMSR.get("sparsityPenaltyMult");
                        configuration.overhaul.fissionMSR.sparsityPenaltyThreshold = fissionMSR.get("sparsityPenaltyThreshold");
                        ConfigList blocks = fissionMSR.get("blocks");
                        overhaulMSRPostLoadMap.clear();
                        for(Iterator bit = blocks.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.fissionmsr.Block block = new multiblock.configuration.overhaul.fissionmsr.Block(blockCfg.get("name"));
                            block.cooling = blockCfg.get("cooling", 0);
                            block.input = blockCfg.get("input");
                            block.output = blockCfg.get("output");
                            block.cluster = blockCfg.get("cluster", false);
                            block.createCluster = blockCfg.get("createCluster", false);
                            block.conductor = blockCfg.get("conductor", false);
                            block.fuelVessel = blockCfg.get("fuelVessel", false);
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
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setTexture(image);
                            }
                            if(blockCfg.hasProperty("closedTexture")){
                                ConfigNumberList closedTexture = blockCfg.get("closedTexture");
                                int size = (int) closedTexture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)closedTexture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                block.setClosedTexture(image);
                            }
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
                            byte index = overhaulMSRPostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.fissionmsr.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.blockType = multiblock.configuration.overhaul.fissionmsr.PlacementRule.BlockType.AIR;
                            }else{
                                rule.block = parent.overhaul.fissionMSR.allBlocks.get(index-1);
                            }
                        }
                        ConfigList fuels = fissionMSR.get("fuels");
                        for(Iterator fit = fuels.iterator(); fit.hasNext();){
                            Config fuelCfg = (Config)fit.next();
                            multiblock.configuration.overhaul.fissionmsr.Fuel fuel = new multiblock.configuration.overhaul.fissionmsr.Fuel(fuelCfg.get("name"), fuelCfg.get("efficiency"), fuelCfg.get("heat"), fuelCfg.get("time"), fuelCfg.get("criticality"), fuelCfg.get("selfPriming"));
                            parent.overhaul.fissionMSR.allFuels.add(fuel);configuration.overhaul.fissionMSR.fuels.add(fuel);
                        }
                        ConfigList sources = fissionMSR.get("sources");
                        for(Iterator sit = sources.iterator(); sit.hasNext();){
                            Config sourceCfg = (Config)sit.next();
                            multiblock.configuration.overhaul.fissionmsr.Source source = new multiblock.configuration.overhaul.fissionmsr.Source(sourceCfg.get("name"), sourceCfg.get("efficiency"));
                            parent.overhaul.fissionMSR.allSources.add(source);configuration.overhaul.fissionMSR.sources.add(source);
                        }
                        ConfigList irradiatorRecipes = fissionMSR.get("irradiatorRecipes");
                        for(Iterator irit = irradiatorRecipes.iterator(); irit.hasNext();){
                            Config irradiatorRecipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe irrecipe = new multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe(irradiatorRecipeCfg.get("name"), irradiatorRecipeCfg.get("efficiency"), irradiatorRecipeCfg.get("heat"));
                            parent.overhaul.fissionMSR.allIrradiatorRecipes.add(irrecipe);configuration.overhaul.fissionMSR.irradiatorRecipes.add(irrecipe);
                        }
                    }
//</editor-fold>
                    //<editor-fold defaultstate="collapsed" desc="Turbine Configuration">
                    if(overhaul.hasProperty("turbine")){
                        configuration.overhaul.turbine = new multiblock.configuration.overhaul.turbine.TurbineConfiguration();
                        Config turbine = overhaul.get("turbine");
                        configuration.overhaul.turbine.minWidth = turbine.get("minWidth");
                        configuration.overhaul.turbine.minLength = turbine.get("minLength");
                        configuration.overhaul.turbine.maxSize = turbine.get("maxSize");
                        configuration.overhaul.turbine.fluidPerBlade = turbine.get("fluidPerBlade");
                        configuration.overhaul.turbine.throughputEfficiencyLeniency = turbine.get("throughputEfficiencyLeniency");
                        configuration.overhaul.turbine.throughputFactor = turbine.get("throughputFactor");
                        configuration.overhaul.turbine.powerBonus = turbine.get("powerBonus");
                        ConfigList coils = turbine.get("coils");
                        overhaulTurbinePostLoadMap.clear();
                        for(Iterator bit = coils.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.turbine.Coil coil = new multiblock.configuration.overhaul.turbine.Coil(blockCfg.get("name"));
                            coil.bearing = blockCfg.get("bearing", false);
                            coil.connector = blockCfg.get("connector", false);
                            coil.efficiency = blockCfg.get("efficiency");
                            if(blockCfg.hasProperty("texture")){
                                ConfigNumberList texture = blockCfg.get("texture");
                                int size = (int) texture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                coil.setTexture(image);
                            }
                            if(blockCfg.hasProperty("rules")){
                                ConfigList rules = blockCfg.get("rules");
                                for(Iterator rit = rules.iterator(); rit.hasNext();){
                                    Config ruleCfg = (Config)rit.next();
                                    coil.rules.add(readOverTurbineRule(ruleCfg));
                                }
                            }
                            parent.overhaul.turbine.allCoils.add(coil);configuration.overhaul.turbine.coils.add(coil);
                        }
                        ConfigList blades = turbine.get("blades");
                        for(Iterator bit = blades.iterator(); bit.hasNext();){
                            Config blockCfg = (Config)bit.next();
                            multiblock.configuration.overhaul.turbine.Blade blade = new multiblock.configuration.overhaul.turbine.Blade(blockCfg.get("name"));
                            blade.expansion = blockCfg.get("expansion");
                            blade.efficiency = blockCfg.get("efficiency");
                            blade.stator = blockCfg.get("stator");
                            if(blockCfg.hasProperty("texture")){
                                ConfigNumberList texture = blockCfg.get("texture");
                                int size = (int) texture.get(0);
                                BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                                int index = 1;
                                for(int x = 0; x<image.getWidth(); x++){
                                    for(int y = 0; y<image.getHeight(); y++){
                                        Color color = new Color((int)texture.get(index));
                                        image.setRGB(x, y, color.getRGB());
                                        index++;
                                    }
                                }
                                blade.setTexture(image);
                            }
                            parent.overhaul.turbine.allBlades.add(blade);configuration.overhaul.turbine.blades.add(blade);
                        }
                        for(multiblock.configuration.overhaul.turbine.PlacementRule rule : overhaulTurbinePostLoadMap.keySet()){
                            byte index = overhaulTurbinePostLoadMap.get(rule);
                            if(index==0){
                                if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.AXIAL_GROUP;
                                if(rule.ruleType==multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN)rule.ruleType=multiblock.configuration.overhaul.turbine.PlacementRule.RuleType.BETWEEN_GROUP;
                                rule.coilType = multiblock.configuration.overhaul.turbine.PlacementRule.CoilType.CASING;
                            }else{
                                rule.coil = parent.overhaul.turbine.allCoils.get(index-1);
                            }
                        }
                        ConfigList recipes = turbine.get("recipes");
                        for(Iterator irit = recipes.iterator(); irit.hasNext();){
                            Config recipeCfg = (Config)irit.next();
                            multiblock.configuration.overhaul.turbine.Recipe recipe = new multiblock.configuration.overhaul.turbine.Recipe(recipeCfg.get("name"), recipeCfg.get("input"), recipeCfg.get("output"), recipeCfg.get("power"), recipeCfg.get("coefficient"));
                            parent.overhaul.turbine.allRecipes.add(recipe);configuration.overhaul.turbine.recipes.add(recipe);
                        }
                    }
//</editor-fold>
                }
//</editor-fold>
                if(config.hasProperty("addons")){
                    ConfigList addons = config.get("addons");
                    for(int i = 0; i<addons.size(); i++){
                        configuration.addons.add(loadAddon(configuration, addons.get(i)));
                    }
                }
                return configuration;
            }
        });// .ncpf version 5
    }
    public static NCPFFile read(InputStreamProvider provider){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream());
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFFile read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}