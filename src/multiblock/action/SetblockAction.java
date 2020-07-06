package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
public class SetblockAction extends Action<Multiblock>{
    private final int x;
    private final int y;
    private final int z;
    private final Block block;
    private Block was = null;
    public SetblockAction(int x, int y, int z, Block block){
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock){
        was = multiblock.blocks[x][y][z];
        multiblock.blocks[x][y][z] = block;
    }
    @Override
    public void doUndo(Multiblock multiblock){
        multiblock.blocks[x][y][z] = was;
    }
    @Override
    protected void getAffectedBlocks(Multiblock multiblock, ArrayList<Block> blocks){
        blocks.add(multiblock.getBlock(x, y, z));
    }
}