package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
public class MenuComponentMSRToggleBlockRecipe extends MenuComponentToggleBlockRecipe{
    public final BlockRecipe recipe;
    public MenuComponentMSRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}