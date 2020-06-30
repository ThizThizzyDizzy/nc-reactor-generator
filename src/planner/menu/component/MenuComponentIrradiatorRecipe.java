package planner.menu.component;
import planner.Core;
import planner.configuration.overhaul.fissionsfr.IrradiatorRecipe;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentIrradiatorRecipe extends MenuComponent{
    public final IrradiatorRecipe recipe;
    public MenuComponentIrradiatorRecipe(IrradiatorRecipe recipe){
        super(0, 0, 0, 0);
        color = Core.theme.getButtonColor();
        selectedColor = Core.theme.getSelectedMultiblockColor();
        foregroundColor = Core.theme.getTextColor();
        this.recipe = recipe;
    }
    @Override
    public void render(){
        if(isMouseOver&&!isSelected)Core.applyAverageColor(color, selectedColor);
        drawRect(x, y, x+width, y+height, 0);
        Core.applyColor(foregroundColor);
        drawText();
    }
    public void drawText(){
        double textLength = FontManager.getLengthForStringWithHeight(recipe.name, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.name);
    }
    @Override
    public boolean mouseWheelChange(int wheelChange){
        return parent.mouseWheelChange(wheelChange);
    }
}