package net.ncplanner.plannerator.planner.gui.menu.component;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.Component;
import static org.lwjgl.glfw.GLFW.*;
public class Slider extends Component{
    public boolean enabled;
    public float textInset = -1;
    public double minimum;
    public double maximum;
    public double value;
    public final int digits;
    public boolean pressed;
    public float sliderHeight;
    public float maxSliderX;
    public float sliderX;    
    private final boolean darker;
    private String name;
    private ArrayList<Consumer<Double>> onChange = new ArrayList<>();
    public Slider(float x, float y, float width, float height, String name, int minimum, int maximum, int initial, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, enabled, false);
    }
    public Slider(float x, float y, float width, float height, String name, double minimum, double maximum, double initial, int digits, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, digits, enabled, false);
    }
    public Slider(float x, float y, float width, float height, String name, int minimum, int maximum, int initial, boolean enabled, boolean darker){
        super(x, y, width, height);
        this.darker = darker;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.value = initial;
        digits = 0;
        this.enabled = enabled;
        updateSlider();
    }
    public Slider(float x, float y, float width, float height, String name, double minimum, double maximum, double initial, int digits, boolean enabled, boolean darker){
        super(x, y, width, height);
        this.darker = darker;
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.value = initial;
        this.digits = digits;
        this.enabled = enabled;
        updateSlider();
    }
    @Override
    public void onMouseButton(double x, double y, int button, int action, int mods){
        if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_PRESS&&enabled){
            pressed = true;
            updateSlider(x);
        }else if(button==GLFW_MOUSE_BUTTON_LEFT&&action==GLFW_RELEASE){
            pressed = false;
        }
    }
    @Override
    public void draw(double deltaTime){
        Renderer renderer = new Renderer();
        updateSlider();
        sliderHeight = height/2;
        if(textInset==0)textInset = height/10;
        Color col;
        if(darker){
            col = Core.theme.getSecondarySliderColor();
            if(enabled){
                if(pressed)col = Core.theme.getSecondarySliderPressedColor();
                else if(isMouseFocused)col = Core.theme.getSecondarySliderMouseoverColor();
            }else{
                col = Core.theme.getSecondarySliderDisabledColor();
            }
        }else{
            col = Core.theme.getSliderColor();
            if(enabled){
                if(pressed)col = Core.theme.getSliderPressedColor();
                else if(isMouseFocused)col = Core.theme.getSliderMouseoverColor();
            }else{
                col = Core.theme.getSliderDisabledColor();
            }
        }
        renderer.setColor(darker?Core.theme.getSecondaryComponentColor(Core.getThemeIndex(this)):Core.theme.getComponentColor(Core.getThemeIndex(this)));
        renderer.fillRect(x, y, x+width, y+height);
        renderer.setColor(col);
        renderer.fillRect(x+sliderX, y, x+sliderX+sliderHeight, y+sliderHeight);
        renderer.setColor(Core.theme.getComponentTextColor(Core.getThemeIndex(this)));
        renderer.drawCenteredText(x+textInset, y+sliderHeight+textInset, x+width-textInset, y+height-textInset, name+": "+getValueS());
    }
    @Override
    public void onCursorMoved(double xpos, double ypos){
        if(pressed)updateSlider(xpos);
    }
    private void updateSlider(double x){
        x-=sliderHeight/2;
        double percent = x/maxSliderX;
        if(percent>1){
            percent = 1;
        }else if(percent<0){
            percent = 0;
        }
        value = percent*(maximum-minimum)+minimum;
        updateSlider();
    }
    private void updateSlider(){
        for(Consumer<Double> c : onChange)c.accept(value);
        sliderHeight = height/2;
        maxSliderX = width-sliderHeight;
        sliderX = 0;
        float percent = (float)((getValue()-minimum)/(maximum-minimum));
        sliderX = maxSliderX*percent;
    }
    public String getValueS(){
        if(Math.round(getValue())==getValue()){
            return ""+Math.round(getValue());
        }else{
            return ""+getValue();
        }
    }
    public double getValue(){
        if(digits==0){
            return Math.round(value);
        }else{
            return (double)Math.round(value*digits)/digits;
        }
    }
    public void setValue(double value){
        this.value = Math.min(maximum, Math.max(minimum, value));
        updateSlider();
    }
    @Override
    public Slider setTooltip(String tooltip){
        this.tooltip = tooltip;
        return this;
    }
    public Slider onChangeAsInt(Consumer<Integer> consumer){
        this.onChange.add((t) -> {
            consumer.accept((int)Math.round(t));
        });
        return this;
    }
    public Slider onChange(Consumer<Double> consumer){
        this.onChange.add(consumer);
        return this;
    }
}