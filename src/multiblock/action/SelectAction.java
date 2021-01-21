package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.editor.Editor;
public class SelectAction extends Action<Multiblock>{
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final Editor editor;
    private final int id;
    public SelectAction(Editor editor, int id, Collection<int[]> sel){
        this.editor = editor;
        this.id = id;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(editor.isSelected(id, i[0], i[1], i[2])||i[0]<0||i[1]<0||i[2]<0||i[0]>=editor.getMultiblock().getX()||i[1]>=editor.getMultiblock().getY()||i[2]>=editor.getMultiblock().getZ())it.remove();
        }
        this.sel.addAll(sel);
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).addAll(sel);
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection(id)){
            for(int[] i : sel){
                for (Iterator<int[]> it = editor.getSelection(id).iterator(); it.hasNext();) {
                    int[] s = it.next();
                    if(s[0]==i[0]&&s[1]==i[1]&&s[2]==i[2])it.remove();
                }
            }
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}