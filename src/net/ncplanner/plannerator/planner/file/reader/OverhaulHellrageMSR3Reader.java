package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class OverhaulHellrageMSR3Reader implements FormatReader{
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
        return major==2&&minor==0&&build==30;
    }
    @Override
    public synchronized LegacyNCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = StringUtil.split(dimS, ",");
        OverhaulMSR msr = new OverhaulMSR(null, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
                if(block.heater&&!block.allRecipes.isEmpty())msr.getBlock(x,y,z).recipe = block.allRecipes.get(0);
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                if(blok.conductor)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block reflector = null;
        float best = 0;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
            if(blok.reflector&&blok.reflectorReflectivity>best){
                reflector = blok;
                best = blok.reflectorReflectivity;
            }
        }
        if(reflector==null)throw new IllegalArgumentException("Configuration has no reflectors!");
        JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
        for(Object blok : reflectors){
            String blokLoc = (String) blok;
            String[] blockLoc = StringUtil.split(blokLoc, ",");
            int x = Integer.parseInt(blockLoc[0]);
            int y = Integer.parseInt(blockLoc[1]);
            int z = Integer.parseInt(blockLoc[2]);
            msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, reflector));
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
            if(blok.fuelVessel)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Configuration has no fuel vessels!");
        JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
        HashMap<net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block> sources = new HashMap<>();
        for(String name : fuelVessels.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            BlockRecipe fuel = recovery.recoverOverhaulMSRFuel(vessel, fuelName);
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block src = null;
            if(hasSource){
                String sourceName = fuelSettings[2];
                src = recovery.recoverOverhaulMSRBlock(sourceName);
            }
            JSON.JSONArray array = fuelVessels.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, vessel));
                msr.getBlock(x, y, z).recipe = fuel;
                if(hasSource)sources.put(msr.getBlock(x, y, z), src);
            }
        }
        for(net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block key : sources.keySet()){
            key.addNeutronSource(msr, sources.get(key));
        }
        LegacyNCPFFile file = new LegacyNCPFFile();
        msr.buildDefaultCasingOnConvert();
        file.multiblocks.add(msr);
        return file;
    }
}