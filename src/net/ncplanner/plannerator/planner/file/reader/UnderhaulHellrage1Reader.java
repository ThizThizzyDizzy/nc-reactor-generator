package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Block;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
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
    public synchronized Project read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = StringUtil.split(dimS, ",");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        UnderhaulSFRDesign sfr = new UnderhaulSFRDesign(null, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
        sfr.fuel = recovery.recoverUnderhaulSFRFuel(fuelName, usedFuel.getFloat("BaseHeat"), usedFuel.getFloat("BasePower"));
        JSON.JSONArray compressedReactor = hellrage.getJSONArray("CompressedReactor");
        for(Object o : compressedReactor){
            JSON.JSONObject ob = (JSON.JSONObject) o;
            for(String name : ob.keySet()){
                Block block = recovery.recoverUnderhaulSFRBlock(name);
                JSON.JSONArray blocks = ob.getJSONArray(name);
                for(Object blok : blocks){
                    String blokLoc = (String) blok;
                    String[] blockLoc = StringUtil.split(blokLoc, ",");
                    int x = Integer.parseInt(blockLoc[0]);
                    int y = Integer.parseInt(blockLoc[1]);
                    int z = Integer.parseInt(blockLoc[2]);
                    sfr.design[x][y][z] = block;
                }
            }
        }
        Project file = new Project();
        file.designs.add(sfr);
        return file;
    }
}