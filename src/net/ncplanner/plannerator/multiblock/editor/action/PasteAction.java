package net.ncplanner.plannerator.multiblock.editor.action;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.editor.Action;
import net.ncplanner.plannerator.planner.editor.ClipboardEntry;
public class PasteAction extends Action<Multiblock>{
    private final ArrayList<AbstractBlock> was = new ArrayList<>();
    private final ArrayList<BlockPos> wasAir = new ArrayList<>();
    private final BlockPos pos;
    private final ArrayList<ClipboardEntry> blocks;
    public PasteAction(ArrayList<ClipboardEntry> blocks, BlockPos pos){
        this.blocks = blocks;
        this.pos = pos;
    }
    @Override
    public void doApply(Multiblock multiblock, boolean allowUndo){
        was.clear();
        for(ClipboardEntry entry : blocks){
            BlockPos p = entry.pos.offset(pos);
            if(!multiblock.contains(p))continue;
            if(allowUndo){
                AbstractBlock bl = multiblock.getBlock(p);
                if(bl!=null)was.add(bl);
                else wasAir.add(p);
            }
            multiblock.setBlock(p, entry.block);
        }
    }
    @Override
    public void doUndo(Multiblock multiblock){
        for(AbstractBlock b : was){
            multiblock.setBlockExact(b.pos, b);
        }
        for(BlockPos loc : wasAir){
            multiblock.setBlockExact(loc, null);
        }
    }
    @Override
    public void getAffectedBlocks(Multiblock multiblock, ArrayList<AbstractBlock> blocks){
        for(ClipboardEntry entry : this.blocks){
            if(multiblock.contains(entry.pos.offset(pos))){
                AbstractBlock block = multiblock.getBlock(entry.pos.offset(pos));
                if(block==null)continue;
                if(!blocks.contains(block)){
                    blocks.add(block);
                }
            }
        }
    }
}