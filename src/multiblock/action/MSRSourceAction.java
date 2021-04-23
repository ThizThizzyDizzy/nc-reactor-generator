package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block vessel;
    private int[] pWas;
    private Block was;
    private final multiblock.configuration.overhaul.fissionmsr.Block source;
    public MSRSourceAction(Block cell, multiblock.configuration.overhaul.fissionmsr.Block source){
        this.vessel = cell;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(vessel.source!=null){
            if(allowUndo){
                pWas = new int[]{vessel.source.x,vessel.source.y,vessel.source.z};
                was = vessel.source;
            }
            multiblock.setBlock(vessel.source.x, vessel.source.y, vessel.source.z, source==null?null:new Block(multiblock.getConfiguration(), vessel.source.x, vessel.source.y, vessel.source.z, source));
        }else{
            if(source==null)return;
            Block bWas = vessel.addNeutronSource(multiblock, source);
            if(bWas==null)return;
            if(allowUndo){
                pWas = new int[]{bWas.x,bWas.y,bWas.z};
                was = bWas.template.source?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        if(pWas!=null)multiblock.setBlockExact(pWas[0], pWas[1], pWas[2], was);
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(vessel.x, vessel.y, vessel.z));
    }
}