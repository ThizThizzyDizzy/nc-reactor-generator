package planner.multiblock.action;
import java.util.ArrayList;
import planner.multiblock.Action;
import planner.multiblock.Block;
import planner.multiblock.Multiblock;
public class SetblockAction extends Action{
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
    protected void getAffectedBlocks(ArrayList blocks){
        blocks.add(block);
    }
}