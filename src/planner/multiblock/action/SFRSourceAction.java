package planner.multiblock.action;
import java.util.ArrayList;
import planner.configuration.overhaul.fissionsfr.Source;
import planner.multiblock.Action;
import planner.multiblock.overhaul.fissionsfr.Block;
import planner.multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRSourceAction extends Action<OverhaulSFR>{
    private final Block block;
    private Source was = null;
    private final Source source;
    public SFRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulSFR multiblock){
        was = block.source;
        block.source = source;
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        block.source = was;
    }
    @Override
    protected void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<planner.multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}