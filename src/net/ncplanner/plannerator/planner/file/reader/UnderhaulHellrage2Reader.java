package net.ncplanner.plannerator.planner.file.reader;
import java.io.InputStream;
import net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Fuel;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.file.LegacyNCPFFile;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
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
    public synchronized LegacyNCPFFile read(InputStream in, RecoveryHandler recovery){
        JSON.JSONObject hellrage = JSON.parse(in);
        JSON.JSONObject dims = hellrage.getJSONObject("InteriorDimensions");
        JSON.JSONObject usedFuel = hellrage.getJSONObject("UsedFuel");
        String fuelName = usedFuel.getString("Name");
        Fuel fuel = recovery.recoverUnderhaulSFRFuel(fuelName, usedFuel.getFloat("BaseHeat"), usedFuel.getFloat("BasePower"));
        UnderhaulSFR sfr = new UnderhaulSFR(null, dims.getInt("X"), dims.getInt("Y"), dims.getInt("Z"), fuel);
        JSON.JSONObject compressedReactor = hellrage.getJSONObject("CompressedReactor");
        for(String name : compressedReactor.keySet()){
            net.ncplanner.plannerator.multiblock.configuration.underhaul.fissionsfr.Block block = recovery.recoverUnderhaulSFRBlock(name);
            JSON.JSONArray blocks = compressedReactor.getJSONArray(name);
            for(Object blok : blocks){
                JSON.JSONObject blokLoc = (JSON.JSONObject) blok;
                int x = blokLoc.getInt("X");
                int y = blokLoc.getInt("Y");
                int z = blokLoc.getInt("Z");
                sfr.setBlockExact(x, y, z, new net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block(Core.configuration, x, y, z, block));
            }
        }
        LegacyNCPFFile file = new LegacyNCPFFile();
        sfr.buildDefaultCasingOnConvert();
        file.multiblocks.add(sfr);
        return file;
    }
}