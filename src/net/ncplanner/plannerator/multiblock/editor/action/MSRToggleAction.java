package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
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
        if(allowUndo)was = block.isToggled();
        block.setToggled(!block.isToggled());
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        block.setToggled(was);
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<AbstractBlock> blocks){
        blocks.add(multiblock.getBlock(block.pos));
    }
}