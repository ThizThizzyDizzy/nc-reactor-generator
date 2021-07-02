package planner.menu.component.generator;
import multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
public class MenuComponentOverhaulSFRToggleBlockRecipe extends MenuComponentToggleBlockRecipe {
    public final BlockRecipe recipe;
    public MenuComponentOverhaulSFRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}