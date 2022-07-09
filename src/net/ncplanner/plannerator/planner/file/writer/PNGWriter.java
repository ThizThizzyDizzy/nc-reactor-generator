package net.ncplanner.plannerator.planner.file.writer;
import java.util.ArrayList;
import net.ncplanner.plannerator.discord.Bot;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.PartCount;
import net.ncplanner.plannerator.multiblock.editor.Decal;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.FormattedText;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.editor.overlay.EditorOverlay;
import net.ncplanner.plannerator.planner.file.FileFormat;
import static net.ncplanner.plannerator.planner.file.FileWriter.botRunning;
import net.ncplanner.plannerator.planner.file.ImageFormatWriter;
import net.ncplanner.plannerator.planner.file.NCPFFile;
import org.joml.Matrix4f;
public class PNGWriter extends ImageFormatWriter{
    private final int textHeight = 20;
    private final int borderSize = 16;
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.PNG;
    }
    @Override
    public Image write(NCPFFile ncpf){
        Renderer renderer = new Renderer();
        if(!ncpf.multiblocks.isEmpty()){
            if(ncpf.multiblocks.size()>1)throw new IllegalArgumentException("Multiple multiblocks are not supported by PNG!");
            final Multiblock multi = ncpf.multiblocks.get(0);
            multi.recalculate();
            int blSiz = 32;
            ArrayList<Block> blox = multi.getBlocks();
            for(Block b : blox){
                if(b.getTexture()==null)continue;
                blSiz = Math.max(b.getTexture().getWidth(), blSiz);
            }
            int textHeight = this.textHeight*blSiz/16;//32x32 blocks result in high-res image
            final int blockSize = blSiz;
            ArrayList<PartCount> parts = multi.getPartsList();
            FormattedText s = multi.getSaveTooltip();
            ArrayList<FormattedText> strs = s.split("\n");
            int totalTextHeight = Math.max(textHeight*strs.size(),textHeight*parts.size());
            float textWidth = 0;
            for(int i = 0; i<strs.size(); i++){
                FormattedText str = strs.get(i);
                textWidth = Math.max(textWidth, renderer.getStringWidth(str.text, textHeight));
            }
            float partsWidth = 0;
            for(PartCount c : parts){
                partsWidth = Math.max(partsWidth, textHeight+renderer.getStringWidth(c.count+"x "+c.name, textHeight));
            }
            final float tW = textWidth+borderSize;
            final float pW = partsWidth+borderSize;
            BoundingBox bbox = multi.getBoundingBox(Core.imageExportCasing);
            int width = (int) Math.max(textWidth+partsWidth+(Core.imageExport3DView?totalTextHeight:0),bbox.getWidth()*blockSize+borderSize);
            int multisPerRow = Math.max(1, (int)(width/(bbox.getWidth()*blockSize+borderSize)));
            int rowCount = (bbox.getHeight()+multisPerRow-1)/multisPerRow;
            int height = totalTextHeight+rowCount*(bbox.getDepth()*blockSize+borderSize)+borderSize/2;
            while(rowCount>1&&height>width){
                width++;
                multisPerRow = Math.max(1, (int)(width/(bbox.getWidth()*blockSize+borderSize)));
                rowCount = (bbox.getHeight()+multisPerRow-1)/multisPerRow;
                height = totalTextHeight+rowCount*(bbox.getDepth()*blockSize+borderSize);
            }
            int mpr = multisPerRow;
            Core.BufferRenderer buffRenderer = (bufferRenderer, bufferWidth, bufferHeight) -> {
                bufferRenderer.setColor(Core.theme.getImageExportBackgroundColor());
                bufferRenderer.fillRect(0, 0, bufferWidth, bufferHeight);
                bufferRenderer.setColor(Core.theme.getImageExportTextColor());
                for(int i = 0; i<strs.size(); i++){
                    FormattedText str = strs.get(i);
                    bufferRenderer.drawFormattedText(borderSize/2, i*textHeight+borderSize/2, tW, (i+1)*textHeight+borderSize/2, str, -1);
                }
                bufferRenderer.setColor(Core.theme.getImageExportTextColor());
                for(int i = 0; i<parts.size(); i++){
                    PartCount c = parts.get(i);
                    if(Core.imageExport3DView)bufferRenderer.drawText(tW+textHeight+borderSize/2, i*textHeight+borderSize/2, tW+pW, (i+1)*textHeight+borderSize/2, c.count+"x "+c.name);
                    else bufferRenderer.drawText(bufferWidth-pW+textHeight+borderSize/2, i*textHeight+borderSize/2, bufferWidth, (i+1)*textHeight+borderSize/2, c.count+"x "+c.name);
                }
                bufferRenderer.setWhite();
                for(int i = 0; i<parts.size(); i++){
                    PartCount c = parts.get(i);
                    Image image = c.getImage();
                    if(image!=null){
                        if(Core.imageExport3DView)bufferRenderer.drawImage(image, tW, i*textHeight+borderSize/2, tW+textHeight, (i+1)*textHeight+borderSize/2);
                        else bufferRenderer.drawImage(image, bufferWidth-pW, i*textHeight+borderSize/2, bufferWidth-pW+textHeight, (i+1)*textHeight+borderSize/2);
                    }
                }
                if(Core.imageExport3DView){
                    BoundingBox bbox3 = multi.getBoundingBox(Core.imageExportCasing3D);
                    float size = Math.max(bbox3.getWidth(), Math.max(bbox3.getHeight(), bbox3.getDepth()));
                    renderer.model(new Matrix4f()
                            .setTranslation(bufferWidth-totalTextHeight/2, totalTextHeight/2, -1)
                            .scale(1, 1, 0.0001f)
                            .rotate((float)MathUtil.toRadians(45), 1, 0, 0)
                            .rotate((float)MathUtil.toRadians(45), 0, 1, 0)
                            .scale(totalTextHeight/2, totalTextHeight/2, totalTextHeight/2)
                            .scale(1/size, 1/size, 1/size)
                            .rotate((float)MathUtil.toRadians(180), 1, 0, 0)
                            .translate(-bbox3.getWidth()/2f, -bbox3.getHeight()/2f, -bbox3.getDepth()/2f));
                    multi.draw3DInOrder(Core.imageExportCasing3D);
                    renderer.resetModelMatrix();
                }
                for(int y = 0; y<bbox.getHeight(); y++){
                    int column = y%mpr;
                    int row = y/mpr;
                    int layerWidth = bbox.getWidth()*blockSize+borderSize;
                    int layerHeight = bbox.getDepth()*blockSize+borderSize;
                    for(int x = 0; x<bbox.getWidth(); x++){
                        for(int z = 0; z<bbox.getDepth(); z++){
                            int X = x+bbox.x1;
                            int Y = y+bbox.y1;
                            int Z = z+bbox.z1;
                            if(!Core.imageExportCasing&&multi.shouldHideWithCasing(X,Y,Z))continue;
                            Block b = multi.getBlock(X, Y, Z);
                            if(b!=null)b.render(bufferRenderer, column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, blockSize, blockSize, overlays, multi);
                            if(multi instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multi).getLocationCategory(X, Y, Z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                                bufferRenderer.drawImage("/textures/overhaul/fusion/plasma.png", column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, column*layerWidth+borderSize/2+x*blockSize+blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight+blockSize);
                            }
                            for(EditorOverlay o : overlays){
                                if(!o.isActive())continue;
                                for(Decal d : (Queue<Decal>)o.decals){
                                    if(d.x==X&&d.y==Y&&d.z==Z){
                                        d.render(bufferRenderer, column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, blockSize);
                                    }
                                }
                            }
                        }
                    }
                }
            };
            if(botRunning)return Bot.makeImage(width, height, buffRenderer);
            return Core.makeImage(width, height, buffRenderer);
        }else{
            throw new IllegalArgumentException("Cannot export configuration to image!");
        }
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
}