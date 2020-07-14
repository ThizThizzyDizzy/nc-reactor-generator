package planner.menu.component;
import java.awt.Color;
import planner.Core;
import simplelibrary.font.FontManager;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
public class MenuComponentMinimalistOptionButton extends MenuComponentOptionButton{
    private String label;
    private boolean isPressed, isRightPressed;
    private final boolean darker;
    public MenuComponentMinimalistOptionButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, int startingOption, String... options){
        this(x, y, width, height, label, enabled, useMouseover, false, startingOption, options);
    }
    public MenuComponentMinimalistOptionButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, boolean darker, int startingOption, String... options){
        super(x, y, width, height, label, enabled, useMouseover, startingOption, options);
        this.label = label;
        this.darker = darker;
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
        Color col = darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor();
        if(enabled){
            if(isPressed||isRightPressed)col = col.darker();
            else if(isMouseOver)col = col.brighter();
        }else{
            col = col.darker();
        }
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
    }
    public void drawText(){
        String text = label+": "+getSelectedString();
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = (int)((height-textInset*2)*scale)-4;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
}