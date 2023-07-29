package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRAllShieldsAction extends Action<OverhaulSFR>{
    private HashMap<Block, Boolean> was = new HashMap<>();
    private final boolean close;
    public SFRAllShieldsAction(boolean close){
        this.close = close;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        for(Block b : multiblock.getBlocks()){
            if(b.template.neutronShield!=null){
                if(allowUndo)was.put(b, b.isToggled());
                b.setToggled(close);
            }
        }
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        for(Block b : was.keySet()){
            b.setToggled(was.get(b));
        }
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.AbstractBlock> blocks){
        for(Block block : multiblock.getBlocks()){
            if(block.template.neutronShield!=null)blocks.add(block);
        }
    }
}