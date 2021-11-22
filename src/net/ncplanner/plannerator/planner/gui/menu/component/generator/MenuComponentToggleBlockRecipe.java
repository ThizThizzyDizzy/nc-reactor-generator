package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.planner.Core;
import static org.lwjgl.glfw.GLFW.*;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.gui.Component;

public class MenuComponentToggleBlockRecipe extends Component{
    public boolean enabled = false;
    public int min = 0;
    public int max = 0;

    private final String displayName;
    private final Image inputTexture;
    private final Image inputDisplayTexture;

    public MenuComponentToggleBlockRecipe(String displayName, Image inputTexture, Image inputDisplayTexture) {
        super(0, 0, 0, 32);
        this.displayName = displayName;
        this.inputTexture = inputTexture;
        this.inputDisplayTexture = inputDisplayTexture;
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        if(enabled){
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverSelectedComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getSelectedComponentColor(Core.getThemeIndex(this)));
        }else{
            if(isMouseFocused)renderer.setColor(Core.theme.getMouseoverComponentColor(Core.getThemeIndex(this)));
            else renderer.setColor(Core.theme.getComponentColor(Core.getThemeIndex(this)));
        }
        renderer.fillRect(x, y, x+width, y+height);
        if(inputTexture!=null){
            renderer.setWhite();
            renderer.drawImage(inputDisplayTexture, x, y, x+height, y+height);
        }
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer, displayName);
    }
    public void drawText(Renderer renderer, String text){
        if(Core.isControlPressed()){
            if(max==0){
                text = min+"+";
            }else if(min==max){
                text = min+"";
            }else{
                text = min+"-"+max;
            }
        }
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-(inputTexture!=null?height:0))/textLength);
        float textHeight = (int)(height*scale)-1;
        renderer.drawText(inputTexture!=null?x+height:x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public boolean onScroll(double dx, double dy){
        if(super.onScroll(dx, dy))return true;
        if(isMouseFocused&&Core.isControlPressed()){
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
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS){
            enabled = !enabled;
        }
    }
}