package planner.multiblock.action;
import java.util.ArrayList;
import planner.Core;
import planner.configuration.overhaul.fissionsfr.CoolantRecipe;
import planner.menu.MenuEdit;
import planner.multiblock.Action;
import planner.multiblock.Block;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SetCoolantRecipeAction extends Action<OverhaulSFR>{
    private CoolantRecipe was = null;
    private final MenuEdit editor;
    private final CoolantRecipe recipe;
    public SetCoolantRecipeAction(MenuEdit editor, CoolantRecipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulSFR multiblock){
        was = multiblock.coolantRecipe;
        multiblock.coolantRecipe = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fissionSFR.coolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        multiblock.coolantRecipe = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fissionSFR.coolantRecipes.indexOf(((OverhaulSFR)multiblock).coolantRecipe));
    }
    @Override
    protected void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}