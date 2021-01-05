package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.editor.Editor;
public class SetSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final Editor editor;
    public SetSelectionAction(Editor editor, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(i[0]<0||i[1]<0||i[2]<0||i[0]>=editor.getMultiblock().getX()||i[1]>=editor.getMultiblock().getY()||i[2]>=editor.getMultiblock().getZ())it.remove();
        }
        this.sel.addAll(sel);
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection()){
            if(allowUndo)was.addAll(editor.getSelection());
            editor.getSelection().clear();
            editor.getSelection().addAll(sel);
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection()){
            editor.getSelection().clear();
            editor.getSelection().addAll(was);
            was.clear();
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}