package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.turbine.Recipe;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.planner.editor.Editor;
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