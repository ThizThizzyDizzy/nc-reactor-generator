package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuComponentOverhaulSFRToggleBlockRecipe extends MenuComponentToggleBlockRecipe {
    public final BlockRecipe recipe;
    public MenuComponentOverhaulSFRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}