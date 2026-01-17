package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class SelectAction extends Action<Multiblock>{
    public final ArrayList<BlockPos> sel = new ArrayList<>();
    private final Editor editor;
    private final int id;
    public SelectAction(Editor editor, int id, Collection<BlockPos> sel){
        this.editor = editor;
        this.id = id;
        for (Iterator<BlockPos> it = sel.iterator(); it.hasNext();) {
            BlockPos i = it.next();
            if(editor.isSelected(id, i)||!editor.getMultiblock().contains(i))it.remove();
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
            editor.getSelection(id).removeAll(sel);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){}
}