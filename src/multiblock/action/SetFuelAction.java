package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.configuration.underhaul.fissionsfr.Fuel;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.editor.Editor;
public class SetFuelAction extends Action<UnderhaulSFR>{
    private Fuel was = null;
    private final Editor editor;
    private final Fuel recipe;
    public SetFuelAction(Editor editor, Fuel recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void doApply(UnderhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.fuel;
        multiblock.fuel = recipe;
        editor.setUnderhaulFuel(multiblock.getConfiguration().underhaul.fissionSFR.allFuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void doUndo(UnderhaulSFR multiblock){
        multiblock.fuel = was;
        editor.setUnderhaulFuel(multiblock.getConfiguration().underhaul.fissionSFR.allFuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void getAffectedBlocks(UnderhaulSFR multiblock, ArrayList<Block> blocks){}
}