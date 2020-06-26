package planner.menu.component;
import java.awt.Color;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
public class MenuComponentMinimalistOptionButton extends MenuComponentOptionButton{
    public MenuComponentMinimalistOptionButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, int startingOption, String... options){
        this(x, y, width, height, label, enabled, useMouseover, .6f, startingOption, options);
    }
    private String label;
    private boolean isPressed, isRightPressed;
    public MenuComponentMinimalistOptionButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, float tint, int startingOption, String... options){
        super(x, y, width, height, label, enabled, useMouseover, startingOption, options);
        this.label = label;
        color = Core.theme.getButtonColor(tint);
        foregroundColor = Core.theme.getTextColor();
    }
    @Override
    public void mouseEvent(double x, double y, int button, boolean isDown){
        super.mouseEvent(x, y, button, isDown);
        if(button==0&&isDown==true&&enabled){
            isPressed = true;
        }else if(button==0&&isDown==false&&isPressed&&enabled){
            isPressed = false;
        }
        if(button==1&&isDown==true&&enabled){
            isRightPressed = true;
        }else if(button==1&&isDown==false&&isRightPressed&&enabled){
            isRightPressed = false;
        }
    }
    @Override
    public void mouseover(double x, double y, boolean isMouseOver){
        super.mouseover(x, y, isMouseOver);
        if(!isMouseOver){
            isPressed = false;
            isRightPressed = false;
        }
    }
    @Override
    public void render(){
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        if(enabled){
            if(isPressed||isRightPressed)col = col.darker();
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
        drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, label+": "+getSelectedString());
    }
}