package net.ncplanner.plannerator.planner.file.reader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import net.ncplanner.plannerator.ncpf.NCPFFile;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulMSRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Block;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.Fuel;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.design.OverhaulMSRDesign;
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
        OverhaulMSRDesign msr = new OverhaulMSRDesign(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"));
        JSON.JSONObject heatSinks = data.getJSONObject("HeatSinks");
        for(String name : heatSinks.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
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
        JSON.JSONObject moderators = data.getJSONObject("Moderators");
        for(String name : moderators.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = moderators.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = block;
            }
        }
        JSON.JSONArray conductors = data.getJSONArray("Conductors");
        if(conductors!=null){
            Block conductor = null;
            for(Block blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
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
        JSON.JSONObject reflectors = data.getJSONObject("Reflectors");
        for(String name : reflectors.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = reflectors.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = block;
            }
        }
        JSON.JSONObject neutronShields = data.getJSONObject("NeutronShields");
        for(String name : neutronShields.keySet()){
            Block block = recovery.recoverOverhaulMSRBlock(name);
            JSON.JSONArray array = neutronShields.getJSONArray(name);
            for(Object blok : array){
                JSON.JSONObject blockLoc = (JSON.JSONObject) blok;
                int x = blockLoc.getInt("X");
                int y = blockLoc.getInt("Y");
                int z = blockLoc.getInt("Z");
                msr.design[x][y][z] = block;
            }
        }
        Block irradiator = null;
        for(Block blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
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
                msr.design[x][y][z] = irradiator;
                msr.irradiatorRecipes[x][y][z] = irrecipe;
            }
        }
        Block vessel = null;
        for(Block blok : Core.project.getConfiguration(OverhaulMSRConfiguration::new).blocks){
            if(blok.fuelVessel!=null)vessel = blok;
        }
        if(vessel==null)throw new IllegalArgumentException("Configuration has no fuel vessels!");
        JSON.JSONObject fuelVessels = data.getJSONObject("FuelCells");
        HashMap<int[], Block> sources = new HashMap<>();
        for(String name : fuelVessels.keySet()){
            String[] fuelSettings = StringUtil.split(name, ";");
            String fuelName = fuelSettings[0];
            boolean hasSource = Boolean.parseBoolean(fuelSettings[1]);
            Fuel fuel = recovery.recoverOverhaulMSRFuel(vessel, fuelName);
            Block src = null;
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