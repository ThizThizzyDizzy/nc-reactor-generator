package planner.menu.component.editor;
import multiblock.configuration.overhaul.fissionmsr.Fuel;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverMSRFuel extends MenuComponent{
    public final Fuel fuel;
    public MenuComponentOverMSRFuel(Fuel fuel){
        super(0, 0, 0, 0);
        this.fuel = fuel;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(Core.theme.getButtonColor(), Core.theme.getSelectedMultiblockColor());
        else Core.applyColor(isSelected?Core.theme.getSelectedMultiblockColor():Core.theme.getButtonColor());
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(Core.theme.getTextColor());
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(fuel.name, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, fuel.name);
    }
    @Override    
    public String getTooltip(){
        return "Efficiency: "+fuel.efficiency+"\n"
             + "Base Heat: "+fuel.heat+"\n"
             + "Criticality: "+fuel.criticality+"\n"
             + "Base Time: "+fuel.time+(fuel.selfPriming?"\nSelf-Priming":"");
    }
}