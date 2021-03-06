package planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import planner.Core;
import planner.file.FormatReader;
import planner.file.JSON;
import planner.file.NCPFFile;
public class OverhaulHellrageSFR4Reader implements FormatReader{
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
        return major==2&&minor==0&&build==31;
    }
    @Override
    public synchronized NCPFFile read(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        String coolantRecipeName = hellrage.getString("CoolantRecipeName");
        CoolantRecipe coolantRecipe = null;
        for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
            for(String nam : recipe.getLegacyNames())if(nam.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
        }
        if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
        OverhaulSFR sfr = new OverhaulSFR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), coolantRecipe);
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("heatsink", "").replace("liquid", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
        for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            for(String nam : blok.getLegacyNames())if(nam.equalsIgnoreCase("conductor"))conductor = blok;
        }
        if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            for(Object blok : conductors){
                    JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                    int x = blockLoc.getInt("X");
                    int y = blockLoc.getInt("Y");
                    int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        multiblock.configuration.overhaul.fissionsfr.Block reflector = null;
        float best = 0;
        for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            if(blok.reflector&&blok.reflectorReflectivity>best){
                reflector = blok;
                best = blok.reflectorReflectivity;
            }
        }
        if(reflector==null)throw new IllegalArgumentException("Unknown block: Reflector");
        JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
        for(Object blok : reflectors){
            JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
            int x = blockLoc.getInt("X");
            int y = blockLoc.getInt("Y");
            int z = blockLoc.getInt("Z");
            sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, reflector));
        }
        multiblock.configuration.overhaul.fissionsfr.Block cell = null;
        for(multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            if(blok.fuelCell)cell = blok;
        }
        if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
        JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
        HashMap<multiblock.overhaul.fissionsfr.Block, multiblock.configuration.overhaul.fissionsfr.Block> sources = new HashMap<>();
        for(String name : fuelCells.keySet()){
            String[] fuelSettings = name.split(";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            BlockRecipe fuel = null;
            for(BlockRecipe feul : cell.allRecipes){
                for(String nam : feul.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
            }
            if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
            if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
            if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
            for(BlockRecipe feul : cell.allRecipes){
                for(String nam : feul.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
            }
            if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
            multiblock.configuration.overhaul.fissionsfr.Block src = null;
            if(hasSource){
                String sourceName = fuelSettings[2];
                for(multiblock.configuration.overhaul.fissionsfr.Block scr : Core.configuration.overhaul.fissionSFR.allBlocks){
                    if(!scr.source)continue;
                    for(String nam : scr.getLegacyNames())if(nam.equalsIgnoreCase(sourceName))src = scr;
                }
                if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
            }
            JSON.JSONArray array = fuelCells.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, cell));
                sfr.getBlock(x, y, z).recipe = fuel;
                if(hasSource)sources.put(sfr.getBlock(x, y, z), src);
            }
        }
        for(multiblock.overhaul.fissionsfr.Block key : sources.keySet()){
            key.addNeutronSource(sfr, sources.get(key));
        }
        NCPFFile file = new NCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}