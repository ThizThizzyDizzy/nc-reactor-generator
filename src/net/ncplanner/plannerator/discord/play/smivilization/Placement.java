package net.ncplanner.plannerator.discord.play.smivilization;
import java.util.UUID;
public class Placement{
    public final Wall wall;
    public final int x;
    public final int y;
    public final int z;
    public final int dimX;
    public final int dimY;
    public final int dimZ;
    public final UUID parent;
    public Placement(Wall wall, int x, int y, int z, int dimX, int dimY, int dimZ, UUID parent){
        this.wall = wall;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimX = dimX;
        this.dimY = dimY;
        this.dimZ = dimZ;
        this.parent = parent;
    }
    public Placement(HutThing thing, PlacementPoint point){
        this(point.wall, point.x, point.y, point.z, thing.getDimX(point.wall), thing.getDimY(point.wall), thing.getDimZ(point.wall), point.parent instanceof HutThing?((HutThing)point.parent).uuid:null);
    }
}