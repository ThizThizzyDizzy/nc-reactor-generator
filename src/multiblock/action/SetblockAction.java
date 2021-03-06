package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.Block;
import multiblock.CuboidalMultiblock;
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
    public SetblockAction(int x, int y, int z, CuboidalMultiblock currentMultiblock, Block newInstance){
        throw new UnsupportedOperationException("Not supported yet.");
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
    @Override
    public boolean equals(Object obj){
        if(obj instanceof SetblockAction){
            SetblockAction other = (SetblockAction)obj;
            if(block==null&&other.block!=null)return false;
            return x==other.x&&y==other.y&&z==other.z&&(block==null||block.isEqual(other.block));
        }
        return false;
    }
}