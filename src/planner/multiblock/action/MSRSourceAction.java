package planner.multiblock.action;
import java.util.ArrayList;
import planner.configuration.overhaul.fissionmsr.Source;
import planner.multiblock.Action;
import planner.multiblock.overhaul.fissionmsr.Block;
import planner.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block block;
    private Source was = null;
    private final Source source;
    public MSRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulMSR multiblock){
        was = block.source;
        block.source = source;
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        block.source = was;
    }
    @Override
    protected void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<planner.multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}