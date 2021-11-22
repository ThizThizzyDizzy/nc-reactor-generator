package net.ncplanner.plannerator.planner.gui.menu.component;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class OptionButton extends Component{
    public static boolean ACT_ON_PRESS = false;
    public String label;
    public boolean enabled;
    public int startingIndex;
    public int currentIndex;
    public String[] options;
    public boolean isLeftPressed;
    public boolean isRightPressed;
    public float textInset = -1;
    public final boolean darker;
    public OptionButton(float x, float y, float width, float height, String label, boolean enabled, int startingOption, String... options){
        this(x, y, width, height, label, enabled, false, startingOption, options);
    }
    public OptionButton(float x, float y, float width, float height, String label, boolean enabled, boolean darker, int startingOption, String... options){
        super(x, y, width, height);
        this.label = label;
        this.darker = darker;
        this.enabled = enabled;
        this.startingIndex = startingOption;
        this.currentIndex = startingOption;
        this.options = options;
    }
    public boolean isChanged(){
        return currentIndex!=startingIndex;
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(!enabled)return;
        if(button==GLFW_MOUSE_BUTTON_LEFT){
            if(action==GLFW_PRESS){
                isLeftPressed = true;
                if(ACT_ON_PRESS)cycle(1);
            }
            if(action==GLFW_RELEASE){
                isLeftPressed = false;
                if(!ACT_ON_PRESS)cycle(1);
            }
        }
        if(button==GLFW_MOUSE_BUTTON_RIGHT){
            if(action==GLFW_PRESS){
                isRightPressed = true;
                if(ACT_ON_PRESS)cycle(-1);
            }
            if(action==GLFW_RELEASE){
                isRightPressed = false;
                if(!ACT_ON_PRESS)cycle(-1);
            }
        }
        super.onMouseButton(x, y, button, action, mods);
    }
    @Override
    public void onCursorExited(){
        super.onCursorExited();
        isLeftPressed = isRightPressed = false;
    }
    @Override
    public void draw(double deltaTime){
        super.draw(deltaTime);
        Renderer renderer = new Renderer();
        Color col;
        if(darker){
             col = Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isLeftPressed||isRightPressed)col = Core.theme.getSecondaryComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseFocused)col = Core.theme.getSecondaryComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getSecondaryComponentDisabledColor(Core.getThemeIndex(this));
            }
        }else{
            col = Core.theme.getComponentColor(Core.getThemeIndex(this));
            if(enabled){
                if(isLeftPressed||isRightPressed)col = Core.theme.getComponentPressedColor(Core.getThemeIndex(this));
                else if(isMouseFocused)col = Core.theme.getComponentMouseoverColor(Core.getThemeIndex(this));
            }else{
                col = Core.theme.getComponentDisabledColor(Core.getThemeIndex(this));
            }
        }
        renderer.setColor(col);
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        drawText(renderer);
    }
    public void cycle(int diff){
        currentIndex+=diff;
        while(currentIndex>=options.length){
            currentIndex -= options.length;
        }
        while(currentIndex<0){
            currentIndex += options.length;
        }
    }
    public int getIndex(){
        return currentIndex;
    }
    public void setIndex(int newIndex){
        currentIndex = newIndex;
        if(currentIndex>=options.length){
            currentIndex = 0;
        }
        if(currentIndex<0){
            currentIndex = options.length-1;
        }
    }
    public String getSelectedString(){
        return options[currentIndex];
    }
    public void drawText(Renderer renderer){
        String text = label+": "+getSelectedString();
        float textLength = renderer.getStringWidth(text, height);
        float scale = Math.min(1, (width-textInset*2)/textLength);
        float textHeight = (int)((height-textInset*2)*scale)-4;
        renderer.drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, text);
    }
    @Override
    public OptionButton setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
}