package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class ClearSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    private final MenuEdit editor;
    public ClearSelectionAction(MenuEdit editor){
        this.editor = editor;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.selection){
            if(allowUndo)was.addAll(editor.selection);
            editor.selection.clear();
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.selection){
            editor.selection.addAll(was);
            was.clear();
        }
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}