package multiblock.action;
import java.util.ArrayList;
import java.util.HashMap;
import multiblock.Action;
import multiblock.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRAllShieldsAction extends Action<OverhaulSFR>{
    private HashMap<Block, Boolean> was = new HashMap<>();
    private final boolean close;
    public SFRAllShieldsAction(boolean close){
        this.close = close;
    }
    @Override
    public void doApply(OverhaulSFR multiblock){
        for(Block b : multiblock.getBlocks()){
            if(b.template.shield){
                was.put(b, b.closed);
                b.closed = close;
            }
        }
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        for(Block b : was.keySet()){
            b.closed = was.get(b);
        }
    }
    @Override
    protected void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<multiblock.Block> blocks){
        for(Block block : multiblock.getBlocks()){
            if(block.template.shield)blocks.add(block);
        }
    }
}