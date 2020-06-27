package planner.menu.component;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Fuel;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentUnderFuel extends MenuComponent{
    private final Fuel fuel;
    public MenuComponentUnderFuel(Fuel fuel){
        super(0, 0, 0, 0);
        color = Core.theme.getButtonColor();
        selectedColor = Core.theme.getSelectedMultiblockColor();
        foregroundColor = Core.theme.getTextColor();
        this.fuel = fuel;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(color, selectedColor);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(foregroundColor);
        drawText();
    }
    public void drawText(){
        drawCenteredText(x, y+height*.2, x+width, y+height*.8, fuel.name);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}