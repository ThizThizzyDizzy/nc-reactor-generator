package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class ClearSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    private final MenuEdit editor;
    public ClearSelectionAction(MenuEdit editor){
        this.editor = editor;
    }
    @Override
    public void apply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.selection){
            if(allowUndo)was.addAll(editor.selection);
            editor.selection.clear();
        }
    }
    @Override
    public void undo(Multiblock multiblock){
        synchronized(editor.selection){
            editor.selection.addAll(was);
            was.clear();
        }
    }
}