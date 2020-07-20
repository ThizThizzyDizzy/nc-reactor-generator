package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
public class SetblocksAction extends Action<Multiblock>{
    public final ArrayList<int[]> locations = new ArrayList<>();
    public final Block block;
    private final HashMap<int[], Block> was = new HashMap<>();
    public SetblocksAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock){
        for(int[] loc : locations){
            was.put(loc, multiblock.blocks[loc[0]][loc[1]][loc[2]]);
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = block==null?null:block.copy(loc[0], loc[1], loc[2]);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.blocks[loc[0]][loc[1]][loc[2]] = was.get(loc);
        }
    }
    public void add(int x, int y, int z){
        locations.add(new int[]{x,y,z});
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        for(int[] loc : locations){
            Block b = multiblock.getBlock(loc[0], loc[1], loc[2]);
            if(b!=null)blocks.add(b);
        }
    }
}