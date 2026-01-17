package net.ncplanner.plannerator.planner.editor;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.BlockPos;
public class ClipboardEntry{
    public final BlockPos pos;
    public final AbstractBlock block;
    public ClipboardEntry(BlockPos pos, AbstractBlock b){
        this.pos = pos;
        this.block = b;
    }
}