package net.ncplanner.plannerator.planner.theme;
import net.ncplanner.plannerator.graphics.Font;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
public abstract class ColorTheme extends Theme{
    public ColorTheme(String name){
        super(name);
    }
    @Override
    public void drawKeywordBackground(float x, float y, float width, float height, float pixelScale){
        Renderer renderer = new Renderer();
        renderer.setColor(getKeywordBackgroundColor());
        renderer.fillRect(0, 0, width, height);
    }
    public abstract Color getKeywordBackgroundColor();
    @Override
    public void drawThemeButtonBackground(float x, float y, float width, float height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
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
    public void drawThemeButtonText(float x, float y, float width, float height, float textHeight, String text){
        Renderer renderer = new Renderer();
        renderer.setFont(getDefaultFont());
        renderer.setColor(getComponentTextColor(0));
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public Font getDefaultFont(){
        return Core.FONT_40;
    }
    @Override
    public Font getTextViewFont(){
        return Core.FONT_20;
    }
    @Override
    public Font getCodeFont(){
        return Core.FONT_MONO_20;
    }
    @Override
    public Font getDecalFont(){
        return Core.FONT_10;
    }
}