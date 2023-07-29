package net.ncplanner.plannerator.planner.editor;
import net.ncplanner.plannerator.multiblock.AbstractBlock;
public class ClipboardEntry{
    public int x;
    public int y;
    public int z;
    public final AbstractBlock block;
    public ClipboardEntry(int[] xyz, AbstractBlock b){
        this(xyz[0], xyz[1], xyz[2], b);
    }
    public ClipboardEntry(int x, int y, int z, AbstractBlock b){
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = b;
    }
}