package net.ncplanner.plannerator.multiblock.symmetry;
import net.ncplanner.plannerator.multiblock.BlockPosConsumer;
import net.ncplanner.plannerator.multiblock.BoundingBox;
public abstract class Symmetry{
    public void apply(int x, int y, int z, BoundingBox bbox, BlockPosConsumer consumer){
        apply(x, y, z, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), consumer);
    }
    public abstract void apply(int x, int y, int z, int w, int h, int d, BlockPosConsumer consumer);
}