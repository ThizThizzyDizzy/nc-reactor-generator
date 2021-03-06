package planner.menu.component;
import java.util.function.Supplier;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.image.Color;
import static simplelibrary.opengl.Renderer2D.drawCenteredText;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuComponentMinimalistButton extends MenuComponentButton{
    public final boolean darker;
    private Supplier<Color> textColor = () -> {
        return Core.theme.getComponentTextColor(Core.getThemeIndex(this));
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
        Color col;
        if(darker){
             col = Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getSecondaryComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseOver)col = Core.theme.getSecondaryComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getSecondaryComponentDisabledColor(Core.getThemeIndex(this));
            }
        }else{
            col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseOver)col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
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
    }
    @Override
    public MenuComponentMinimalistButton setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
}