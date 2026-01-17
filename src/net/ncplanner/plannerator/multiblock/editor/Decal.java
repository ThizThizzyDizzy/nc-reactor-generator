package net.ncplanner.plannerator.multiblock.editor;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.BlockPos;
public abstract class Decal{
    public final BlockPos pos;
    public Decal(BlockPos pos){
        this.pos = pos;
    }
    public abstract void render(Renderer renderer, float x, float y, float blockSize);
    public abstract void render3D(Renderer renderer, float x, float y, float z, float blockSize);
    public abstract String getTooltip();
}