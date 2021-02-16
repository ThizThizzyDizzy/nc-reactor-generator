package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
public class SetblocksAction extends Action<Multiblock>{
    public final HashSet<int[]> locations = new HashSet<>();
    public final Block block;
    private final HashMap<int[], Block> was = new HashMap<>();
    public SetblocksAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        for(int[] loc : locations){
            if(allowUndo)was.put(loc, multiblock.getBlock(loc[0], loc[1], loc[2]));
            multiblock.setBlock(loc[0], loc[1], loc[2], block);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(int[] loc : was.keySet()){
            multiblock.setBlockExact(loc[0], loc[1], loc[2], was.get(loc));
        }
    }
    public SetblocksAction add(int x, int y, int z){
        locations.add(new int[]{x,y,z});
        return this;
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        for(int[] loc : locations){
            Block b = multiblock.getBlock(loc[0], loc[1], loc[2]);
            if(b!=null)blocks.add(b);
        }
    }
    public boolean isEmpty(){
        return locations.isEmpty();
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof SetblocksAction){
            SetblocksAction other = (SetblocksAction)obj;
            if(block==null&&other.block!=null)return false;
            if(block==null||block.isEqual(other.block)){
                return locations.equals(other.locations);
            }
        }
        return false;
    }
}