package net.ncplanner.plannerator.planner.theme;
import net.ncplanner.plannerator.Renderer;
import simplelibrary.image.Color;
public abstract class ColorTheme extends Theme{
    public ColorTheme(String name){
        super(name);
    }
    @Override
    public void drawKeywordBackground(double x, double y, double width, double height, double pixelScale){
        Renderer renderer = new Renderer();
        renderer.setColor(getKeywordBackgroundColor());
        renderer.fillRect(0, 0, width, height);
    }
    public abstract Color getKeywordBackgroundColor();
    @Override
    public void drawThemeButtonBackground(double x, double y, double width, double height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
        Renderer renderer = new Renderer();
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
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawThemeButtonText(double x, double y, double width, double height, double textHeight, String text){
        Renderer renderer = new Renderer();
        renderer.setColor(getComponentTextColor(0));
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
}