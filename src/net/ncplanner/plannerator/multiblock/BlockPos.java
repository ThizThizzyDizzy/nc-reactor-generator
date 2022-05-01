package net.ncplanner.plannerator.multiblock;
public class BlockPos{
    public final int x;
    public final int y;
    public final int z;
    public BlockPos(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public boolean equals(Object obj){
        if(obj instanceof BlockPos){
            return x==((BlockPos)obj).x&&y==((BlockPos)obj).y&&z==((BlockPos)obj).z;
        }
        return false;
    }
    @Override
    public int hashCode(){
        int hash = 7;
        hash = 89*hash+this.x;
        hash = 89*hash+this.y;
        hash = 89*hash+this.z;
        return hash;
    }
}