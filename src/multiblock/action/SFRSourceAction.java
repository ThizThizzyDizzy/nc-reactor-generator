package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionsfr.Block;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
public class SFRSourceAction extends Action<OverhaulSFR>{
    private final Block cell;
    private int[] pWas;
    private Block was;
    private final multiblock.configuration.overhaul.fissionsfr.Block source;
    public SFRSourceAction(Block cell, multiblock.configuration.overhaul.fissionsfr.Block source){
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
                was = bWas.template.source?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulSFR multiblock){
        if(pWas!=null)multiblock.setBlockExact(pWas[0], pWas[1], pWas[2], was);
    }
    @Override
    public void getAffectedBlocks(OverhaulSFR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(cell.x, cell.y, cell.z));
    }
}