package planner.file;
import common.JSON;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import planner.multiblock.Multiblock;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
import planner.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import simplelibrary.config2.Config;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    static{
        formats.add(new FormatWriter() {
            @Override
            public String getName(){
                return "Hellrage format";
            }
            @Override
            public String[] getExtensions(){
                return new String[]{"json"};
            }
            @Override
            public void write(NCPFFile ncpf, OutputStream stream){
                if(!ncpf.multiblocks.isEmpty()){
                    if(ncpf.multiblocks.size()>1)throw new IllegalArgumentException("Multible multiblocks are not supported by Hellrage JSON!");
                    Multiblock multi = ncpf.multiblocks.get(0);
                    if(multi instanceof UnderhaulSFR){
                        UnderhaulSFR reactor = (UnderhaulSFR) multi;
                        JSON.JSONObject hellrage = new JSON.JSONObject();
                        JSON.JSONObject saveVersion = new JSON.JSONObject();
                        saveVersion.set("Major", 1);
                        saveVersion.set("Minor", 2);
                        saveVersion.set("Build", 23);
                        saveVersion.set("Revision", 0);
                        saveVersion.set("MajorRevision", 0);
                        saveVersion.set("MinorRevision", 0);
                        hellrage.set("SaveVersion", saveVersion);
                        JSON.JSONObject compressedReactor = new JSON.JSONObject();
                        hellrage.set("CompressedReactor", compressedReactor);
                        for(planner.configuration.underhaul.fissionsfr.Block b : ncpf.configuration.underhaul.fissionSFR.blocks){
                            JSON.JSONArray array = new JSON.JSONArray();
                            for(planner.multiblock.underhaul.fissionsfr.Block block : reactor.getBlocks()){
                                if(block.template==b){
                                    JSON.JSONObject bl = new JSON.JSONObject();
                                    bl.set("X", block.x+1);
                                    bl.set("Y", block.y+1);
                                    bl.set("Z", block.z+1);
                                    array.add(bl);
                                }
                            }
                            compressedReactor.put(b.name.replace(" ", "").replace("Liquid", "").replace("Active", "Active ").replace("Cooler", "").replace("Moderator", ""), array);
                        }
                        JSON.JSONObject dims = new JSON.JSONObject();
                        dims.set("X", reactor.getX());
                        dims.set("Y", reactor.getY());
                        dims.set("Z", reactor.getZ());
                        hellrage.set("InteriorDimensions", dims);
                        JSON.JSONObject usedFuel = new JSON.JSONObject();
                        usedFuel.set("Name", reactor.fuel.name);
                        usedFuel.set("BasePower", reactor.fuel.power);
                        usedFuel.set("BaseHeat", reactor.fuel.heat);
                        usedFuel.set("FuelTime", reactor.fuel.time);
                        hellrage.set("UsedFuel", usedFuel);
                        try{
                            hellrage.write(stream);
                        }catch(IOException ex){
                            throw new RuntimeException(ex);
                        }
                    }else if(multi instanceof OverhaulSFR){
                        OverhaulSFR reactor = (OverhaulSFR) multi;
                        JSON.JSONObject hellrage = new JSON.JSONObject();
                        JSON.JSONObject saveVersion = new JSON.JSONObject();
                        saveVersion.set("Major", 2);
                        saveVersion.set("Minor", 1);
                        saveVersion.set("Build", 1);
                        saveVersion.set("Revision", 0);
                        saveVersion.set("MajorRevision", 0);
                        saveVersion.set("MinorRevision", 0);
                        hellrage.set("SaveVersion", saveVersion);
                        JSON.JSONObject data = new JSON.JSONObject();
                        JSON.JSONObject heatSinks = new JSON.JSONObject();
                        JSON.JSONObject moderators = new JSON.JSONObject();
                        JSON.JSONObject reflectors = new JSON.JSONObject();
                        JSON.JSONObject fuelCells = new JSON.JSONObject();
                        JSON.JSONObject irradiators = new JSON.JSONObject();
                        JSON.JSONObject shields = new JSON.JSONObject();
                        for(planner.configuration.overhaul.fissionsfr.Block b : ncpf.configuration.overhaul.fissionSFR.blocks){
                            if(b.cooling>0){
                                JSON.JSONArray array = new JSON.JSONArray();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                }
                                heatSinks.set(b.name.replace(" ", "").replace("HeatSink", "").replace("Sink", "").replace("Heatsink", "").replace("Liquid", ""), array);
                            }
                            if(b.moderator&&!b.shield){
                                JSON.JSONArray array = new JSON.JSONArray();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                }
                                moderators.set(b.name.replace(" ", "").replace("Moderator", ""), array);
                            }
                            if(b.reflector){
                                JSON.JSONArray array = new JSON.JSONArray();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                }
                                reflectors.set(b.name.replace(" ", "").replace("Reflector", ""), array);
                            }
                            if(b.shield){
                                JSON.JSONArray array = new JSON.JSONArray();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                }
                                shields.set(b.name.replace(" ", "").replace("NeutronShield", "").replace("Shield", ""), array);
                            }
                            if(b.fuelCell){
                                HashMap<String, ArrayList<planner.multiblock.overhaul.fissionsfr.Block>> cells = new HashMap<>();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        String name = block.fuel.name;
                                        if(name.endsWith(" Oxide"))name = "[OX]"+name.replace(" Oxide", "");
                                        if(name.endsWith(" Nitride"))name = "[NI]"+name.replace(" Nitride", "");
                                        if(name.endsWith("-Zirconium Alloy"))name = "[ZA]"+name.replace("-Zirconium Alloy", "");
                                        name+=";"+(block.isPrimed()?"True":"False")+";";
                                        if(block.isPrimed())name+=(block.fuel.selfPriming?"Self":block.source.name);
                                        else name+="None";
                                        if(cells.containsKey(name)){
                                            cells.get(name).add(block);
                                        }else{
                                            ArrayList<planner.multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                            blox.add(block);
                                            cells.put(name, blox);
                                        }
                                    }
                                }
                                for(String key : cells.keySet()){
                                    JSON.JSONArray array = new JSON.JSONArray();
                                    for(planner.multiblock.overhaul.fissionsfr.Block block : cells.get(key)){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                    fuelCells.set(key, array);
                                }
                            }
                            if(b.irradiator){
                                HashMap<String, ArrayList<planner.multiblock.overhaul.fissionsfr.Block>> radiators = new HashMap<>();
                                for(planner.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        String name = "{\\\"HeatPerFlux\\\":"+(block.recipe==null?0:block.recipe.heat)+",\\\"EfficiencyMultiplier\\\":"+(block.recipe==null?0:block.recipe.efficiency)+"}";
                                        if(radiators.containsKey(name)){
                                            radiators.get(name).add(block);
                                        }else{
                                            ArrayList<planner.multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                            blox.add(block);
                                            radiators.put(name, blox);
                                        }
                                    }
                                }
                                for(String key : radiators.keySet()){
                                    JSON.JSONArray array = new JSON.JSONArray();
                                    for(planner.multiblock.overhaul.fissionsfr.Block block : radiators.get(key)){
                                        JSON.JSONObject bl = new JSON.JSONObject();
                                        bl.set("X", block.x+1);
                                        bl.set("Y", block.y+1);
                                        bl.set("Z", block.z+1);
                                        array.add(bl);
                                    }
                                    irradiators.set(key, array);
                                }
                            }
                        }
                        data.set("HeatSinks", heatSinks);
                        data.set("Moderators", moderators);
                        data.set("Reflectors", reflectors);
                        data.set("FuelCells", fuelCells);
                        data.set("Irradiators", irradiators);
                        data.set("NeutronShields", shields);
                        JSON.JSONObject dims = new JSON.JSONObject();
                        dims.set("X", reactor.getX());
                        dims.set("Y", reactor.getY());
                        dims.set("Z", reactor.getZ());
                        data.set("InteriorDimensions", dims);
                        data.set("CoolantRecipeName", reactor.coolantRecipe.name);
                        hellrage.set("Data", data);
                        try{
                            hellrage.write(stream);
                        }catch(IOException ex){
                            throw new RuntimeException(ex);
                        }
                    }else throw new IllegalArgumentException(ncpf.multiblocks.get(0).getDefinitionName()+" is not supported by Hellrage JSON!");
                }else{
                    //TODO config export?
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        });
        formats.add(new FormatWriter() {
            @Override
            public String getName(){
                return "NuclearCraft Planner Format";
            }
            @Override
            public String[] getExtensions(){
                return new String[]{"ncpf"};
            }
            @Override
            public void write(NCPFFile ncpf, OutputStream stream){
                Config header = Config.newConfig();
                header.set("version", (byte)1);
                header.set("count", ncpf.multiblocks.size());
                Config meta = Config.newConfig();
                for(String key : ncpf.metadata.keySet()){
                    String value = ncpf.metadata.get(key);
                    if(value.trim().isEmpty())continue;
                    meta.set(key,value);
                }
                if(meta.properties().length>0){
                    header.set("metadata", meta);
                }
                header.save(stream);
                ncpf.configuration.save(stream);
                for(Multiblock m : ncpf.multiblocks){
                    m.save(ncpf.configuration, stream);
                }
            }
        });
    }
    public static void write(NCPFFile ncpf, OutputStream stream, FormatWriter format){
        format.write(ncpf, stream);
    }
    public static void write(NCPFFile ncpf, File file, FormatWriter format){
        if(file.exists())file.delete();
        try{
            file.createNewFile();
            write(ncpf, new FileOutputStream(file), format);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}