package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.Recipe;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fusion.OverhaulFusionReactor;
import net.ncplanner.plannerator.planner.editor.Editor;
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