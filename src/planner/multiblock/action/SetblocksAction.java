package planner.multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import planner.multiblock.Action;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
public class SetblocksAction implements Action{
    public final ArrayList<int[]> locations = new ArrayList<>();
    public final Block block;
    private final HashMap<int[], Block> was = new HashMap<>();
    public SetblocksAction(Block block){
        this.block = block;
    }
    @Override
    public void apply(Multiblock multiblock){
        for(int[] loc : locations){
            was.put(loc, multiblock.blocks[loc[0]][loc[1]][loc[2]]);
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = block==null?null:block.copy(loc[0], loc[1], loc[2]);
        }
    }
    @Override
    public void undo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = was.get(loc);
        }
    }
    public void add(int x, int y, int z){
        locations.add(new int[]{x,y,z});
    }
}