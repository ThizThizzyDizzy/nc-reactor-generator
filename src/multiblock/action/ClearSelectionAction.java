package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.editor.Editor;
public class ClearSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    private final Editor editor;
    private final int id;
    public ClearSelectionAction(Editor editor, int id){
        this.editor = editor;
        this.id = id;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection(id)){
            if(allowUndo)was.addAll(editor.getSelection(id));
            editor.getSelection(id).clear();
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).addAll(was);
            was.clear();
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}