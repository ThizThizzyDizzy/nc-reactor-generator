package planner.multiblock.action;
import planner.configuration.overhaul.fissionsfr.Source;
import planner.multiblock.Action;
import planner.multiblock.overhaul.fissionsfr.Block;
import planner.multiblock.Multiblock;
public class SFRSourceAction implements Action{
    private final Block block;
    private Source was = null;
    private final Source source;
    public SFRSourceAction(Block block, Source source){
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