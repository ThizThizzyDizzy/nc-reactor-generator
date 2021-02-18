package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.configuration.overhaul.fusion.Recipe;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.editor.Editor;
public class SetFusionRecipeAction extends Action<OverhaulFusionReactor>{
    private Recipe was = null;
    private final Editor editor;
    private final Recipe recipe;
    public SetFusionRecipeAction(Editor editor, Recipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulFusionReactor multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.recipe;
        multiblock.recipe = recipe;
        editor.setFusionRecipe(multiblock.getConfiguration().overhaul.fusion.allRecipes.indexOf(((OverhaulFusionReactor)multiblock).recipe));
    }
    @Override
    public void doUndo(OverhaulFusionReactor multiblock){
        multiblock.recipe = was;
        editor.setFusionRecipe(multiblock.getConfiguration().overhaul.fusion.allRecipes.indexOf(((OverhaulFusionReactor)multiblock).recipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulFusionReactor multiblock, ArrayList<Block> blocks){}
}