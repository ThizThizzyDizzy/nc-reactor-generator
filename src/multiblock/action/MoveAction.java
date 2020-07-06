package multiblock.action;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
import planner.menu.MenuEdit;
public class MoveAction extends Action<Multiblock>{
    private final MenuEdit editor;
    private final ArrayList<int[]> selection = new ArrayList<>();
    private final HashMap<int[], Block> was = new HashMap<>();
    private final int dx;
    private final int dy;
    private final int dz;
    public MoveAction(MenuEdit editor, Collection<int[]> selection, int dy, int dx, int dz){
        this.selection.addAll(selection);
        this.dx = dy;
        this.dy = dx;
        this.dz = dz;
        this.editor = editor;
    }
    @Override
    public void doApply(Multiblock multiblock){
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
            was.put(loc, multiblock.getBlock(loc[0], loc[1], loc[2]));
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = null;
        }
        for(int[] loc : selection){
            Block to = multiblock.getBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz);
            Block bl = null;
            for(int[] i : was.keySet()){
                if(i[0]==loc[0]&&i[1]==loc[1]&&i[2]==loc[2])bl = was.get(i);
            }
            if(to==null||!to.isCasing()){
                multiblock.setBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz, bl);
            }
        }
        editor.selection.clear();
        editor.selection.addAll(movedSelection);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = was.get(loc);
        }
        editor.selection.clear();
        editor.selection.addAll(selection);
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        for(int[] loc : selection){
            blocks.add(multiblock.getBlock(loc[0], loc[1], loc[2]));
            Block to = multiblock.getBlock(loc[0]+dx, loc[1]+dy, loc[2]+dz);
            if(to==null)continue;
            if(!to.isCasing()&&!blocks.contains(to)){
                blocks.add(to);
            }
        }
    }
}