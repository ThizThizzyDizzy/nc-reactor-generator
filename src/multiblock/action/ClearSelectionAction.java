package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.editor.Editor;
public class ClearSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    private final Editor editor;
    public ClearSelectionAction(Editor editor){
        this.editor = editor;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection()){
            if(allowUndo)was.addAll(editor.getSelection());
            editor.getSelection().clear();
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection()){
            editor.getSelection().addAll(was);
            was.clear();
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}