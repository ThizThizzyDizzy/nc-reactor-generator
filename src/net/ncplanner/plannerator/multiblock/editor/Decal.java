package net.ncplanner.plannerator.multiblock.editor;
import net.ncplanner.plannerator.graphics.Renderer;
public abstract class Decal{
    public final int x;
    public final int y;
    public final int z;
    public Decal(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract void render(Renderer renderer, float x, float y, float blockSize);
    public abstract void render3D(Renderer renderer, float x, float y, float z, float blockSize);
    public abstract String getTooltip();
}