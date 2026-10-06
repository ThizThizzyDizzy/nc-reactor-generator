package net.ncplanner.plannerator.multiblock.editor;
import net.ncplanner.plannerator.multiblock.BlockPos;
public abstract class Decal{
    public final BlockPos pos;
    public Decal(BlockPos pos){
        this.pos = pos;
    }
    public /* abstract */ void render(float x, float y, float blockSize) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public /* abstract */ void render3D(float x, float y, float z, float blockSize) /* ; */ {
        throw new UnsupportedOperationException("Pending refactor");
    }
    public abstract String getTooltip();
}