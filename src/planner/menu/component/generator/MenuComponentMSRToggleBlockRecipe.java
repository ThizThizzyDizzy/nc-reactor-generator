package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentMSRToggleBlockRecipe extends MenuComponentToggleBlockRecipe{
    public final BlockRecipe recipe;
    public MenuComponentMSRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}