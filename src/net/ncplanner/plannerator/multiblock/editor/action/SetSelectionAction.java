package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class SetSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    private final int id;
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final Editor editor;
    public SetSelectionAction(Editor editor, int id, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(!editor.getMultiblock().contains(i[0], i[1], i[2]))it.remove();
        }
        this.sel.addAll(sel);
        this.id = id;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection(id)){
            if(allowUndo)was.addAll(editor.getSelection(id));
            editor.getSelection(id).clear();
            editor.getSelection(id).addAll(sel);
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).clear();
            editor.getSelection(id).addAll(was);
            was.clear();
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}