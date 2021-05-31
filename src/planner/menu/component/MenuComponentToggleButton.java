package planner.menu.component;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;

public class MenuComponentToggleButton extends MenuComponent{
    public final String label;
    public boolean enabled = false;
    public MenuComponentToggleButton(double x, double y, double width, double height, String label, boolean enabled){
        super(0, 0, 0, 32);
        this.label = label;
        this.enabled = enabled;
    }
    @Override
    public void render(){
        if(enabled){
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseOver)Core.applyColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else Core.applyColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(label, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, label);
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed){
            enabled = !enabled;
        }
    }
}