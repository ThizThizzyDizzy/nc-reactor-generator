package planner.menu.component;
import java.awt.Color;
import planner.Core;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
public class MenuComponentMinimalistSlider extends MenuComponentSlider{
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, int minimum, int maximum, int initial, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, enabled, .6f);
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, double minimum, double maximum, double initial, int digits, boolean enabled){
        this(x, y, width, height, name, minimum, maximum, initial, digits, enabled, .6f);
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, int minimum, int maximum, int initial, boolean enabled, float tint){
        super(x, y, width, height, minimum, maximum, initial, enabled);
        color = Core.theme.getButtonColor(tint);
        foregroundColor = Core.theme.getTextColor();
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        updateSlider();
    }
    public MenuComponentMinimalistSlider(double x, double y, double width, double height, String name, double minimum, double maximum, double initial, int digits, boolean enabled, float tint){
        super(x, y, width, height, minimum, maximum, initial, digits, enabled);
        color = Core.theme.getButtonColor(tint);
        foregroundColor = Core.theme.getTextColor();
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
    public void mouseEvent(double x, double y, int button, boolean isDown){
        super.mouseEvent(x, y, button, isDown);
        if(button==0&&isDown==true&&enabled){
            isPressed = true;
            updateSlider(x);
        }else if(button==0&&isDown==false&&isPressed&&enabled){
            isPressed = false;
        }
    }
    @Override
    public void render(){
        if(textInset<0){
            textInset = height/10;
        }
        Color col = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        if(enabled){
            if(isPressed)col = col.darker().darker();
            else if(isMouseOver)col = col.brighter();
        }else{
            col = col.darker().darker();
        }
        col = col.brighter();
        Core.applyColor(color);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(col);
        drawRect(x+sliderX, y, x+sliderX+sliderHeight, y+sliderHeight, 0);
        Core.applyColor(foregroundColor);
        drawCenteredText(x+textInset, y+sliderHeight+textInset, x+width-textInset, y+height-textInset, name+": "+getValueS());
    }
    @Override
    public void mouseover(double x, double y, boolean isMouseOver){
        super.mouseover(x, y, isMouseOver);
        if(!isMouseOver){
            isPressed = false;
        }
    }
    @Override
    public void mouseDragged(double x, double y, int button){
        super.mouseDragged(x, y, button);
        if(button==0&&enabled){
            updateSlider(x);
        }
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
}