package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionmsr.Source;
import multiblock.Action;
import multiblock.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block block;
    private Source was = null;
    private final Source source;
    public MSRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(allowUndo)was = block.source;
        block.source = source;
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        block.source = was;
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}