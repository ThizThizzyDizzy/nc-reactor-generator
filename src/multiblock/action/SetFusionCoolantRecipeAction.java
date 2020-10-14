package multiblock.action;
import java.util.ArrayList;
import planner.Core;
import multiblock.configuration.overhaul.fusion.CoolantRecipe;
import planner.menu.MenuEdit;
import multiblock.Action;
import multiblock.Block;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
public class SetFusionCoolantRecipeAction extends Action<OverhaulFusionReactor>{
    private CoolantRecipe was = null;
    private final MenuEdit editor;
    private final CoolantRecipe recipe;
    public SetFusionCoolantRecipeAction(MenuEdit editor, CoolantRecipe recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(OverhaulFusionReactor multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.coolantRecipe;
        multiblock.coolantRecipe = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fusion.allCoolantRecipes.indexOf(((OverhaulFusionReactor)multiblock).coolantRecipe));
    }
    @Override
    public void doUndo(OverhaulFusionReactor multiblock){
        multiblock.coolantRecipe = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.overhaul.fusion.allCoolantRecipes.indexOf(((OverhaulFusionReactor)multiblock).coolantRecipe));
    }
    @Override
    protected void getAffectedBlocks(OverhaulFusionReactor multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}