package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRToggleAction extends Action<OverhaulSFR>{
    private final Block block;
    private boolean was;
    public SFRToggleAction(Block block){
        this.block = block;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        if(allowUndo)was = block.isToggled();
        block.setToggled(!block.isToggled());
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        block.setToggled(was);
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<AbstractBlock> blocks){
        blocks.add(multiblock.getBlock(block.pos));
    }
}