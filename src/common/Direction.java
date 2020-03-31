package common;
public enum Direction{
    XP(1,0,0),
    XN(-1,0,0),
    YP(0,1,0),
    YN(0,-1,0),
    ZP(0,0,1),
    ZN(0,0,-1);
    public final int x;
    public final int y;
    public final int z;
    private Direction(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}