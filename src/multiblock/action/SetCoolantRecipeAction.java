package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.menu.MenuEdit;
import multiblock.Action;
import multiblock.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SetCoolantRecipeAction extends Action<OverhaulSFR>{
    private CoolantRecipe was = null;
    private final MenuEdit editor;
    private final CoolantRecipe recipe;
    public SetCoolantRecipeAction(MenuEdit editor, CoolantRecipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.coolantRecipe;
        multiblock.coolantRecipe = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().overhaul.fissionSFR.allCoolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        multiblock.coolantRecipe = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().overhaul.fissionSFR.allCoolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}