package planner.menu.component;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.Fuel;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMSRToggleFuel extends MenuComponent{
    public final Fuel fuel;
    public boolean enabled = false;
    public int min = 0;
    public int max = 0;
    public MenuComponentMSRToggleFuel(Fuel fuel){
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
    public boolean mouseWheelChange(int wheelChange){
        int trueWheelChange = Core.calcWheelChange(wheelChange);
        if(isMouseOver&&Core.isControlPressed()){
            if(Core.isShiftPressed()){
                min+=trueWheelChange;
            }else{
                max+=trueWheelChange;
            }
            if(min<0)min = 0;
            if(max<0)max = 0;
            if(min>max)min = max;
        }
        return parent.mouseWheelChange(wheelChange);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(button==0&&pressed){
            enabled = !enabled;
        }
    }
}