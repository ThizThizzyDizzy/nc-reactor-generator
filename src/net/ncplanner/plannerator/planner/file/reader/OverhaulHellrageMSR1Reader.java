package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
public class OverhaulHellrageMSR1Reader implements FormatReader{
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
        return major==2&&minor==0&&build>=1&&build<=6;
    }
    @Override
    public synchronized Project read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = StringUtil.split(dimS, ",");
        OverhaulMSRDesign msr = new OverhaulMSRDesign(null, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
        JSON.JSONObject heatSinks = hellrage.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = heatSinks.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.design[x][y][z] = block;
                msr.heaterRecipes[x][y][z] = block.heaterRecipes.get(0);
            }
        }
        JSON.JSONObject moderators = hellrage.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.design[x][y][z] = block;
            }
        }
        JSON.JSONArray conductors = hellrage.getJSONArray("Conductors");
        if(conductors!=null){
            Block conductor = null;
            for(Block blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
                if(blok.conductor!=null)conductor = blok;
            }
            if(conductor==null)throw new IllegalArgumentException("Configuation has no conductors!");
            for(Object blok : conductors){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
                msr.design[x][y][z] = conductor;
            }
        }
        Block vessel = null;
        for(Block blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
            if(blok.fuelVessel!=null)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Configuration has no fuel vessels!");
        JSON.JSONObject fuelVessels = hellrage.getJSONObject("FuelCells");
        HashMap<int[], Block> sources = new HashMap<>();
        for(String name : fuelVessels.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            Fuel fuel = recovery.recoverOverhaulMSRFuel(vessel, fuelName);
            Block src = null;
            float highest = 0;
            for(Block scr : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
                if(scr.neutronSource!=null&&scr.neutronSource.efficiency>highest){
                    src = scr;
                    highest = src.neutronSource.efficiency;
                }
            }
            if(src==null)throw new IllegalArgumentException("Configuration has no neutron sources!");
            JSON.JSONArray array = fuelVessels.getJSONArray(name);
            for(Object blok : array){
                String blokLoc = (String) blok;
                String[] blockLoc = StringUtil.split(blokLoc, ",");
                int x = Integer.parseInt(blockLoc[0]);
                int y = Integer.parseInt(blockLoc[1]);
                int z = Integer.parseInt(blockLoc[2]);
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