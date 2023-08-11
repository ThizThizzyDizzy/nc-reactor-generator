package net.ncplanner.plannerator.planner.file.writer;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.StringUtil;
import net.ncplanner.plannerator.planner.file.FileFormat;
import net.ncplanner.plannerator.planner.file.FormatWriter;
import net.ncplanner.plannerator.planner.file.JSON;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.Project;
import net.ncplanner.plannerator.planner.ncpf.configuration.OverhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.design.MultiblockDesign;
public class HellrageWriter extends FormatWriter{
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.HELLRAGE_REACTOR;
    }
    @Override
    public void write(Project ncpf, OutputStream stream){
        boolean hasOverhaul = false;
        for(Design d : ncpf.designs){
            if(d.definition.type.contains("overhaul"))hasOverhaul = true;
        }
        if(hasOverhaul){
            Core.warning("Hellrage JSON format is deprecated!\nCasings, configurations, and addons will not be saved!\nSome things, such as coolant recipes, may not be saved properly!\n\nPlease use NCPF for full support", null);
        }
        if(!ncpf.designs.isEmpty()){
            if(ncpf.designs.size()>1)throw new IllegalArgumentException("Multiple designs are not supported by Hellrage JSON!");
            Design design = ncpf.designs.get(0);
            if(!(design instanceof MultiblockDesign))throw new IllegalArgumentException("Cannot export non-multiblock design "+design.definition.toString()+" to Hellrage JSON!");
            Multiblock multi = ((MultiblockDesign)design).toMultiblock();
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
                for(net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.BlockElement b : ncpf.getConfiguration(UnderhaulSFRConfiguration::new).blocks){
                    JSON.JSONArray array = new JSON.JSONArray();
                    for(net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.Block block : reactor.getBlocks()){
                        if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                        if(block.template==b){
                            JSON.JSONObject bl = new JSON.JSONObject();
                            bl.set("X", block.x);
                            bl.set("Y", block.y);
                            bl.set("Z", block.z);
                            array.add(bl);
                        }
                    }
                    compressedReactor.put(StringUtil.superRemove(StringUtil.superReplace(b.getDisplayName(), "Reactor Cell", "Fuel Cell", "Active", "Active "), " ", "Liquid", "Cooler", "Moderator"), array);
                }
                JSON.JSONObject dims = new JSON.JSONObject();
                dims.set("X", reactor.getInternalWidth());
                dims.set("Y", reactor.getInternalHeight());
                dims.set("Z", reactor.getInternalDepth());
                hellrage.set("InteriorDimensions", dims);
                JSON.JSONObject usedFuel = new JSON.JSONObject();
                usedFuel.set("Name", reactor.fuel.getDisplayName());
                usedFuel.set("BasePower", reactor.fuel.stats.power);
                usedFuel.set("BaseHeat", reactor.fuel.stats.heat);
                usedFuel.set("FuelTime", reactor.fuel.stats.time);
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
                for(net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement b : ncpf.getConfiguration(OverhaulSFRConfiguration::new).blocks){
                    if(b.heatsink!=null){
                        JSON.JSONArray array = new JSON.JSONArray();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
                                array.add(bl);
                            }
                        }
                        heatSinks.set(StringUtil.superRemove(b.getDisplayName(), " ", "HeatSink", "Sink", "Heatsink", "Liquid"), array);
                    }
                    if(b.moderator!=null){
                        JSON.JSONArray array = new JSON.JSONArray();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
                                array.add(bl);
                            }
                        }
                        moderators.set(StringUtil.superRemove(b.getDisplayName(), " ", "Moderator"), array);
                    }
                    if(b.reflector!=null){
                        JSON.JSONArray array = new JSON.JSONArray();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
                                array.add(bl);
                            }
                        }
                        reflectors.set(StringUtil.superRemove(b.getDisplayName(), " ", "Reflector"), array);
                    }
                    if(b.neutronShield!=null){
                        JSON.JSONArray array = new JSON.JSONArray();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
                                array.add(bl);
                            }
                        }
                        shields.set(StringUtil.superRemove(b.getDisplayName(), " ", "NeutronShield", "Shield"), array);
                    }
                    if(b.fuelCell!=null){
                        HashMap<String, ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>> cells = new HashMap<>();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                String name = block.getRecipe().getDisplayName();
                                if(name.endsWith(" Oxide"))name = "[OX]"+StringUtil.superReplace(name, " Oxide", "");
                                if(name.endsWith(" Nitride"))name = "[NI]"+StringUtil.superReplace(name, " Nitride", "");
                                if(name.endsWith("-Zirconium Alloy"))name = "[ZA]"+StringUtil.superReplace(name, "-Zirconium Alloy", "");
                                name+=";"+(block.isPrimed()?"True":"False")+";";
                                if(block.isPrimed())name+=(block.fuel.stats.selfPriming?"Self":StringUtil.superRemove(block.source.template.getDisplayName(), " Neutron Source"));
                                else name+="None";
                                if(cells.containsKey(name)){
                                    cells.get(name).add(block);
                                }else{
                                    ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                    blox.add(block);
                                    cells.put(name, blox);
                                }
                            }
                        }
                        for(String key : cells.keySet()){
                            JSON.JSONArray array = new JSON.JSONArray();
                            for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : cells.get(key)){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
                                array.add(bl);
                            }
                            fuelCells.set(key, array);
                        }
                    }
                    if(b.irradiator!=null){
                        HashMap<String, ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block>> radiators = new HashMap<>();
                        for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                            if(block.template==b){
                                String name = "{\\\"HeatPerFlux\\\":"+(block.irradiatorRecipe==null?0:(int)block.irradiatorRecipe.stats.heat)+",\\\"EfficiencyMultiplier\\\":"+(block.irradiatorRecipe==null?0:block.irradiatorRecipe.stats.efficiency)+"}";
                                if(radiators.containsKey(name)){
                                    radiators.get(name).add(block);
                                }else{
                                    ArrayList<net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                    blox.add(block);
                                    radiators.put(name, blox);
                                }
                            }
                        }
                        for(String key : radiators.keySet()){
                            JSON.JSONArray array = new JSON.JSONArray();
                            for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : radiators.get(key)){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x);
                                bl.set("Y", block.y);
                                bl.set("Z", block.z);
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
                JSON.JSONArray conductors = new JSON.JSONArray();
                for(net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                    if(block.x==0||block.y==0||block.z==0||block.x==reactor.getInternalWidth()+1||block.y==reactor.getInternalHeight()+1||block.z==reactor.getInternalDepth()+1)continue;//can't save the casing :(
                    if(block.isConductor()){
                        JSON.JSONObject bl = new JSON.JSONObject();
                        bl.set("X", block.x);
                        bl.set("Y", block.y);
                        bl.set("Z", block.z);
                        conductors.add(bl);
                    }
                }
                data.set("Conductors", conductors);
                JSON.JSONObject dims = new JSON.JSONObject();
                dims.set("X", reactor.getInternalWidth());
                dims.set("Y", reactor.getInternalHeight());
                dims.set("Z", reactor.getInternalDepth());
                data.set("InteriorDimensions", dims);
                data.set("CoolantRecipeName", reactor.coolantRecipe.getDisplayName()+" to whatever it gets turned into");
                hellrage.set("Data", data);
                try{
                    hellrage.write(stream);
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }else throw new IllegalArgumentException(multi.getDefinitionName()+" is not supported by Hellrage JSON!");
        }else{
            //TODO config export?
            throw new UnsupportedOperationException("Cannot export NCPF configuration to Hellrage JSON format!");
        }
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return multi instanceof OverhaulSFR||multi instanceof UnderhaulSFR;
    }
}