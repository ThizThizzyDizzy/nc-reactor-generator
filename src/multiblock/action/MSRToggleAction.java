package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRToggleAction extends Action<OverhaulMSR>{
    private final Block block;
    private boolean was;
    public MSRToggleAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(allowUndo)was = block.isToggled;
        block.isToggled = !block.isToggled;
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        block.isToggled = was;
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}