package planner.multiblock.action;
import planner.configuration.overhaul.fissionmsr.Source;
import planner.multiblock.Action;
import planner.multiblock.overhaul.fissionmsr.Block;
import planner.multiblock.Multiblock;
public class MSRSourceAction implements Action{
    private final Block block;
    private Source was = null;
    private final Source source;
    public MSRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void apply(Multiblock multiblock){
        was = block.source;
        block.source = source;
    }
    @Override
    public void undo(Multiblock multiblock){
        block.source = was;
    }
}