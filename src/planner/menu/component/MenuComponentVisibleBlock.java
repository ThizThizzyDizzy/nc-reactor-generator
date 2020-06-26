package planner.menu.component;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentVisibleBlock extends MenuComponent{
    public final Multiblock multiblock;
    public final int blockX;
    public final int blockY;
    public final int blockZ;
    public MenuComponentVisibleBlock(int x, int y, int width, int height, Multiblock multiblock, int blockX, int blockY, int blockZ){
        super(x, y, width, height);
        float tint = .9f;
        this.multiblock = multiblock;
        color = new Color(tint/2, tint/2, tint, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }
    @Override
    public void render(){
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glColor4f(col.getRed()/255F, col.getGreen()/255F, col.getBlue()/255F, col.getAlpha()/255F);
        drawRect(x, y, x+width, y+height, 0);
        double border = height/8;
        GL11.glColor4d(foregroundColor.getRed()/255f, foregroundColor.getGreen()/255f, foregroundColor.getBlue()/255f, foregroundColor.getAlpha()/255f);
        drawRect(x, y, x+width, y+border/4, 0);
        drawRect(x, y+height-border/4, x+width, y+height, 0);
        drawRect(x, y+border/4, x+border/4, y+height-border/4, 0);
        drawRect(x+width-border/4, y+border/4, x+width, y+height-border/4, 0);        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255F);
        drawText();
        GL11.glColor4f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F, color.getAlpha()/255F);
    }
    public void drawText(){
        Block block = multiblock.getBlock(blockX, blockY, blockZ);
        if(block==null)return;
        if(block.getTexture()==null){
            drawCenteredText(x, y+height*.4, x+width, y+height*.6, block.getName());
        }else{
            GL11.glColor4d(1, 1, 1, 1);
            drawRect(x, y, x+width, y+height, Core.getTexture(block.getTexture()));
        }
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}