package planner.menu.component;
import java.awt.Color;
import java.util.function.Supplier;
import planner.Core;
import simplelibrary.font.FontManager;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentLabel extends MenuComponent{
    private final String text;
    private final boolean darker;
    private double textInset = 5;
    private Supplier<Color> textColor = () -> {
        return Core.theme.getTextColor();
    };
    public MenuComponentLabel(double x, double y, double width, double height, String label){
        this(x, y, width, height, label, false);
    }
    public MenuComponentLabel(double x, double y, double width, double height, String label, boolean darker){
        super(x, y, width, height);
        this.text = label;
        this.darker = darker;
    }
    public MenuComponentLabel setTextColor(Supplier<Color> color){
        textColor = color;
        return this;
    }
    @Override
    public void render(){
        Core.applyColor(darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(textColor.get());
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(text, height)+4;
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = (int)((height-textInset*2)*scale);
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}