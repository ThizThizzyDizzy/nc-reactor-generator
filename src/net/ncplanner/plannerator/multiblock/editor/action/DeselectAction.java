package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class DeselectAction extends Action<Multiblock>{
    private final int id;
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final Editor editor;
    public DeselectAction(Editor editor, int id, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(!editor.isSelected(id, i[0], i[1], i[2]))it.remove();
        }
        this.sel.addAll(sel);
        this.id = id;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
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
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).addAll(sel);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}