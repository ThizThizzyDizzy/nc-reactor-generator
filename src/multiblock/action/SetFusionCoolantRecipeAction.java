package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.editor.Editor;
public class SetFusionCoolantRecipeAction extends Action<OverhaulFusionReactor>{
    private CoolantRecipe was = null;
    private final Editor editor;
    private final CoolantRecipe recipe;
    public SetFusionCoolantRecipeAction(Editor editor, CoolantRecipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulFusionReactor multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.coolantRecipe;
        multiblock.coolantRecipe = recipe;
        editor.setFusionCoolantRecipe(multiblock.getConfiguration().overhaul.fusion.allCoolantRecipes.indexOf(((OverhaulFusionReactor)multiblock).coolantRecipe));
    }
    @Override
    public void doUndo(OverhaulFusionReactor multiblock){
        multiblock.coolantRecipe = was;
        editor.setFusionCoolantRecipe(multiblock.getConfiguration().overhaul.fusion.allCoolantRecipes.indexOf(((OverhaulFusionReactor)multiblock).coolantRecipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulFusionReactor multiblock, ArrayList<Block> blocks){}
}