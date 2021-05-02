package planner.file.writer;
import discord.Bot;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.BoundingBox;
import multiblock.Multiblock;
import multiblock.PartCount;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.FormattedText;
import planner.file.FileFormat;
import static planner.file.FileWriter.botRunning;
import planner.file.ImageFormatWriter;
import planner.file.NCPFFile;
import simplelibrary.font.FontManager;
import simplelibrary.image.Image;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class PNGFormatWriter extends ImageFormatWriter{
private final int textHeight = 20;
    private final int borderSize = 16;
    @Override
    public FileFormat getFileFormat(){
        return FileFormat.PNG;
    }
    @Override
    public Image write(NCPFFile ncpf){
        if(!ncpf.multiblocks.isEmpty()){
            if(ncpf.multiblocks.size()>1)throw new IllegalArgumentException("Multible multiblocks are not supported by PNG!");
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
            double textWidth = 0;
            for(int i = 0; i<strs.size(); i++){
                FormattedText str = strs.get(i);
                textWidth = Math.max(textWidth, FontManager.getLengthForStringWithHeight(str.text, textHeight));
            }
            double partsWidth = 0;
            for(PartCount c : parts){
                partsWidth = Math.max(partsWidth, textHeight+FontManager.getLengthForStringWithHeight(textHeight+c.count+"x "+c.name, textHeight));
            }
            final double tW = textWidth+borderSize;
            final double pW = partsWidth+borderSize;
            BoundingBox bbox = multi.getBoundingBox();
            int width = (int) Math.max(textWidth+partsWidth+totalTextHeight,bbox.getWidth()*blockSize+borderSize);
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
            Core.BufferRenderer renderer = (buff) -> {
                Core.applyColor(Core.theme.getEditorListBorderColor());
                Renderer2D.drawRect(0, 0, buff.width, buff.height, 0);
                Core.applyColor(Core.theme.getTextColor());
                for(int i = 0; i<strs.size(); i++){
                    FormattedText str = strs.get(i);
                    Core.drawFormattedText(borderSize/2, i*textHeight+borderSize/2, tW, (i+1)*textHeight+borderSize/2, str, -1);
                }
                Core.applyColor(Core.theme.getTextColor());
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
//                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glPushMatrix();
                GL11.glTranslated(buff.width-totalTextHeight/2, totalTextHeight/2, -1);
                GL11.glScaled(1, 1, 0.0001);
                GL11.glRotated(45, 1, 0, 0);
                GL11.glRotated(45, 0, 1, 0);
                double size = Math.max(bbox.getWidth(), Math.max(bbox.getHeight(), bbox.getDepth()));
                GL11.glScaled(totalTextHeight/2, totalTextHeight/2, totalTextHeight/2);
                GL11.glScaled(1/size, 1/size, 1/size);
                GL11.glRotated(180, 1, 0, 0);
                GL11.glTranslated(-bbox.getWidth()/2d, -bbox.getHeight()/2d, -bbox.getDepth()/2d);
                multi.draw3DInOrder();
                GL11.glPopMatrix();
//                GL11.glDisable(GL11.GL_CULL_FACE);
                for(int y = 0; y<bbox.getHeight(); y++){
                    int column = y%mpr;
                    int row = y/mpr;
                    int layerWidth = bbox.getWidth()*blockSize+borderSize;
                    int layerHeight = bbox.getDepth()*blockSize+borderSize;
                    for(int x = 0; x<bbox.getWidth(); x++){
                        for(int z = 0; z<bbox.getDepth(); z++){
                            Block b = multi.getBlock(x+bbox.x1, y+bbox.y1, z+bbox.z1);
                            if(b!=null)b.render(column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, blockSize, blockSize, true, multi);
                            if(multi instanceof OverhaulFusionReactor&&((OverhaulFusionReactor)multi).getLocationCategory(x, y, z)==OverhaulFusionReactor.LocationCategory.PLASMA){
                                Renderer2D.drawRect(column*layerWidth+borderSize/2+x*blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight, column*layerWidth+borderSize/2+x*blockSize+blockSize, row*layerHeight+borderSize+z*blockSize+totalTextHeight+blockSize, ImageStash.instance.getTexture("/textures/overhaul/fusion/plasma.png"));
                            }
                        }
                    }
                }
            };
            if(botRunning)return Bot.makeImage(width, height, renderer);
            return Core.makeImage(width, height, renderer);
        }else{
            throw new IllegalArgumentException("Cannot export configuration to image!");
        }
    }
    @Override
    public boolean isMultiblockSupported(Multiblock multi){
        return true;
    }
}