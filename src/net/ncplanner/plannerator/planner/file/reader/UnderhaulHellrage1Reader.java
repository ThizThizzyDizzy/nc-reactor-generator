package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement;
import net.ncplanner.plannerator.planner.ncpf.design.UnderhaulSFRDesign;
public class UnderhaulHellrage1Reader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> in){
        JSON.JSONObject hellrage = JSON.parse(in.get());
        JSON.JSONObject saveVersion = hellrage.getJSONObject("SaveVersion");
        int major = saveVersion.getInt("Major");
        int minor = saveVersion.getInt("Minor");
        int build = saveVersion.getInt("Build");
        return major==1&&minor==2&&build>=5&&build<=22;
    }
    @Override
    public synchronized Project read(Supplier<InputStream> in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in.get());
        String dimS = hellrage.getString("InteriorDimensions");
        String[] dims = StringUtil.split(dimS, ",");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        UnderhaulSFRDesign sfr = new UnderhaulSFRDesign(Core.project, Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
        sfr.fuel = recovery.recoverUnderhaulSFRFuel(fuelName, usedFuel.getFloat("BaseHeat"), usedFuel.getFloat("BasePower"));
        JSON.JSONArray compressedReactor = hellrage.getJSONArray("CompressedReactor");
        for(Object o : compressedReactor){
            JSON.JSONObject ob = (JSON.JSONObject) o;
            for(String name : ob.keySet()){
                BlockElement block = recovery.recoverUnderhaulSFRBlock(name);
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
        Project file = new Project(Core.project);
        file.designs.add(sfr);
        return file;
    }
}