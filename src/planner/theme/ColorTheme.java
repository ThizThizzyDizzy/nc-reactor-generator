package planner.theme;
import static planner.Core.applyColor;
import simplelibrary.image.Color;
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
}