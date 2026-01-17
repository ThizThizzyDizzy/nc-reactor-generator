package net.ncplanner.plannerator.multiblock;
import java.util.function.Consumer;
public class BlockPos{
    public static int taxicabDistance(BlockPos a, BlockPos b){
        return Math.abs(a.x-b.x)+Math.abs(a.y-b.y)+Math.abs(a.z-b.z);
    }
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
    public static void forEachInCell(int x1, int y1, int z1, int x2, int y2, int z2, Consumer<BlockPos> onCorner, Consumer<BlockPos> onEdge, Consumer<BlockPos> onFace, Consumer<BlockPos> onCenter){
        Consumer<BlockPos>[] consumers = new Consumer[]{onCenter, onFace, onEdge, onCorner};
        BoundingBox.around(new BlockPos(x1,y1,z1), new BlockPos(x2,y2,z2)).forEachPosition((pos)->{
            int cornerness = 0;
            if(pos.x==x1||pos.x==x2)cornerness++;
            if(pos.y==y1||pos.y==y2)cornerness++;
            if(pos.z==z1||pos.z==z2)cornerness++;
            if(consumers[cornerness]!=null)
                consumers[cornerness].accept(pos);
        });
    }
    @Override
    public String toString(){
        return "("+x+","+y+","+z+")";
    }
    public BlockPos offset(int x, int y, int z){
        return new BlockPos(this.x+x, this.y+y, this.z+z);
    }
    public BlockPos offset(Direction d){
        return offset(d.x, d.y, d.z);
    }
    public BlockPos offset(Direction d, int i){
        return offset(d.x*i, d.y*i, d.z*i);
    }
    public BlockPos offset(BlockPos pos){
        return offset(pos.x,pos.y,pos.z);
    }
    public BlockPos offset(Axis axis, int i){
        return offset(axis.x*i, axis.y*i, axis.z*i);
    }
    public BlockPos offset(BlockPos p, int i){
        return offset(p.x*i, p.y*i, p.z*i);
    }
    public int taxicab(BlockPos pos){
        return Math.abs(x-pos.x)+Math.abs(y-pos.y)+Math.abs(z-pos.z);
    }
}
