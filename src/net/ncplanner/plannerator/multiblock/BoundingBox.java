package net.ncplanner.plannerator.multiblock;
import java.util.function.Consumer;
import java.util.function.Function;
public class BoundingBox{
    public static <T> BoundingBox around(Function<T, BoundingBox> func, T... objects){
        BoundingBox[] bboxes = new BoundingBox[objects.length];
        for(int i = 0; i<objects.length; i++)bboxes[i] = func.apply(objects[i]);
        return around(bboxes);
    }
    public static BoundingBox around(BoundingBox... bboxes){
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for(BoundingBox box : bboxes){
            minX = Math.min(minX, box.x1);
            minY = Math.min(minY, box.y1);
            minZ = Math.min(minZ, box.z1);
            maxX = Math.max(maxX, box.x2);
            maxY = Math.max(maxY, box.y2);
            maxZ = Math.max(maxZ, box.z2);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
    public static BoundingBox around(BlockPos... points){
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for(BlockPos point : points){
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            minZ = Math.min(minZ, point.z);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
            maxZ = Math.max(maxZ, point.z);
        }
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }
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
    public void forEachPosition(Consumer<BlockPos> consumer){
        for(int x = x1; x<=x2; x++){
            for(int y = y1; y<=y2; y++){
                for(int z = z1; z<=z2; z++){
                    consumer.accept(new BlockPos(x, y, z));
                }
            }
        }
    }
}