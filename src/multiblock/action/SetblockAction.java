package multiblock.action;
import multiblock.Action;
import multiblock.Multiblock;
import multiblock.configuration.Block;
public class SetblockAction extends Action<Multiblock>{
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
    public void apply(Multiblock multiblock, boolean allowUndo){
        if(allowUndo)was = multiblock.getBlock(x, y, z);
        multiblock.setBlock(x, y, z, block);
    }
    @Override
    public void undo(Multiblock multiblock){
        multiblock.setBlock(x, y, z, was);
    }
}