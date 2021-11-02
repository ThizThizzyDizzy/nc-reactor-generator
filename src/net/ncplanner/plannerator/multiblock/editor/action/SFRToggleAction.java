package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
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
        if(allowUndo)was = block.isToggled;
        block.isToggled = !block.isToggled;
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        block.isToggled = was;
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(block.x, block.y, block.z));
    }
}