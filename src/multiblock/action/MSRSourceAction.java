package multiblock.action;
import java.util.ArrayList;
import multiblock.Action;
import multiblock.overhaul.fissionmsr.Block;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
public class MSRSourceAction extends Action<OverhaulMSR>{
    private final Block cell;
    private int[] pWas;
    private Block was;
    private final multiblock.configuration.overhaul.fissionmsr.Block source;
    public MSRSourceAction(Block cell, multiblock.configuration.overhaul.fissionmsr.Block source){
        this.cell = cell;
        this.source = source;
    }
    @Override
    public void doApply(OverhaulMSR multiblock, boolean allowUndo){
        if(cell.source!=null){
            if(allowUndo){
                pWas = new int[]{cell.source.x,cell.source.y,cell.source.z};
                was = cell.source;
            }
            multiblock.setBlock(cell.source.x, cell.source.y, cell.source.z, source==null?null:new Block(multiblock.getConfiguration(), cell.source.x, cell.source.y, cell.source.z, source));
        }else{
            if(source==null)return;
            Block bWas = cell.addNeutronSource(multiblock, source);
            if(allowUndo){
                pWas = new int[]{bWas.x,bWas.y,bWas.z};
                was = bWas.template.source?null:bWas;
            }
        }
    }
    @Override
    public void doUndo(OverhaulMSR multiblock){
        multiblock.setBlockExact(pWas[0], pWas[1], pWas[2], was);
    }
    @Override
    public void getAffectedBlocks(OverhaulMSR multiblock, ArrayList<multiblock.Block> blocks){
        blocks.add(multiblock.getBlock(cell.x, cell.y, cell.z));
    }
}