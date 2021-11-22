package net.ncplanner.plannerator.planner.editor;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.editor.action.SetblocksAction;
import net.ncplanner.plannerator.planner.Task;
import net.ncplanner.plannerator.planner.editor.suggestion.Suggestion;
import net.ncplanner.plannerator.planner.editor.tool.EditorTool;
import net.ncplanner.plannerator.graphics.image.Color;
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
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fusion.BlockRecipe getSelectedOverhaulFusionBlockRecipe(int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe getSelectedOverhaulSFRBlockRecipe(int id);
    public net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe getSelectedOverhaulMSRBlockRecipe(int id);
    public ArrayList<Suggestion> getSuggestions();
    public Task getTask();
}