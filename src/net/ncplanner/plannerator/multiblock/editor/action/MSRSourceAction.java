package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block vessel;
    private int[] pWas;
    private Block was;
    private final net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block source;
    public MSRSourceAction(Block cell, net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.Block source){
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
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(vessel.x, vessel.y, vessel.z));
    }
}