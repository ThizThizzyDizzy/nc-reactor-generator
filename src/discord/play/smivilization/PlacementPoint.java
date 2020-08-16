package discord.play.smivilization;
public class PlacementPoint{
    public final Object parent;
    public final int x;
    public final int y;
    public final int z;
    public final int vx;
    public final int vy;
    public final float scale;
    public PlacementPoint(HutThing parent, int x, int y, int z, int vx, int vy, float scale){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.scale = scale;
    }
    public PlacementPoint(Hut parent, int x, int y, int z, int vx, int vy, float scale){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.scale = scale;
    }
}