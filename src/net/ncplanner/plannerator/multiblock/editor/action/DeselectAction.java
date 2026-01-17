package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.Editor;
public class DeselectAction extends Action<Multiblock>{
    private final int id;
    public final ArrayList<BlockPos> sel = new ArrayList<>();
    private final Editor editor;
    public DeselectAction(Editor editor, int id, Collection<BlockPos> sel){
        this.editor = editor;
        for (Iterator<BlockPos> it = sel.iterator(); it.hasNext();) {
            BlockPos pos = it.next();
            if(!editor.isSelected(id, pos))it.remove();
        }
        this.sel.addAll(sel);
        this.id = id;
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).removeAll(sel);
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).addAll(sel);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){}
}