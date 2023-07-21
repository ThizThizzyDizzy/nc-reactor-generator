package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.editor.Editor;
import net.ncplanner.plannerator.planner.ncpf.configuration.UnderhaulSFRConfiguration;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.Fuel;
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
        editor.setUnderhaulFuel(multiblock.getConfiguration().getConfiguration(UnderhaulSFRConfiguration::new).fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void doUndo(UnderhaulSFR multiblock){
        multiblock.fuel = was;
        editor.setUnderhaulFuel(multiblock.getConfiguration().getConfiguration(UnderhaulSFRConfiguration::new).fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void getAffectedBlocks(UnderhaulSFR multiblock, ArrayList<Block> blocks){}
}