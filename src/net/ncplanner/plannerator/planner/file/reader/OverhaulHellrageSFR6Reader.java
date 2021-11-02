package net.ncplanner.plannerator.planner.file.reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.NCPFFile;
public class OverhaulHellrageSFR6Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        JSON.JSONObject data = hellrage.getJSONObject("Data");
        JSON.JSONObject fuelCells = data.getJSONObject("FuelCells");
        for(String name : fuelCells.keySet()){
            if(name.startsWith("[F4]"))return false;//that's an MSR!
        }
        return major==2&&minor==1&&build>=1;//&&build<=7;
    }
    @Override
    public synchronized NCPFFile read(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject data = hellrage.getJSONObject("Data");
        JSON.JSONObject dims = data.getJSONObject("InteriorDimensions");
        String coolantRecipeName = data.getString("CoolantRecipeName");
        CoolantRecipe coolantRecipe = null;
        for(CoolantRecipe recipe : Core.configuration.overhaul.fissionSFR.allCoolantRecipes){
            for(String nam : recipe.getLegacyNames())if(nam.equalsIgnoreCase(coolantRecipeName))coolantRecipe = recipe;
        }
        if(coolantRecipe==null)throw new IllegalArgumentException("Unknown coolant recipe: "+coolantRecipeName);
        OverhaulSFR sfr = new OverhaulSFR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), coolantRecipe);
        JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "heatsink", "liquid").equalsIgnoreCase(StringUtil.superRemove(StringUtil.toLowerCase(name), " ")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONObject moderators = data.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "moderator").equalsIgnoreCase(StringUtil.superRemove(name, " ")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block conductor = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            for(String nam : blok.getLegacyNames())if(nam.equalsIgnoreCase("conductor"))conductor = blok;
        }
        if(conductor==null)throw new IllegalArgumentException("Unknown block: Conductor");
        JSON.JSONArray conductors = data.getJSONArray("Conductors");
        if(conductors!=null){
            for(Object blok : conductors){
                    JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                    int x = blockLoc.getInt("X");
                    int y = blockLoc.getInt("Y");
                    int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, conductor));
            }
        }
        JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
        for(String name : reflectors.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "reflector").equalsIgnoreCase(StringUtil.superRemove(name, " ")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = reflectors.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
        for(String name : neutronShields.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block block = null;
            for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ", "neutronshield", "shield").equalsIgnoreCase(StringUtil.superRemove(name, " ")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray array = neutronShields.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block irradiator = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            if(blok.irradiator)irradiator = blok;
        }
        if(irradiator==null)throw new IllegalArgumentException("Unknown block: Irradiator");
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
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, irradiator));
                sfr.getBlock(x, y, z).recipe = irrecipe;
            }
        }
        net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block cell = null;
        for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block blok : Core.configuration.overhaul.fissionSFR.allBlocks){
            if(blok.fuelCell)cell = blok;
        }
        if(cell==null)throw new IllegalArgumentException("Unknown block: Fuel Cell");
        JSON.JSONObject fuelCells = data.getJSONObject("FuelCells");
        HashMap<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block> sources = new HashMap<>();
        for(String name : fuelCells.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            BlockRecipe fuel = null;
            for(BlockRecipe feul : cell.allRecipes){
                for(String nam : feul.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(fuelName.substring(4), " ")))fuel = feul;
            }
            if(fuelName.startsWith("[OX]"))fuelName = fuelName.substring(4)+" Oxide";
            if(fuelName.startsWith("[NI]"))fuelName = fuelName.substring(4)+" Nitride";
            if(fuelName.startsWith("[ZA]"))fuelName = fuelName.substring(4)+"-Zirconium Alloy";
            for(BlockRecipe feul : cell.allRecipes){
                for(String nam : feul.getLegacyNames())if(StringUtil.superRemove(StringUtil.toLowerCase(nam), " ").equalsIgnoreCase(StringUtil.superRemove(fuelName, " ")))fuel = feul;
            }
            if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+name);
            net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block src = null;
            if(hasSource){
                String sourceName = fuelSettings[2];
                if(sourceName.equals("Self"))hasSource = false;
                else{
                    for(net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.Block scr : Core.configuration.overhaul.fissionSFR.allBlocks){
                        if(!scr.source)continue;
                        for(String nam : scr.getLegacyNames())if(nam.equalsIgnoreCase(sourceName))src = scr;
                    }
                    if(src==null)throw new IllegalArgumentException("Unknown source: "+name);
                }
            }
            JSON.JSONArray array = fuelCells.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block(Core.configuration, x, y, z, cell));
                sfr.getBlock(x, y, z).recipe = fuel;
                if(hasSource)sources.put(sfr.getBlock(x, y, z), src);
            }
        }
        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block key : sources.keySet()){
            key.addNeutronSource(sfr, sources.get(key));
        }
        NCPFFile file = new NCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}