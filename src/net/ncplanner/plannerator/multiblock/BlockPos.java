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
    public static void forEachInCell(int x1, int y1, int z1, int x2, int y2, int z2, BlockPosConsumer onCorner, BlockPosConsumer onEdge, BlockPosConsumer onFace, BlockPosConsumer onCenter){
        if(x1>x2){
            forEachInCell(x2, y1, z1, x1, y2, z2, onCorner, onEdge, onFace, onCenter);
            return;
        }
        if(y1>y2){
            forEachInCell(x1, y2, z1, x2, y1, z2, onCorner, onEdge, onFace, onCenter);
            return;
        }
        if(z1>z2){
            forEachInCell(x1, y1, z2, x2, y2, z1, onCorner, onEdge, onFace, onCenter);
            return;
        }
        BlockPosConsumer[] consumers = new BlockPosConsumer[]{onCenter, onFace, onEdge, onCorner};
        for(int x = x1; x<=x2; x++){
            for(int y = y1; y<=y2; y++){
                for(int z = z1; z<=z2; z++){
                    int cornerness = 0;
                    if(x==x1||x==x2)cornerness++;
                    if(y==y1||y==y2)cornerness++;
                    if(z==z1||z==z2)cornerness++;
                    if(consumers[cornerness]!=null)
                        consumers[cornerness].accept(x, y, z);
                }
            }
        }
    }
}
