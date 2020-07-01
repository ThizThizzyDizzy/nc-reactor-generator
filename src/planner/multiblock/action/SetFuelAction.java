package planner.multiblock.action;
import planner.Core;
import planner.configuration.underhaul.fissionsfr.Fuel;
import planner.menu.MenuEdit;
import planner.multiblock.Action;
import planner.multiblock.underhaul.fissionsfr.UnderhaulSFR;
public class SetFuelAction implements Action<UnderhaulSFR>{
    private Fuel was = null;
    private final MenuEdit editor;
    private final Fuel recipe;
    public SetFuelAction(MenuEdit editor, Fuel recipe){
        this.editor = editor;
        this.recipe = recipe;
    }
    @Override
    public void apply(UnderhaulSFR multiblock){
        was = multiblock.fuel;
        multiblock.fuel = recipe;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
    @Override
    public void undo(UnderhaulSFR multiblock){
        multiblock.fuel = was;
        editor.underFuelOrCoolantRecipe.setSelectedIndex(Core.configuration.underhaul.fissionSFR.fuels.indexOf(((UnderhaulSFR)multiblock).fuel));
    }
}