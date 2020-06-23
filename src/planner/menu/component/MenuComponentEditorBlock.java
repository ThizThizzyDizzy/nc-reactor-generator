package planner.menu.component;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import planner.multiblock.Block;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentEditorBlock extends MenuComponent{
    private final Block block;
    public MenuComponentEditorBlock(Block block){
        super(0, 0, 0, 0);
        float tint = .9f;
        color = new Color(tint/2, tint/2, tint, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
        this.block = block;
        selectedColor = foregroundColor.darker();
    }
    @Override
    public void render(){
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        if(isMouseOver)col = col.brighter();
        GL11.glColor4f(col.getRed()/255F, col.getGreen()/255F, col.getBlue()/255F, col.getAlpha()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255F);
        drawText();
        GL11.glColor4f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F, color.getAlpha()/255F);
        double border = height/8;
        if(isSelected){
            Color cornerColor = color.darker();
            GL11.glColor4d(cornerColor.getRed()/255f, cornerColor.getGreen()/255f, cornerColor.getBlue()/255f, cornerColor.getAlpha()/300f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            GL11.glColor4d(selectedColor.getRed()/255f, selectedColor.getGreen()/255f, selectedColor.getBlue()/255f, selectedColor.getAlpha()/300f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
        if(isMouseOver){
            Color cornerColor = color;
            GL11.glColor4d(cornerColor.getRed()/255f, cornerColor.getGreen()/255f, cornerColor.getBlue()/255f, cornerColor.getAlpha()/400f);
            drawRect(x, y, x+border, y+border, 0);
            drawRect(x+width-border, y, x+width, y+border, 0);
            drawRect(x, y+height-border, x+border, y+height, 0);
            drawRect(x+width-border, y+height-border, x+width, y+height, 0);
            GL11.glColor4d(foregroundColor.getRed()/255f, foregroundColor.getGreen()/255f, foregroundColor.getBlue()/255f, foregroundColor.getAlpha()/400f);
            drawRect(x+border, y, x+width-border, y+border, 0);
            drawRect(x+border, y+height-border, x+width-border, y+height, 0);
            drawRect(x, y+border, x+border, y+height-border, 0);
            drawRect(x+width-border, y+border, x+width, y+height-border, 0);
        }
    }
    public void drawText(){
        if(block.getTexture()==null){
            drawCenteredText(x, y+height*.4, x+width, y+height*.6, block.getName());
        }else{
            GL11.glColor4d(1, 1, 1, 1);
            drawRect(x, y, x+width, y+height, ImageStash.instance.allocateAndSetupTexture(block.getTexture()));
        }
    }
}