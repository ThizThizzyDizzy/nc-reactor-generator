package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe;
public class MenuComponentFusionToggleBlockRecipe extends MenuComponentToggleBlockRecipe{
    public final BlockRecipe recipe;
    public MenuComponentFusionToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}