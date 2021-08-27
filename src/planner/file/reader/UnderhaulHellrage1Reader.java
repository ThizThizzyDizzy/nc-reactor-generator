package planner.file.reader;
import java.io.InputStream;
import java.util.Locale;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.Core;
import planner.file.FormatReader;
import planner.file.JSON;
import planner.file.NCPFFile;
public class UnderhaulHellrage1Reader implements FormatReader{
    @Override
    public boolean formatMatches(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        return major==1&&minor==2&&build>=5&&build<=22;
    }
    @Override
    public synchronized NCPFFile read(InputStream in){
        JSON.JSONObject hellrage = JSON.parse(in);
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = dimS.split(",");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        Fuel fuel = null;
        for(Fuel fool : Core.configuration.underhaul.fissionSFR.allFuels){
            for(String nam : fool.getLegacyNames())if(nam.equalsIgnoreCase(fuelName))fuel = fool;
        }
        if(fuel==null)throw new IllegalArgumentException("Unknown fuel: "+fuelName);
        UnderhaulSFR sfr = new UnderhaulSFR(null, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]), fuel);
        JSON.JSONArray compressedReactor = hellrage.getJSONArray("CompressedReactor");
        for(Object o : compressedReactor){
            JSON.JSONObject ob = (JSON.JSONObject) o;
            for(String name : ob.keySet()){
                multiblock.configuration.underhaul.fissionsfr.Block block = null;
                for(multiblock.configuration.underhaul.fissionsfr.Block blok : Core.configuration.underhaul.fissionSFR.allBlocks){
                    for(String nam : blok.getLegacyNames())if(nam.toLowerCase(Locale.ENGLISH).replace("cooler", "").replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))block = blok;
                }
                if(block==null)throw new IllegalArgumentException("Unknown block: "+name);
                JSON.JSONArray blocks = ob.getJSONArray(name);
                for(Object blok : blocks){
                    String blokLoc = (String) blok;
                    String[] blockLoc = blokLoc.split(",");
                    int x = Integer.parseInt(blockLoc[0]);
                    int y = Integer.parseInt(blockLoc[1]);
                    int z = Integer.parseInt(blockLoc[2]);
                    sfr.setBlockExact(x, y, z, new multiblock.underhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
                }
            }
        }
        NCPFFile file = new NCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}