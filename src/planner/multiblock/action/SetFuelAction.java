package planner.multiblock.action;
import java.util.ArrayList;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Fuel;
import planner.menu.MenuEdit;
import planner.multiblock.Action;
import planner.multiblock.Block;
import planner.multiblock.underhaul.fissionsfr.UnderhaulSFR;
public class SetFuelAction extends Action<UnderhaulSFR>{
    private Fuel was = null;
    private final MenuEdit editor;
    private final Fuel recipe;
    public SetFuelAction(MenuEdit editor, Fuel recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(UnderhaulSFR multiblock){
        was = multiblock.fuel;
        multiblock.fuel = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void doUndo(UnderhaulSFR multiblock){
        multiblock.fuel = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    protected void getAffectedBlocks(UnderhaulSFR multiblock, ArrayList<Block> blocks){
        blocks.addAll(multiblock.getBlocks());
    }
}