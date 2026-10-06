package net.ncplanner.plannerator.planner.theme;
import com.thizthizzydizzy.dizzyengine.ResourceManager;
import com.thizthizzydizzy.dizzyengine.graphics.image.Color;
import com.thizthizzydizzy.dizzyengine.graphics.text.Font;
public class FontStandardTheme extends StandardTheme{
    private Font font;
    private final String fontName;
    private final float yOff;
    public FontStandardTheme(String name, Color background, Color color, float rgbTint, float rgbSat, String fontName){
        this(name, background, color, rgbTint, rgbSat, fontName, 0);
    }
    public FontStandardTheme(String name, Color background, Color color, float rgbTint, float rgbSat, String fontName, float yOff){
        super(name, background, color, rgbTint, rgbSat);
        this.fontName = fontName;
        this.yOff = yOff;
    }
    private Font getFont(){
        if(font==null)font = Font.loadFont(ResourceManager.loadData(ResourceManager.getInternalResource(fontName)), yOff);
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