package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionsfr.Fuel;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentSFRToggleFuel extends MenuComponent{
    public final Fuel fuel;
    public boolean enabled = false;
    public int min = 0;
    public int max = 0;
    public MenuComponentSFRToggleFuel(Fuel fuel){
        super(0, 0, 0, 32);
        this.fuel = fuel;
    }
    @Override
    public void render(){
        if(isMouseOver&&!enabled)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(enabled?Core.theme.getSelectedMultiblockColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText(fuel.name);
    }
    public void drawText(String text){
        if(Core.isControlPressed()){
            if(max==0){
                text = min+"+";
            }else if(min==max){
                text = min+"";
            }else{
                text = min+"-"+max;
            }
        }
        double textLength = FontManager.getLengthForStringWithHeight(text, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public boolean onMouseScrolled(double x, double y, double dx, double dy){
        if(super.onMouseScrolled(x, y, dx, dy))return true;
        if(isMouseOver&&Core.isControlPressed()){
            if(Core.isShiftPressed()){
                min+=dy;
            }else{
                max+=dy;
            }
            if(min<0)min = 0;
            if(max<0)max = 0;
            if(min>max)min = max;
            return true;
        }
        return false;
    }
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed){
            enabled = !enabled;
        }
    }
}