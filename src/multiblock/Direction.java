package multiblock;
public enum Direction{
    PX(1,0,0),
    PY(0,1,0),
    PZ(0,0,1),
    NX(-1,0,0),
    NY(0,-1,0),
    NZ(0,0,-1);
    public final int x;
    public final int y;
    public final int z;
    private Direction(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}