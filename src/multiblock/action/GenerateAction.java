package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.Block;
import multiblock.BlockPos;
import multiblock.Multiblock;
public class GenerateAction extends Action<Multiblock>{
    private final Multiblock multiblock;
    private HashMap<BlockPos, Block> was = new HashMap();
    public GenerateAction(Multiblock multiblock){
        this.multiblock = multiblock;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        multiblock.forEachPosition((x, y, z) -> {
            if(allowUndo)was.put(new BlockPos(x,y,z),multiblock.getBlock(x, y, z));
            Block block = this.multiblock.getBlock(x, y, z);
            multiblock.setBlock(x, y, z, block);
        });
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(BlockPos pos : was.keySet()){
            multiblock.setBlockExact(pos.x, pos.y, pos.z, was.get(pos));
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        //TODO only list the actually affected blocks
    }
}