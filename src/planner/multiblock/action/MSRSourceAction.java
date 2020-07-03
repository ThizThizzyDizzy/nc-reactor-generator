package planner.multiblock.action;
import java.util.ArrayList;
import planner.configuration.overhaul.fissionmsr.Source;
import planner.multiblock.Action;
import planner.multiblock.overhaul.fissionmsr.Block;
import planner.multiblock.Multiblock;
public class MSRSourceAction extends Action{
    private final Block block;
    private Source was = null;
    private final Source source;
    public MSRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void doApply(Multiblock multiblock){
        was = block.source;
        block.source = source;
    }
    @Override
    public void doUndo(Multiblock multiblock){
        block.source = was;
    }
    @Override
    protected void getAffectedBlocks(ArrayList blocks){
        blocks.add(block);
    }
}