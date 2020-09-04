package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRShieldAction extends Action<OverhaulMSR>{
    private final Block block;
    private boolean was;
    public MSRShieldAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(allowUndo)was = block.closed;
        block.closed = !block.closed;
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        block.closed = was;
    }
    @Override
    protected void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}