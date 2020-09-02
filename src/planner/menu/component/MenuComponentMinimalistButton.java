package planner.menu.component;
import java.awt.Color;
import java.util.function.Supplier;
import planner.Core;
import simplelibrary.font.FontManager;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuComponentMinimalistButton extends MenuComponentButton{
    private final boolean darker;
    private Supplier<Color> textColor = () -> {
        return Core.theme.getTextColor();
    };
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover){
        this(x, y, width, height, label, enabled, useMouseover, false);
    }
    public MenuComponentMinimalistButton(double x, double y, double width, double height, String label, boolean enabled, boolean useMouseover, boolean darker){
        super(x, y, width, height, label, enabled, useMouseover);
        this.darker = darker;
        textInset+=5;
    }
    public MenuComponentMinimalistButton setTextColor(Supplier<Color> color){
        textColor = color;
        return this;
    }
    @Override
    public void render(){
        Color col = darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor();
        if(enabled){
            if(isPressed)col = col.darker();
            else if(isMouseOver)col = col.brighter();
        }else{
            col = col.darker();
        }
        Core.applyColor(col);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(textColor.get());
        drawText();
    }
    public void drawText(){
        String text = label;
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, (width-textInset*2)/textLength);
        double textHeight = (int)((height-textInset*2)*scale)-4;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
//        if(height>width){
//            drawCenteredText(x, y+height/2-width/2, x+width, y+height/2+width/2, label);
//        }
//        drawCenteredText(x, y+textInset, x+width, y+height-textInset, label);
    }
}