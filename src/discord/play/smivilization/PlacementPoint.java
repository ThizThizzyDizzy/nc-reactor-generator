package discord.play.smivilization;
public class PlacementPoint{
    public final Object parent;
    public final Wall wall;
    public final int x;
    public final int y;
    public final int z;
    public final int vx;
    public final int vy;
    public PlacementPoint(HutThing parent, Wall wall, int x, int y, int z){
        this.parent = parent;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.z = z;
        double[] xy = Hut.convertXYZtoXY512(x, y, z);
        this.vx = (int)xy[0];
        this.vy = (int)xy[1];
    }
    public PlacementPoint(Hut parent, Wall wall, int x, int y, int z){
        this.parent = parent;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.z = z;
        double[] xy = Hut.convertXYZtoXY512(x, y, z);
        this.vx = (int)xy[0];
        this.vy = (int)xy[1];
    }
    @Deprecated
    public PlacementPoint(HutThing parent, Wall wall, int x, int y, int z, int vx, int vy){
        this.parent = parent;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
    }
    @Deprecated
    public PlacementPoint(Hut parent, Wall wall, int x, int y, int z, int vx, int vy){
        this.parent = parent;
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
    }
}