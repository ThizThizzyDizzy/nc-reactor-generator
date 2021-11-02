package net.ncplanner.plannerator.multiblock;
public class BoundingBox{
    public final int x1;
    public final int y1;
    public final int z1;
    public final int x2;
    public final int y2;
    public final int z2;
    public BoundingBox(int x1, int y1, int z1, int x2, int y2, int z2){
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    public int getWidth(){
        return x2-x1+1;
    }
    public int getHeight(){
        return y2-y1+1;
    }
    public int getDepth(){
        return z2-z1+1;
    }
}