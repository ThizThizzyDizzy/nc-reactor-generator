package net.ncplanner.plannerator.multiblock.symmetry;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
public abstract class Symmetry{
    public void apply(BlockPos pos, BoundingBox bbox, Consumer<BlockPos> consumer){
        apply(pos, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), consumer);
    }
    public abstract void apply(BlockPos pos, int w, int h, int d, Consumer<BlockPos> consumer);
}