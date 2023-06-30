package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class OverhaulHellrageSFR1Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
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
    public synchronized LegacyNCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = StringUtil.split(dimS, ",");
        OverhaulSFR sfr = new OverhaulSFR(null, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), Core.configuration.overhaul.fissionSFR.allCoolantRecipes.get(0));
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                if(blok.conductor)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block cell = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            if(blok.fuelCell)cell = blok;
        }
        if(cell==null)throw new IllegalArgumentException("Configuration has no fuel cells!");
        JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
        HashMap<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block> sources = new HashMap<>();
        for(String name : fuelCells.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean source = Boolean.parseBoolean(fuelSettings[1]);
            BlockRecipe fuel = recovery.recoverOverhaulSFRFuel(cell, fuelName);
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block src = null;
            float highest = 0;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block scr : Core.configuration.overhaul.fissionSFR.allBlocks){
                if(scr.source&&scr.sourceEfficiency>highest){
                    src = scr;
                    highest = src.sourceEfficiency;
                }
            }
            if(src==null)throw new IllegalArgumentException("Configuration has no neutron sources!");
            JSON.JSONArray array = fuelCells.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, cell));
                sfr.getBlock(x, y, z).recipe = fuel;
                if(source)sources.put(sfr.getBlock(x, y, z), src);
            }
        }
        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block key : sources.keySet()){
            key.addNeutronSource(sfr, sources.get(key));
        }
        LegacyNCPFFile file = new LegacyNCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}