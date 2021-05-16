package planner.theme;
import planner.theme.legacy.SolidColorTheme;
import simplelibrary.image.Color;
public class StandardTheme extends SolidColorTheme{
    public StandardTheme(String name, Color background, Color color, float rgbTint, float rgbSat){
        super(name, background, color, rgbTint, rgbSat);
    }
    @Override
    public Color getEditorMouseoverLineColor(){
        return getRGBA(1, .5f, 0, 1);
    }
}