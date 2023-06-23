package net.ncplanner.plannerator.planner.file.reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
public class OverhaulHellrageMSR6Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        JSON.JSONObject data = hellrage.getJSONObject("Data");
        JSON.JSONObject fuelVessels = data.getJSONObject("FuelCells");
        for(String name : fuelVessels.keySet()){
            if(!name.startsWith("[F4]"))return false;//that's not an MSR!
        }
        return major==2&&minor==1&&build>=1;//&&build<=7;
    }
    @Override
    public synchronized NCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject data = hellrage.getJSONObject("Data");
        JSON.JSONObject dims = data.getJSONObject("InteriorDimensions");
        OverhaulMSR msr = new OverhaulMSR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
                if(block.heater&&!block.allRecipes.isEmpty())msr.getBlock(x,y,z).recipe = block.allRecipes.get(0);
            }
        }
        JSON.JSONObject moderators = data.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONArray conductors = data.getJSONArray("Conductors");
        if(conductors!=null){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block conductor = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
                if(blok.conductor)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                    JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                    int x = blockLoc.getInt("X");
                    int y = blockLoc.getInt("Y");
                    int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
        for(String name : reflectors.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = reflectors.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
        for(String name : neutronShields.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = neutronShields.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, block));
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block irradiator = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
            if(blok.irradiator)irradiator = blok;
        }
        if(irradiator==null)throw new IllegalArgumentException("Configuration has no irradiators!");
        JSON.JSONObject irradiators = data.getJSONObject("Irradiators");
        for(String name : irradiators.keySet()){
            BlockRecipe irrecipe = null;
            try{
                JSON.JSONObject recipe = JSON.parse(name);
                for(BlockRecipe irr : irradiator.allRecipes){
                    if(irr.irradiatorHeat==recipe.getFloat("HeatPerFlux")&&irr.irradiatorEfficiency==recipe.getFloat("EfficiencyMultiplier"))irrecipe = irr;
                }
            }catch(IOException ex){
                throw new IllegalArgumentException("Invalid irradiator recipe: "+name);
            }
            JSON.JSONArray array = irradiators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, irradiator));
                msr.getBlock(x, y, z).recipe = irrecipe;
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block vessel = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block blok : Core.configuration.overhaul.fissionMSR.allBlocks){
            if(blok.fuelVessel)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Configuration has no fuel vessels!");
        JSON.JSONObject fuelVessels = data.getJSONObject("FuelCells");
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
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block(Core.configuration, x, y, z, vessel));
                msr.getBlock(x, y, z).recipe = fuel;
                if(hasSource)sources.put(msr.getBlock(x, y, z), src);
            }
        }
        for(net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block key : sources.keySet()){
            key.addNeutronSource(msr, sources.get(key));
        }
        NCPFFile file = new NCPFFile();
        msr.buildDefaultCasingOnConvert();
        file.multiblocks.add(msr);
        return file;
    }
}