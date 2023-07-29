package net.ncplanner.plannerator.planner.file.reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
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
    public synchronized Project read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject data = hellrage.getJSONObject("Data");
        JSON.JSONObject dims = data.getJSONObject("InteriorDimensions");
        String coolantRecipeName = data.getString("CoolantRecipeName");
        OverhaulSFRDesign sfr = new OverhaulSFRDesign(Core.project, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        sfr.coolantRecipe = recovery.recoverOverhaulSFRCoolantRecipe(coolantRecipeName);
        JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            BlockElement block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        JSON.JSONObject moderators = data.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            BlockElement block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        JSON.JSONArray conductors = data.getJSONArray("Conductors");
        if(conductors!=null){
            BlockElement conductor = null;
            for(BlockElement blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
                if(blok.conductor!=null)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                    JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                    int x = blockLoc.getInt("X");
                    int y = blockLoc.getInt("Y");
                    int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = conductor;
            }
        }
        JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
        for(String name : reflectors.keySet()){
            BlockElement block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = reflectors.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
        for(String name : neutronShields.keySet()){
            BlockElement block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = neutronShields.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        BlockElement irradiator = null;
        for(BlockElement blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
            if(blok.irradiator!=null)irradiator = blok;
        }
        if(irradiator==null)throw new IllegalArgumentException("Configuration has no irradiators!");
        JSON.JSONObject irradiators = data.getJSONObject("Irradiators");
        for(String name : irradiators.keySet()){
            IrradiatorRecipe irrecipe = null;
            try{
                JSON.JSONObject recipe = JSON.parse(name);
                for(IrradiatorRecipe irr : irradiator.irradiatorRecipes){
                    if(irr.stats.heat==recipe.getFloat("HeatPerFlux")&&irr.stats.efficiency==recipe.getFloat("EfficiencyMultiplier"))irrecipe = irr;
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
                sfr.design[x][y][z] = irradiator;
                sfr.irradiatorRecipes[x][y][z] = irrecipe;
            }
        }
        BlockElement cell = null;
        for(BlockElement blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
            if(blok.fuelCell!=null)cell = blok;
        }
        if(cell==null)throw new IllegalArgumentException("Configuration has no fuel cells!");
        JSON.JSONObject fuelCells = data.getJSONObject("FuelCells");
        HashMap<int[], BlockElement> sources = new HashMap<>();
        for(String name : fuelCells.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            Fuel fuel = recovery.recoverOverhaulSFRFuel(cell, fuelName);
            BlockElement src = null;
            if(hasSource){
                String sourceName = fuelSettings[2];
                src = recovery.recoverOverhaulSFRBlock(sourceName);
            }
            JSON.JSONArray array = fuelCells.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = cell;
                sfr.fuels[x][y][z] = fuel;
                if(hasSource)sources.put(new int[]{x,y,z}, src);
            }
        }
        for(int[] key : sources.keySet()){
            LegacyNeutronSourceHandler.addNeutronSource(sfr, key[0], key[1], key[2], sources.get(key));
        }
        Project file = new Project();
        file.designs.add(sfr);
        return file;
    }
}