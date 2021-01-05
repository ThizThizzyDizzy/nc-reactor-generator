package planner.editor;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Multiblock;
import multiblock.action.SetblocksAction;
public interface Editor{
    public Multiblock getMultiblock();
    public ArrayList<int[]> getSelection();
    public void addSelection(ArrayList<int[]> sel);
    public boolean isSelected(int x, int y, int z);
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
    public void clearSelection();
    public void select(int x1, int y1, int z1, int x2, int y2, int z2);
    public void copySelection(int x, int y, int z);
    public void cutSelection(int x, int y, int z);
    public Block getSelectedBlock();
    public void setblocks(SetblocksAction set);
    public void cloneSelection(int x, int y, int z);
    public void moveSelection(int x, int y, int z);
    public void pasteSelection(int x, int y, int z);
    public ArrayList<ClipboardEntry> getClipboard();
    public void selectGroup(int x, int y, int z);
    public void deselectGroup(int x, int y, int z);
    public void selectCluster(int x, int y, int z);
    public void deselectCluster(int x, int y, int z);
    public void deselect(int x1, int y1, int z1, int x2, int y2, int z2);
}