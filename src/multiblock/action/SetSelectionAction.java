package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class SetSelectionAction extends Action<Multiblock>{
    public final ArrayList<int[]> was = new ArrayList<>();
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final MenuEdit editor;
    public SetSelectionAction(MenuEdit editor, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(i[0]<0||i[1]<0||i[2]<0||i[0]>=editor.multiblock.getX()||i[1]>=editor.multiblock.getY()||i[2]>=editor.multiblock.getZ())it.remove();
        }
        this.sel.addAll(sel);
    }
    @Override
    protected void doApply(Multiblock multiblock){
        was.addAll(editor.selection);
        editor.selection.clear();
        editor.selection.addAll(sel);
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        editor.selection.clear();
        editor.selection.addAll(was);
        was.clear();
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}