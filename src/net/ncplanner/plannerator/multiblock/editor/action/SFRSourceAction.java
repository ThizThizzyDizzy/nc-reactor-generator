package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class SFRSourceAction extends Action<OverhaulSFR>{
    private final Block cell;
    private BlockPos pWas;
    private Block was;
    private final BlockElement source;
    public SFRSourceAction(Block cell, BlockElement source){
        this.cell = cell;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulSFR multiblock, boolean allowUndo){
        if(cell.source!=null){
            if(allowUndo){
                pWas = cell.source.pos;
                was = cell.source;
            }
            multiblock.setBlock(cell.source.pos, source==null?null:new Block(multiblock.getConfiguration(), cell.source.pos, source));
        }else{
            if(source==null)return;
            Block bWas = cell.addNeutronSource(multiblock, source);
            if(bWas==null)return;
            if(allowUndo){
                pWas = bWas.pos;
                was = bWas.template.neutronSource!=null?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        if(pWas!=null)multiblock.setBlockExact(pWas, was);
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.AbstractBlock> blocks){
        blocks.add(multiblock.getBlock(cell.pos));
    }
}