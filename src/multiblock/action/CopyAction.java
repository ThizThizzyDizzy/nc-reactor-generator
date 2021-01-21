package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.Core;
import planner.editor.Editor;
public class CopyAction extends Action<Multiblock>{
    private final Editor editor;
    private final int id;
    private final ArrayList<int[]> selection = new ArrayList<>();
    private final HashMap<int[], Block> was = new HashMap<>();
    private final int dx;
    private final int dy;
    private final int dz;
    public CopyAction(Editor editor, int id, Collection<int[]> selection, int dy, int dx, int dz){
        this.selection.addAll(selection);
        this.dx = dy;
        this.dy = dx;
        this.dz = dz;
        this.editor = editor;
        this.id = id;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
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
        synchronized(editor.getSelection(id)){
            if(!Core.isShiftPressed())editor.getSelection(id).clear();
        }
        editor.addSelection(id, movedSelection);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.setBlockExact(loc[0], loc[1], loc[2], was.get(loc));
        }
        synchronized(editor.getSelection(id)){
            editor.getSelection(id).clear();
            editor.getSelection(id).addAll(selection);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        for(int[] loc : selection){
            Block from = multiblock.getBlock(loc[0], loc[1], loc[2]);
            if(from!=null)blocks.add(from);
            Block to = multiblock.getBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz);
            if(to==null)continue;
            if(!to.isCasing()&&!blocks.contains(to)){
                blocks.add(to);
            }
        }
    }
}