package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockElement;
public class SFRSourceAction extends Action<OverhaulSFR>{
    private final Block cell;
    private int[] pWas;
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
                pWas = new int[]{cell.source.x,cell.source.y,cell.source.z};
                was = cell.source;
            }
            multiblock.setBlock(cell.source.x, cell.source.y, cell.source.z, source==null?null:new Block(multiblock.getConfiguration(), cell.source.x, cell.source.y, cell.source.z, source));
        }else{
            if(source==null)return;
            Block bWas = cell.addNeutronSource(multiblock, source);
            if(bWas==null)return;
            if(allowUndo){
                pWas = new int[]{bWas.x,bWas.y,bWas.z};
                was = bWas.template.neutronSource!=null?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        if(pWas!=null)multiblock.setBlockExact(pWas[0], pWas[1], pWas[2], was);
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<net.ncplanner.plannerator.multiblock.AbstractBlock> blocks){
        blocks.add(multiblock.getBlock(cell.x, cell.y, cell.z));
    }
}