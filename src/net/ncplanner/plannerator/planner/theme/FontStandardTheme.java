package net.ncplanner.plannerator.planner.theme;
import net.ncplanner.plannerator.graphics.Font;
import net.ncplanner.plannerator.graphics.image.Color;
public class FontStandardTheme extends StandardTheme{
    private Font font;
    private final String fontName;
    public FontStandardTheme(String name, Color background, Color color, float rgbTint, float rgbSat, String fontName){
        super(name, background, color, rgbTint, rgbSat);
        this.fontName = fontName;
    }
    private Font getFont(){
        if(font==null)font = Font.loadFont(fontName);
        return font;
    }
    @Override
    public Font getDefaultFont(){
        return getFont();
    }
    @Override
    public Font getTextViewFont(){
        return getFont();
    }
    @Override
    public Font getCodeFont(){
        return getFont();
    }
    @Override
    public Font getDecalFont(){
        return getFont();
    }
}