package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulSFRDesign;
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
    public synchronized NCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        String coolantRecipeName = hellrage.getString("CoolantRecipeName");
        OverhaulSFRDesign sfr = new OverhaulSFRDesign(Core.project, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        sfr.coolantRecipe = recovery.recoverOverhaulSFRCoolantRecipe(coolantRecipeName);
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            Block block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            Block block = recovery.recoverOverhaulSFRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                sfr.design[x][y][z] = block;
            }
        }
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            Block conductor = null;
            for(Block blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
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
        Block reflector = null;
        float best = 0;
        for(Block blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
            if(blok.reflector!=null&&blok.reflector.reflectivity>best){
                reflector = blok;
                best = blok.reflector.reflectivity;
            }
        }
        if(reflector==null)throw new IllegalArgumentException("Configuration has no reflectors!");
        JSON.JSONArray reflectors = hellrage.getJSONArray("Reflectors");
        for(Object blok : reflectors){
            JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
            int x = blockLoc.getInt("X");
            int y = blockLoc.getInt("Y");
            int z = blockLoc.getInt("Z");
            sfr.design[x][y][z] = reflector;
        }
        Block cell = null;
        for(Block blok : Core.project.getConfiguration(OverhaulSFRConfiguration::new).blocks){
            if(blok.fuelCell!=null)cell = blok;
        }
        if(cell==null)throw new IllegalArgumentException("Configuration has no fuel cells!");
        JSON.JSONObject fuelCells = hellrage.getJSONObject("FuelCells");
        HashMap<int[], Block> sources = new HashMap<>();
        for(String name : fuelCells.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            Fuel fuel = recovery.recoverOverhaulSFRFuel(cell, fuelName);
            Block src = null;
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