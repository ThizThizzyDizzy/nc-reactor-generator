package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
public class OverhaulHellrageMSR4Reader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> in){
        JSON.JSONObject hellrage = JSON.parse(in.get());
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
    public synchronized Project read(Supplier<InputStream> in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in.get());
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        OverhaulMSRDesign msr = new OverhaulMSRDesign(Core.project, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            BlockElement block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = block;
                msr.heaterRecipes[x][y][z] = block.heaterRecipes.get(0);
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            BlockElement block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = block;
            }
        }
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            BlockElement conductor = null;
            for(BlockElement blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
                if(blok.conductor!=null)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                    JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                    int x = blockLoc.getInt("X");
                    int y = blockLoc.getInt("Y");
                    int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = conductor;
            }
        }
        BlockElement reflector = null;
        float best = 0;
        for(BlockElement blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
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
            msr.design[x][y][z] = reflector;
        }
        BlockElement vessel = null;
        for(BlockElement blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
            if(blok.fuelVessel!=null)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Configuration has no fuel vessels!");
        JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
        HashMap<int[], BlockElement> sources = new HashMap<>();
        for(String name : fuelVessels.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            Fuel fuel = recovery.recoverOverhaulMSRFuel(vessel, fuelName);
            BlockElement src = null;
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
                msr.design[x][y][z] = vessel;
                msr.fuels[x][y][z] = fuel;
                if(hasSource)sources.put(new int[]{x,y,z}, src);
            }
        }
        for(int[] key : sources.keySet()){
            LegacyNeutronSourceHandler.addNeutronSource(msr, key[0], key[1], key[2], sources.get(key));
        }
        Project file = new Project();
        file.designs.add(msr);
        return file;
    }
}