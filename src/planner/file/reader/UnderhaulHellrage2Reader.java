package planner.file.reader;
import java.io.InputStream;
import java.util.Locale;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.Core;
import planner.file.FormatReader;
import planner.file.JSON;
import planner.file.NCPFFile;
public class UnderhaulHellrage2Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        return major==1&&minor==2&&build>=23;//&&build<=25;
    }
    @Override
    public synchronized NCPFFile read(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        Fuel fuel = null;
        for(Fuel fool : Core.configuration.underhaul.fissionSFR.allFuels){
            for(String nam : fool.getLegacyNames())if(nam.equalsIgnoreCase(fuelName))fuel = fool;
        }
        if(fuel==null){
            for(Fuel fool : Core.configuration.underhaul.fissionSFR.allFuels){
                if(fool.heat==usedFuel.getFloat("BaseHeat")
                        &&fool.power==usedFuel.getFloat("BasePower"))fuel = fool;
            }
        }
        if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+fuelName);
        UnderhaulSFR sfr = new UnderhaulSFR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), fuel);
        JSON.JSONObject compressedReactor = hellrage.getJSONObject("CompressedReactor");
        for(String name : compressedReactor.keySet()){
            multiblock.configuration.underhaul.fissionsfr.Block block = null;
            for(multiblock.configuration.underhaul.fissionsfr.Block blok : Core.configuration.underhaul.fissionSFR.allBlocks){
                for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace("cooler", "").replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
            }
            if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
            JSON.JSONArray blocks = compressedReactor.getJSONArray(name);
            for(Object blok : blocks){
                JSON.JSONObject blokLoc = (JSON.JSONObject) blok;
                int x = blokLoc.getInt("X");
                int y = blokLoc.getInt("Y");
                int z = blokLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        NCPFFile file = new NCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}