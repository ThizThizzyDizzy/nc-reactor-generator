package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.BlockElement;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block vessel;
    private BlockPos pWas;
    private Block was;
    private final BlockElement source;
    public MSRSourceAction(Block cell, BlockElement source){
        this.vessel = cell;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(vessel.source!=null){
            if(allowUndo){
                pWas = vessel.source.pos;
                was = vessel.source;
            }
            multiblock.setBlock(vessel.source.pos, source==null?null:new Block(multiblock.getConfiguration(), vessel.source.pos, source));
        }else{
            if(source==null)return;
            Block bWas = vessel.addNeutronSource(multiblock, source);
            if(bWas==null)return;
            if(allowUndo){
                pWas = bWas.pos;
                was = bWas.template.neutronSource!=null?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        if(pWas!=null)multiblock.setBlockExact(pWas, was);
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.AbstractBlock> blocks){
        blocks.add(multiblock.getBlock(vessel.pos));
    }
}