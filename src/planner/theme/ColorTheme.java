package planner.theme;
import planner.Core;
import static planner.Core.applyColor;
import simplelibrary.image.Color;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import static simplelibrary.opengl.Renderer2D.drawRect;
public abstract class ColorTheme extends Theme{
    public ColorTheme(String name){
        super(name);
    }
    @Override
    public void drawKeywordBackground(double x, double y, double width, double height, double pixelScale){
        applyColor(getKeywordBackgroundColor());
        drawRect(0, 0, width, height, 0);
    }
    public abstract Color getKeywordBackgroundColor();
    @Override
    public void drawThemeButtonBackground(double x, double y, double width, double height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
        Color col;
        if(darker){
             col = getSecondaryComponentColor(0);
            if(enabled){
                if(pressed)col = getSecondaryComponentPressedColor(0);
                else if(mouseOver)col = getSecondaryComponentMouseoverColor(0);
            }else{
                col = getSecondaryComponentDisabledColor(0);
            }
        }else{
            col = getComponentColor(0);
            if(enabled){
                if(pressed)col = getComponentPressedColor(0);
                else if(mouseOver)col = getComponentMouseoverColor(0);
            }else{
                col = getComponentDisabledColor(0);
            }
        }
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
    }
    @Override
    public void drawThemeButtonText(double x, double y, double width, double height, double textHeight, String text){
        Core.applyColor(getComponentTextColor(0));
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
}