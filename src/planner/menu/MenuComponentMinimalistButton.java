package planner.menu;
import java.awt.Color;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuComponentMinimalistButton extends MenuComponentButton{
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover){
        this(x, y, width, height, label, enabled, useMouseover, .6f);
    }
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, float tint){
        super(x, y, width, height, label, enabled, useMouseover);
        color = new Color(tint/2, tint/2, tint, 1f);
        foregroundColor = new Color(.1f, .1f, .2f, 1f);
    }
    @Override
    public void render(){
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        if(enabled){
            if(isPressed)col = col.darker();
            else if(isMouseOver)col = col.brighter();
        }else{
            col = col.darker();
        }
        GL11.glColor4f(col.getRed()/255F, col.getGreen()/255F, col.getBlue()/255F, col.getAlpha()/255F);
        drawRect(x, y, x+width, y+height, 0);
        GL11.glColor4f(foregroundColor.getRed()/255F, foregroundColor.getGreen()/255F, foregroundColor.getBlue()/255F, foregroundColor.getAlpha()/255F);
        drawText();
        GL11.glColor4f(color.getRed()/255F, color.getGreen()/255F, color.getBlue()/255F, color.getAlpha()/255F);
    }
    public void drawText(){
        drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, label);
    }
}