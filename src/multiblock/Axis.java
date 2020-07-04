package multiblock;
public enum Axis{
    X(1,0,0),
    Y(0,1,0),
    Z(0,0,1);
    public final int x;
    public final int y;
    public final int z;
    private Axis(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}