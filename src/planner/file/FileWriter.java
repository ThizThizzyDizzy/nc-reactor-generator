package planner.file;
import discord.Bot;
import planner.JSON;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import org.lwjgl.opengl.GL11;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.Renderer2D;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    public static FormatWriter NCPF,PNG,HELLRAGE;
    static{
        formats.add(HELLRAGE = new FormatWriter(){
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
                        for(multiblock.configuration.underhaul.fissionsfr.Block b : ncpf.configuration.underhaul.fissionSFR.allBlocks){
                            JSON.JSONArray array = new JSON.JSONArray();
                            for(multiblock.underhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                        for(multiblock.configuration.overhaul.fissionsfr.Block b : ncpf.configuration.overhaul.fissionSFR.allBlocks){
                            if(b.cooling>0){
                                JSON.JSONArray array = new JSON.JSONArray();
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                                HashMap<String, ArrayList<multiblock.overhaul.fissionsfr.Block>> cells = new HashMap<>();
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
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
                                            ArrayList<multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                            blox.add(block);
                                            cells.put(name, blox);
                                        }
                                    }
                                }
                                for(String key : cells.keySet()){
                                    JSON.JSONArray array = new JSON.JSONArray();
                                    for(multiblock.overhaul.fissionsfr.Block block : cells.get(key)){
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
                                HashMap<String, ArrayList<multiblock.overhaul.fissionsfr.Block>> radiators = new HashMap<>();
                                for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                                    if(block.template==b){
                                        String name = "{\\\"HeatPerFlux\\\":"+(block.irradiatorRecipe==null?0:(int)block.irradiatorRecipe.heat)+",\\\"EfficiencyMultiplier\\\":"+(block.irradiatorRecipe==null?0:block.irradiatorRecipe.efficiency)+"}";
                                        if(radiators.containsKey(name)){
                                            radiators.get(name).add(block);
                                        }else{
                                            ArrayList<multiblock.overhaul.fissionsfr.Block> blox = new ArrayList<>();
                                            blox.add(block);
                                            radiators.put(name, blox);
                                        }
                                    }
                                }
                                for(String key : radiators.keySet()){
                                    JSON.JSONArray array = new JSON.JSONArray();
                                    for(multiblock.overhaul.fissionsfr.Block block : radiators.get(key)){
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
                        JSON.JSONArray conductors = new JSON.JSONArray();
                        for(multiblock.overhaul.fissionsfr.Block block : reactor.getBlocks()){
                            if(block.isConductor()||block.isInert()){
                                JSON.JSONObject bl = new JSON.JSONObject();
                                bl.set("X", block.x+1);
                                bl.set("Y", block.y+1);
                                bl.set("Z", block.z+1);
                                conductors.add(bl);
                            }
                        }
                        data.set("Conductors", conductors);
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
            @Override
            public boolean isMultiblockSupported(Multiblock multi){
                return multi instanceof OverhaulSFR||multi instanceof UnderhaulSFR;
            }
        });
        formats.add(NCPF = new FormatWriter(){
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
                header.set("version", NCPFFile.SAVE_VERSION);
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
                ncpf.configuration.save(null, Config.newConfig()).save(stream);
                for(Multiblock m : ncpf.multiblocks){
                    m.save(ncpf, ncpf.configuration, stream);
                }
                try{
                    stream.close();
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
            }
            @Override
            public boolean isMultiblockSupported(Multiblock multi){
                return true;
            }
        });
        formats.add(PNG = new FormatWriter(){
            private final int textHeight = 20;
            private final int borderSize = 16;
            @Override
            public String getName(){
                return "PNG Image";
            }
            @Override
            public String[] getExtensions(){
                return new String[]{"png"};
            }
            @Override
            public void write(NCPFFile ncpf, OutputStream stream){
                if(!ncpf.multiblocks.isEmpty()){
                    if(ncpf.multiblocks.size()>1)throw new IllegalArgumentException("Multible multiblocks are not supported by Hellrage JSON!");
                    final Multiblock<Block> multi = ncpf.multiblocks.get(0);
                    multi.recalculate();
                    int blSiz = 32;
                    for(Block b : multi.getBlocks()){
                        blSiz = Math.max(b.getTexture().getWidth(), blSiz);
                    }
                    int textHeight = this.textHeight*blSiz/16;//32x32 blocks result in high-res image
                    final int blockSize = blSiz;
                    ArrayList<PartCount> parts = multi.getPartsList();
                    String s = multi.getSaveTooltip();
                    String[] strs = s.split("\n");
                    int totalTextHeight = Math.max(textHeight*strs.length,textHeight*parts.size());
                    double textWidth = 0;
                    for(int i = 0; i<strs.length; i++){
                        String str = strs[i];
                        textWidth = Math.max(textWidth, FontManager.getLengthForStringWithHeight(str, textHeight));
                    }
                    double partsWidth = 0;
                    for(PartCount c : parts){
                        partsWidth = Math.max(partsWidth, textHeight+FontManager.getLengthForStringWithHeight(textHeight+c.count+"x "+c.name, textHeight));
                    }
                    final double tW = textWidth+borderSize;
                    final double pW = partsWidth+borderSize;
                    int width = (int) Math.max(textWidth+partsWidth+totalTextHeight,multi.getX()*blockSize+borderSize);
                    int multisPerRow = Math.max(1, (int)(width/(multi.getX()*blockSize+borderSize)));
                    int rowCount = (multi.getY()+multisPerRow-1)/multisPerRow;
                    int height = totalTextHeight+rowCount*(multi.getZ()*blockSize+borderSize)+borderSize/2;
                    while(rowCount>1&&height>width){
                        width++;
                        multisPerRow = Math.max(1, (int)(width/(multi.getX()*blockSize+borderSize)));
                        rowCount = (multi.getY()+multisPerRow-1)/multisPerRow;
                        height = totalTextHeight+rowCount*(multi.getZ()*blockSize+borderSize);
                    }
                    int mpr = multisPerRow;
                    try{
                        Core.BufferRenderer renderer = (buff) -> {
                            Core.applyColor(Core.theme.getEditorListBorderColor());
                            Renderer2D.drawRect(0, 0, buff.width, buff.height, 0);
                            Core.applyColor(Core.theme.getTextColor());
                            for(int i = 0; i<strs.length; i++){
                                String str = strs[i];
                                Renderer2D.drawText(borderSize/2, i*textHeight+borderSize/2, tW, (i+1)*textHeight+borderSize/2, str);
                            }
                            for(int i = 0; i<parts.size(); i++){
                                PartCount c = parts.get(i);
                                Renderer2D.drawText(tW+textHeight+borderSize/2, i*textHeight+borderSize/2, tW+pW, (i+1)*textHeight+borderSize/2, c.count+"x "+c.name);
                            }
                            Core.applyWhite();
                            for(int i = 0; i<parts.size(); i++){
                                PartCount c = parts.get(i);
                                int tex = c.getTexture();
                                if(tex!=-1)Renderer2D.drawRect(tW, i*textHeight+borderSize/2, tW+textHeight, (i+1)*textHeight+borderSize/2, tex);
                            }
//                            GL11.glEnable(GL11.GL_CULL_FACE);
                            GL11.glPushMatrix();
                            GL11.glTranslated(buff.width-totalTextHeight/2, totalTextHeight/2, -1);
                            GL11.glScaled(1, 1, 0.0001);
                            GL11.glRotated(45, 1, 0, 0);
                            GL11.glRotated(45, 0, 1, 0);
                            double size = Math.max(multi.getX(), Math.max(multi.getY(), multi.getZ()));
                            GL11.glScaled(totalTextHeight/2, totalTextHeight/2, totalTextHeight/2);
                            GL11.glScaled(1/size, 1/size, 1/size);
                            GL11.glTranslated(-multi.getX()/2d, -multi.getY()/2d, -multi.getZ()/2d);
                            multi.draw3DInOrder();
                            GL11.glPopMatrix();
//                            GL11.glDisable(GL11.GL_CULL_FACE);
                            for(int y = 0; y<multi.getY(); y++){
                                int column = y%mpr;
                                int row = y/mpr;
                                int layerWidth = multi.getX()*blockSize+borderSize;
                                int layerHeight = multi.getZ()*blockSize+borderSize;
                                for(int x = 0; x<multi.getX(); x++){
                                    for(int z = 0; z<multi.getZ(); z++){
                                        Block b = multi.getBlock(x, y, z);
                                        if(b!=null)b.render(column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, blockSize, blockSize, false, multi);
                                    }
                                }
                            }
                        };
                        if(botRunning)ImageIO.write(Bot.makeImage(width, height, renderer), "png", stream);
                        else ImageIO.write(Core.makeImage(width, height, renderer), "png", stream);
                        stream.close();
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }else{
                    throw new IllegalArgumentException("Cannot export configuration to image!");
                }
            }
            @Override
            public boolean isMultiblockSupported(Multiblock multi){
                return true;
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