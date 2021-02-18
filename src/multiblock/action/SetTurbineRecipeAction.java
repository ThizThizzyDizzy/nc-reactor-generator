package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.configuration.overhaul.turbine.Recipe;
import multiblock.overhaul.turbine.OverhaulTurbine;
import planner.editor.Editor;
public class SetTurbineRecipeAction extends Action<OverhaulTurbine>{
    private Recipe was = null;
    private final Editor editor;
    private final Recipe recipe;
    public SetTurbineRecipeAction(Editor editor, Recipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulTurbine multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.recipe;
        multiblock.recipe = recipe;
        editor.setTurbineRecipe(multiblock.getConfiguration().overhaul.turbine.allRecipes.indexOf(((OverhaulTurbine)multiblock).recipe));
    }
    @Override
    public void doUndo(OverhaulTurbine multiblock){
        multiblock.recipe = was;
        editor.setTurbineRecipe(multiblock.getConfiguration().overhaul.turbine.allRecipes.indexOf(((OverhaulTurbine)multiblock).recipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulTurbine multiblock, ArrayList<Block> blocks){}
}