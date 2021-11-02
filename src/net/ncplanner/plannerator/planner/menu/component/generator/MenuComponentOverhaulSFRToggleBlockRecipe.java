package net.ncplanner.plannerator.planner.menu.component.generator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe;
public class MenuComponentOverhaulSFRToggleBlockRecipe extends MenuComponentToggleBlockRecipe {
    public final BlockRecipe recipe;
    public MenuComponentOverhaulSFRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}