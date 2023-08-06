package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRAllShieldsAction extends Action<OverhaulMSR>{
    private HashMap<Block, Boolean> was = new HashMap<>();
    private final boolean close;
    public MSRAllShieldsAction(boolean close){
        this.close = close;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        for(Block b : multiblock.getBlocks()){
            if(b.template.neutronShield!=null){
                if(allowUndo)was.put(b, b.isToggled());
                b.setToggled(close);
            }
        }
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        for(Block b : was.keySet()){
            b.setToggled(was.get(b));
        }
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.AbstractBlock> blocks){
        for(Block block : multiblock.getBlocks()){
            if(block.template.neutronShield!=null)blocks.add(block);
        }
    }
}