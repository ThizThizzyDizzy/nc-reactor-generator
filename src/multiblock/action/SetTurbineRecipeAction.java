package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.overhaul.turbine.Recipe;
import planner.menu.MenuEdit;
import multiblock.Action;
import multiblock.Block;
import multiblock.overhaul.turbine.OverhaulTurbine;
public class SetTurbineRecipeAction extends Action<OverhaulTurbine>{
    private Recipe was = null;
    private final MenuEdit editor;
    private final Recipe recipe;
    public SetTurbineRecipeAction(MenuEdit editor, Recipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulTurbine multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.recipe;
        multiblock.recipe = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().overhaul.turbine.allRecipes.indexOf(((OverhaulTurbine)multiblock).recipe));
    }
    @Override
    public void doUndo(OverhaulTurbine multiblock){
        multiblock.recipe = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().overhaul.turbine.allRecipes.indexOf(((OverhaulTurbine)multiblock).recipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulTurbine multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}