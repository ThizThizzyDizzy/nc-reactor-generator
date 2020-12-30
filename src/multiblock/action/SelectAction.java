package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class SelectAction extends Action<Multiblock>{
    public final ArrayList<int[]> sel = new ArrayList<>();
    private final MenuEdit editor;
    public SelectAction(MenuEdit editor, Collection<int[]> sel){
        this.editor = editor;
        for (Iterator<int[]> it = sel.iterator(); it.hasNext();) {
            int[] i = it.next();
            if(editor.isSelected(i[0], i[1], i[2])||i[0]<0||i[1]<0||i[2]<0||i[0]>=editor.multiblock.getX()||i[1]>=editor.multiblock.getY()||i[2]>=editor.multiblock.getZ())it.remove();
        }
        this.sel.addAll(sel);
    }
    @Override
    protected void doApply(Multiblock multiblock, boolean allowUndo){
        synchronized(editor.selection){
            editor.selection.addAll(sel);
        }
    }
    @Override
    protected void doUndo(Multiblock multiblock){
        synchronized(editor.selection){
            for(int[] i : sel){
                for (Iterator<int[]> it = editor.selection.iterator(); it.hasNext();) {
                    int[] s = it.next();
                    if(s[0]==i[0]&&s[1]==i[1]&&s[2]==i[2])it.remove();
                }
            }
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){}
}