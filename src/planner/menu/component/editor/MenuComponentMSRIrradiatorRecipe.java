package planner.menu.component.editor;
import planner.Core;
import multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMSRIrradiatorRecipe extends MenuComponent{
    public final IrradiatorRecipe recipe;
    public MenuComponentMSRIrradiatorRecipe(IrradiatorRecipe recipe){
        super(0, 0, 0, 0);
        this.recipe = recipe;
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
        double textLength = FontManager.getLengthForStringWithHeight(recipe.name, height);
        double scale = Math.min(1, width/textLength);
        double textHeight = (int)(height*scale)-1;
        drawCenteredText(x, y+height/2-textHeight/2, x+width, y+height/2+textHeight/2, recipe.name);
    }
}