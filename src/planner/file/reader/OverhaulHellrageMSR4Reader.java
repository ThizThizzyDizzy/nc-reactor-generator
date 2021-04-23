package planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import planner.Core;
import planner.file.FormatReader;
import planner.file.JSON;
import planner.file.NCPFFile;
public class OverhaulHellrageMSR4Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
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
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        OverhaulMSR msr = new OverhaulMSR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            multiblock.configuration.overhaul.fissionmsr.Block block = null;
            for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("coolant", "").replace("heater", "").replace("liquid", "").equalsIgnoreCase(name.toLowerCase(Locale.ENGLISH).replace("water", "standard").replace(" ", "")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
                if(block.heater&&!block.allRecipes.isEmpty())msr.getBlock(x,y,z).recipe = block.allRecipes.get(0);
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            multiblock.configuration.overhaul.fissionmsr.Block block = null;
            for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").replace("moderator", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
            }
        }
        multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
        for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
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
                msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        multiblock.configuration.overhaul.fissionmsr.Block reflector = null;
        float best = 0;
        for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
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
            msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, reflector));
        }
        multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
        for(multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
            if(blok.fuelVessel)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Unknown block: Fuel Vessel");
        JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
        HashMap<multiblock.overhaul.fissionmsr.Block, multiblock.configuration.overhaul.fissionmsr.Block> sources = new HashMap<>();
        for(String name : fuelVessels.keySet()){
            String[] fuelSettings = name.split(";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            BlockRecipe fuel = null;
            for(BlockRecipe feul : vessel.allRecipes){
                for(String nam : feul.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.substring(4).replace(" ", "")))fuel = feul;
            }
            if(fuelName.startsWith("[F4]"))fuelName = fuelName.substring(4)+" Fluoride";
            for(BlockRecipe feul : vessel.allRecipes){
                for(String nam : feul.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace(" ", "").equalsIgnoreCase(fuelName.replace(" ", "")))fuel = feul;
            }
            if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
            multiblock.configuration.overhaul.fissionmsr.Block src = null;
            if(hasSource){
                String sourceName = fuelSettings[2];
                for(multiblock.configuration.overhaul.fissionmsr.Block scr : Core.configuration.overhaul.fissionMSR.allBlocks){
                    if(!scr.source)continue;
                    for(String nam : scr.getLegacyNames())if(nam.equalsIgnoreCase(sourceName))src = scr;
                }
                if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
            }
            JSON.JSONArray array = fuelVessels.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, vessel));
                msr.getBlock(x, y, z).recipe = fuel;
                if(hasSource)sources.put(msr.getBlock(x, y, z), src);
            }
        }
        for(multiblock.overhaul.fissionmsr.Block key : sources.keySet()){
            key.addNeutronSource(msr, sources.get(key));
        }
        NCPFFile file = new NCPFFile();
        msr.buildDefaultCasingOnConvert();
        file.multiblocks.add(msr);
        return file;
    }
}