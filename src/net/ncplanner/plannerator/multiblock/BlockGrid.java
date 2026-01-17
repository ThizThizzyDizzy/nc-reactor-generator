package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
public class BlockGrid<T extends AbstractBlock>{
    public final int x;
    public final int y;
    public final int z;
    private final AbstractBlock[][][] blocks;
    public BlockGrid(int x1, int y1, int z1, int x2, int y2, int z2){
        if(x2<x1||y2<y1||z2<z1)throw new IllegalArgumentException("BlockGrid can not have negative dimensions!");
        x = x1;
        y = y1;
        z = z1;
        blocks = new AbstractBlock[x2-x1+1][y2-y1+1][z2-z1+1];
    }
    public int getWidth(){
        return blocks.length;
    }
    public int getHeight(){
        return blocks[0].length;
    }
    public int getDepth(){
        return blocks[0][0].length;
    }
    public boolean contains(BlockPos pos){
        if(pos.x<this.x||pos.y<this.y||pos.z<this.z)return false;
        return pos.x-this.x<getWidth()&&pos.y-this.y<getHeight()&&pos.z-this.z<getDepth();
    }
    public T getBlock(BlockPos pos){
        if(!contains(pos))throw new IndexOutOfBoundsException("Position "+pos.toString()+" is not in this block grid! check contains(...) first!");
        return (T)blocks[x-this.x][y-this.y][z-this.z];
    }
    public BoundingBox getBoundingBox(){
        return new BoundingBox(x, y, z, x+getWidth()-1, y+getHeight()-1, z+getDepth()-1);
    }
    public ArrayList<T> getBlocks(){
        ArrayList<T> blox = new ArrayList<>();
        getBoundingBox().forEachPosition(pos->{
            T block = getBlock(pos);
            if(block!=null)blox.add(block);
        });
        return blox;
    }
    public void setBlock(BlockPos pos, T block){
        if(!contains(pos))throw new IndexOutOfBoundsException("Position "+pos.toString()+" is not in this block grid! check contains(...) first!");
        blocks[x-this.x][y-this.y][z-this.z] = block;
    }
    public int getVolume(){
        return getWidth()*getHeight()*getDepth();
    }
}