package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRShieldAction extends Action<OverhaulSFR>{
    private final Block block;
    private boolean was;
    public SFRShieldAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(OverhaulSFR multiblock){
        was = block.closed;
        block.closed = !block.closed;
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        block.closed = was;
    }
    @Override
    protected void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}