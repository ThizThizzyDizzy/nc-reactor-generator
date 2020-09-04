package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class DeselectAction extends Action<Multiblock>{
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final MenuEdit editor;
    public DeselectAction(MenuEdit editor, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(!editor.isSelected(i[0], i[1], i[2]))it.remove();
        }
        this.sel.addAll(sel);
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        for(int[] i : sel){
            for (Iterator<int[]> it = editor.selection.iterator(); it.hasNext();) {
                int[] s = it.next();
                if(s[0]==i[0]&&s[1]==i[1]&&s[2]==i[2])it.remove();
            }
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        editor.selection.addAll(sel);
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}