package planner.menu.component;
import java.awt.Color;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
public class MenuComponentMinimalistSlider extends MenuComponentSlider{
    private final boolean darker;
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, int minimum, int maximum, int initial, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, enabled, false);
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, double minimum, double maximum, double initial, int digits, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, digits, enabled, false);
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, int minimum, int maximum, int initial, boolean enabled, boolean darker){
        super(x, y, width, height, minimum, maximum, initial, enabled);
        this.darker = darker;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        updateSlider();
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, double minimum, double maximum, double initial, int digits, boolean enabled, boolean darker){
        super(x, y, width, height, minimum, maximum, initial, digits, enabled);
        this.darker = darker;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        updateSlider();
    }
    private boolean isPressed;
    private double sliderHeight;
    private double maxSliderX;
    private double sliderX;
    private String name;
    private double minimum;
    private double maximum;
    private double value;
    @Override
    public void onMouseButton(double x, double y, int button, boolean pressed, int mods){
        super.onMouseButton(x, y, button, pressed, mods);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&pressed&&enabled){
            isPressed = true;
            updateSlider(x);
        }else if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&!pressed){
            isPressed = false;
        }
    }
    @Override
    public void render(){
        if(textInset<0){
            textInset = height/10;
        }
        Color col = darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor();
        if(enabled){
            if(isPressed)col = col.darker().darker();//TODO .darker()
            else if(isMouseOver)col = col.brighter();//TODO .brighter()
        }else{
            col = col.darker().darker();//TODO .darker()
        }
        col = col.brighter();
        Core.applyColor(darker?Core.theme.getDarkButtonColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(col);
        drawRect(x+sliderX, y, x+sliderX+sliderHeight, y+sliderHeight, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawCenteredText(x+textInset, y+sliderHeight+textInset, x+width-textInset, y+height-textInset, name+": "+getValueS());
    }
    @Override
    public void onMouseMove(double x, double y){
        if(isPressed)updateSlider(x);
    }
    @Override
    public void onMouseMovedElsewhere(double x, double y){
        if(isPressed)updateSlider(x);
    }
    private void updateSlider(double x){
        x-=sliderHeight/2;
        double percent = x/maxSliderX;
        if(percent>1){
            percent = 1;
        }else if(percent<0){
            percent = 0;
        }
        updateSlider();
    }
    private void updateSlider(){
        sliderHeight = height/2;
        maxSliderX = width-sliderHeight;
        sliderX = 0;
        double percent = (getValue()-minimum)/(maximum-minimum);
        sliderX = maxSliderX*percent;
    }
    @Override
    public MenuComponentMinimalistSlider setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
}