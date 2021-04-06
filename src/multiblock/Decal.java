package multiblock;
import simplelibrary.opengl.Renderer2D;
public abstract class Decal extends Renderer2D{
    public final int x;
    public final int y;
    public final int z;
    public Decal(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract void render(double x, double y, double blockSize);
    public abstract void render3D(double x, double y, double z, double blockSize);
    public abstract String getTooltip();
}