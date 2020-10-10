package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Multiblock;
import multiblock.configuration.Block;
import planner.Core;
import planner.menu.MenuEdit;
public class CopyAction extends Action<Multiblock>{
    private final MenuEdit editor;
    private final ArrayList<int[]> selection = new ArrayList<>();
    private final HashMap<int[], Block> was = new HashMap<>();
    private final int dx;
    private final int dy;
    private final int dz;
    public CopyAction(MenuEdit editor, Collection<int[]> selection, int dy, int dx, int dz){
        this.selection.addAll(selection);
        this.dx = dy;
        this.dy = dx;
        this.dz = dz;
        this.editor = editor;
    }
    @Override
    public void apply(Multiblock multiblock, boolean allowUndo){
        ArrayList<int[]> movedSelection = new ArrayList<>();
        for(int[] loc : selection){
            int[] movedLoc = new int[]{loc[0]+dx, loc[1]+dy, loc[2]+dz};
            Block to = multiblock.getBlock(movedLoc[0], movedLoc[1], movedLoc[2]);
            if(to==null||!to.isCasing()){
                movedSelection.add(movedLoc);
                was.put(new int[]{movedLoc[0], movedLoc[1], movedLoc[2]}, to);
            }
        }
        for(int[] loc : selection){
            Block to = multiblock.getBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz);
            Block bl = multiblock.getBlock(loc[0], loc[1], loc[2]);
            for(int[] i : was.keySet()){
                if(i[0]==loc[0]&&i[1]==loc[1]&&i[2]==loc[2])bl = was.get(i);
            }
            if(to==null||!to.isCasing()){
                multiblock.setBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz, bl);
            }
        }
        synchronized(editor.selection){
            if(!Core.isShiftPressed())editor.selection.clear();
        }
        editor.addSelection(movedSelection);
    }
    @Override
    public void undo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.setBlock(loc[0], loc[1], loc[2], was.get(loc));
        }
        synchronized(editor.selection){
            editor.selection.clear();
            editor.selection.addAll(selection);
        }
    }
}