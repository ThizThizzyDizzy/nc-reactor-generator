package net.ncplanner.plannerator.planner.theme;
import net.ncplanner.plannerator.graphics.PlanneratorRenderer;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.text.Font;
import net.ncplanner.plannerator.planner.Core;
public abstract class ColorTheme extends Theme{
    public ColorTheme(String name){
        super(name);
    }
    @Override
    public void drawKeywordBackground(float x, float y, float width, float height, float pixelScale){
        PlanneratorRenderer.setColor(getKeywordBackgroundColor());
        PlanneratorRenderer.fillRect(0, 0, width, height);
    }
    public abstract Color getKeywordBackgroundColor();
    @Override
    public void drawThemeButtonBackground(float x, float y, float width, float height, boolean darker, boolean enabled, boolean pressed, boolean mouseOver){
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
        PlanneratorRenderer.setColor(col);
        PlanneratorRenderer.fillRect(x, y, x+width, y+height);
    }
    @Override
    public void drawThemeButtonText(float x, float y, float width, float height, float textHeight, String text){
        PlanneratorRenderer.setFont(getDefaultFont());
        PlanneratorRenderer.setColor(getComponentTextColor(0));
        PlanneratorRenderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
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
