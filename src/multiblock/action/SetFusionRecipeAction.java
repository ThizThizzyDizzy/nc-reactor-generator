package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fusion.Recipe;
import planner.menu.MenuEdit;
import multiblock.Action;
import multiblock.Block;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
public class SetFusionRecipeAction extends Action<OverhaulFusionReactor>{
    private Recipe was = null;
    private final MenuEdit editor;
    private final Recipe recipe;
    public SetFusionRecipeAction(MenuEdit editor, Recipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulFusionReactor multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.recipe;
        multiblock.recipe = recipe;
        editor.overFuel.setSelectedIndex(multiblock.getConfiguration().overhaul.fusion.allRecipes.indexOf(((OverhaulFusionReactor)multiblock).recipe));
    }
    @Override
    public void doUndo(OverhaulFusionReactor multiblock){
        multiblock.recipe = was;
        editor.overFuel.setSelectedIndex(multiblock.getConfiguration().overhaul.fusion.allRecipes.indexOf(((OverhaulFusionReactor)multiblock).recipe));
    }
    @Override
    protected void getAffectedBlocks(OverhaulFusionReactor multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}