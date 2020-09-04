package multiblock.action;
import java.util.ArrayList;
import multiblock.configuration.overhaul.fissionsfr.Source;
import multiblock.Action;
import multiblock.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRSourceAction extends Action<OverhaulSFR>{
    private final Block block;
    private Source was = null;
    private final Source source;
    public SFRSourceAction(Block block, Source source){
        this.block = block;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = block.source;
        block.source = source;
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        block.source = was;
    }
    @Override
    protected void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}