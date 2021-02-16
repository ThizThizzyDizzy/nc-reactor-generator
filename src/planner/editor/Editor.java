package planner.editor;
import java.awt.Color;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.SetblocksAction;
import planner.Task;
import planner.editor.suggestion.Suggestion;
import planner.editor.tool.EditorTool;
public interface Editor{
    public Multiblock getMultiblock();
    public void action(Action action, boolean allowUndo);
    public ArrayList<int[]> getSelection(int id);
    public void addSelection(int id, ArrayList<int[]> sel);
    public boolean hasSelection(int id);
    public boolean isSelected(int id, int x, int y, int z);
    @Deprecated
    public void setCoolantRecipe(int idx);
    @Deprecated
    public void setUnderhaulFuel(int idx);
    @Deprecated
    public void setFusionCoolantRecipe(int idx);
    @Deprecated
    public void setFusionRecipe(int idx);
    @Deprecated
    public void setTurbineRecipe(int idx);
    /**
     * @deprecated use ClearSelectionAction instead?
     */
    @Deprecated
    public void clearSelection(int id);
    public void select(int id, int x1, int y1, int z1, int x2, int y2, int z2);
    public void copySelection(int id, int x, int y, int z);
    public void cutSelection(int id, int x, int y, int z);
    public Block getSelectedBlock(int id);
    public void setblocks(int id, SetblocksAction set);
    /**
     * @deprecated use CopyAction instead?
     */
    @Deprecated
    public void cloneSelection(int id, int x, int y, int z);
    /**
     * @deprecated use MoveAction instead?
     */
    @Deprecated
    public void moveSelection(int id, int x, int y, int z);
    public void pasteSelection(int id, int x, int y, int z);
    public ArrayList<ClipboardEntry> getClipboard(int id);
    public void selectGroup(int id, int x, int y, int z);
    public void deselectGroup(int id, int x, int y, int z);
    public void selectCluster(int id, int x, int y, int z);
    public void deselectCluster(int id, int x, int y, int z);
    public void deselect(int id, int x1, int y1, int z1, int x2, int y2, int z2);
    public EditorTool getSelectedTool(int id);
    public boolean isControlPressed(int id);
    public boolean isShiftPressed(int id);
    public boolean isAltPressed(int id);
    public Color convertToolColor(Color color, int id);
    public multiblock.configuration.overhaul.fusion.BreedingBlanketRecipe getSelectedFusionBreedingBlanketRecipe(int id);
    public multiblock.configuration.overhaul.fissionsfr.Fuel getSelectedOverSFRFuel(int id);
    public multiblock.configuration.overhaul.fissionsfr.IrradiatorRecipe getSelectedSFRIrradiatorRecipe(int id);
    public multiblock.configuration.overhaul.fissionmsr.Fuel getSelectedOverMSRFuel(int id);
    public multiblock.configuration.overhaul.fissionmsr.IrradiatorRecipe getSelectedMSRIrradiatorRecipe(int id);
    public ArrayList<Suggestion> getSuggestions();
    public Task getTask();
}