package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.Multiblock;
public class SetblockAction extends Action{
    public final int x;
    public final int y;
    public final int z;
    public final Block block;
    private Block was = null;
    public SetblockAction(int x, int y, int z, Block block){
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.getBlock(x, y, z);
        multiblock.setBlockExact(x, y, z, block);
    }
    @Override
    public void doUndo(Multiblock multiblock){
        multiblock.setBlockExact(x, y, z, was);
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList blocks){
        Block b = multiblock.getBlock(x, y, z);
        if(b!=null)blocks.add(b);
    }
}