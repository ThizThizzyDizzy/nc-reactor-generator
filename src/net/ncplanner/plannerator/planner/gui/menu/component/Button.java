package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class Button extends Component{
    public static boolean ACT_ON_PRESS = false;
    public String text;
    public boolean enabled, pressed;
    public int textInset = 5;
    public final boolean darker;
    private Supplier<Color> textColor = () -> {
        return Core.theme.getComponentTextColor(Core.getThemeIndex(this));
    };
    private ArrayList<Runnable> actions = new ArrayList<>();
    public Button(float x, float y, float width, float height, String text, boolean enabled){
        this(x, y, width, height, text, enabled, false);
    }
    public Button(float x, float y, float width, float height, String text, boolean enabled, boolean darker){
        super(x, y, width, height);
        this.text = text;
        this.enabled = enabled;
        this.darker = darker;
    }
    public Button setTextColor(Supplier<Color> color){
        textColor = color;
        return this;
    }
    @Override
    public void draw(double deltaTime){
        super.draw(deltaTime);
        Renderer renderer = new Renderer();
        Color col;
        if(darker){
             col = Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(pressed)col = Core.theme.getSecondaryComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseFocused)col = Core.theme.getSecondaryComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getSecondaryComponentDisabledColor(Core.getThemeIndex(this));
            }
        }else{
            col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(pressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseFocused)col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
        }
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(textColor.get());
        drawText(renderer, deltaTime);
    }
    public void drawText(Renderer renderer, double deltaTime){
        String text = this.text;
        float textLength = renderer.getStringWidth(text, height-textInset*2);
        if(textLength<0)return;
        float scale = Math.min(1, (width-textInset*2)/textLength);
        int textHeight = (int)((height-textInset*2)*scale);
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public Button setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        super.onMouseButton(x, y, button, action, mods);
        if(!enabled)return;
        if(button==GLFW_MOUSE_BUTTON_LEFT){
            if(action==GLFW_PRESS&&!pressed){
                pressed = true;
                if(ACT_ON_PRESS)runActions();
            }
            if(action==GLFW_RELEASE&&pressed){
                pressed = false;
                if(isMouseFocused&&!ACT_ON_PRESS)runActions();
            }
        }
    }
    public void runActions(){
        for(Runnable r : actions)r.run();
    }
    public Button addAction(Runnable action){
        actions.add(action);
        return this;
    }
}