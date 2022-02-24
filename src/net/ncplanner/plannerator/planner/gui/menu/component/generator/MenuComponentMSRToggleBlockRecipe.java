package net.ncplanner.plannerator.planner.gui.menu.component.generator;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe;
public class MenuComponentMSRToggleBlockRecipe extends MenuComponentToggleBlockRecipe{
    public final BlockRecipe recipe;
    public MenuComponentMSRToggleBlockRecipe(BlockRecipe recipe){
        super(recipe.getInputDisplayName(), recipe.inputTexture, recipe.inputDisplayTexture);
        this.recipe = recipe;
    }
}