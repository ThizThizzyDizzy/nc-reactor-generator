package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
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
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}