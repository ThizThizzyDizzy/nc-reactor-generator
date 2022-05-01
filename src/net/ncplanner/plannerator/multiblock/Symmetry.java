package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
import java.util.HashSet;
public class Symmetry{
    public boolean mx;
    public boolean my;
    public boolean mz;
    public boolean rx180;
    public boolean ry180;
    public boolean rz180;
    public Symmetry(){
        this(false,false,false,false,false,false);
    }
    public Symmetry(boolean mx, boolean my, boolean mz, boolean rx180, boolean ry180, boolean rz180){
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.rx180 = rx180;
        this.ry180 = ry180;
        this.rz180 = rz180;
    }
    public void apply(int x, int y, int z, BoundingBox bbox, BlockPosConsumer consumer){
        apply(x, y, z, bbox.getWidth(), bbox.getHeight(), bbox.getDepth(), consumer);
    }
    public void apply(int x, int y, int z, int w, int h, int d, BlockPosConsumer consumer){
        HashSet<BlockPos> positions = new HashSet<>();
        positions.add(new BlockPos(x, y, z));
        if(mx){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, p.y, p.z));
            });
            positions.addAll(newPositions);
        }
        if(my){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, h-p.y-1, p.z));
            });
            positions.addAll(newPositions);
        }
        if(mz){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, p.y, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(rx180){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(p.x, h-p.y-1, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(ry180){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, p.y, d-p.z-1));
            });
            positions.addAll(newPositions);
        }
        if(rz180){
            ArrayList<BlockPos> newPositions = new ArrayList<>();
            positions.forEach((p) -> {
                newPositions.add(new BlockPos(w-p.x-1, h-p.y-1, p.z));
            });
            positions.addAll(newPositions);
        }
        positions.forEach((p) -> {
            if(p.x<0||p.y<0||p.z<0||p.x>=w||p.y>=h||p.z>=d)return;
            consumer.accept(p.x, p.y, p.z);
        });
    }
}