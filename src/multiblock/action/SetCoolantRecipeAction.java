package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import planner.editor.Editor;
public class SetCoolantRecipeAction extends Action<OverhaulSFR>{
    private CoolantRecipe was = null;
    private final Editor editor;
    private final CoolantRecipe recipe;
    public SetCoolantRecipeAction(Editor editor, CoolantRecipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.coolantRecipe;
        multiblock.coolantRecipe = recipe;
        editor.setCoolantRecipe(multiblock.getConfiguration().overhaul.fissionSFR.allCoolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        multiblock.coolantRecipe = was;
        editor.setCoolantRecipe(multiblock.getConfiguration().overhaul.fissionSFR.allCoolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<Block> blocks){}
}