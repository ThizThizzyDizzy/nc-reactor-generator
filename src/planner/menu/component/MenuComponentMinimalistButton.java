package planner.menu.component;
import java.awt.Color;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuComponentMinimalistButton extends MenuComponentButton{
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover){
        this(x, y, width, height, label, enabled, useMouseover, false);
    }
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, boolean darker){
        super(x, y, width, height, label, enabled, useMouseover);
        color = darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor();
        foregroundColor = Core.theme.getTextColor();
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
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(foregroundColor);
        drawText();
        Core.applyColor(color);
    }
    public void drawText(){
        if(height>width){
            drawCenteredText(x, y+height/2-width/2, x+width, y+height/2+width/2, label);
        }
        drawCenteredText(x, y+textInset, x+width, y+height-textInset, label);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}