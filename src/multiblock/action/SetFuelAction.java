package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import planner.menu.MenuEdit;
import multiblock.Action;
import multiblock.Block;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
public class SetFuelAction extends Action<UnderhaulSFR>{
    private Fuel was = null;
    private final MenuEdit editor;
    private final Fuel recipe;
    public SetFuelAction(MenuEdit editor, Fuel recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(UnderhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.fuel;
        multiblock.fuel = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().underhaul.fissionSFR.allFuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void doUndo(UnderhaulSFR multiblock){
        multiblock.fuel = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(multiblock.getConfiguration().underhaul.fissionSFR.allFuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void getAffectedBlocks(UnderhaulSFR multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}