package net.ncplanner.plannerator.multiblock;
import java.util.ArrayList;
public class BlockGrid<T extends Block>{
    public final int x;
    public final int y;
    public final int z;
    private final Block[][][] blocks;
    public BlockGrid(int x1, int y1, int z1, int x2, int y2, int z2){
        if(x2<x1||y2<y1||z2<z1)throw new IllegalArgumentException("BlockGrid can not have negative dimensions!");
        x = x1;
        y = y1;
        z = z1;
        blocks = new Block[x2-x1+1][y2-y1+1][z2-z1+1];
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
    public boolean contains(int x, int y, int z){
        if(x<this.x||y<this.y||z<this.z)return false;
        return x-this.x<getWidth()&&y-this.y<getHeight()&&z-this.z<getDepth();
    }
    public T getBlock(int x, int y, int z){
        if(!contains(x, y, z))throw new IndexOutOfBoundsException("Position ("+x+","+y+","+z+") is not in this block grid! check contains(...) first!");
        return (T)blocks[x-this.x][y-this.y][z-this.z];
    }
    public ArrayList<T> getBlocks(){
        ArrayList<T> blox = new ArrayList<>();
        for(int x = 0; x<getWidth(); x++){
            for(int y = 0; y<getHeight(); y++){
                for(int z = 0; z<getDepth(); z++){
                    T block = getBlock(this.x+x, this.y+y, this.z+z);
                    if(block!=null)blox.add(block);
                }
            }
        }
        return blox;
    }
    public void setBlock(int x, int y, int z, T block){
        if(!contains(x, y, z))throw new IndexOutOfBoundsException("Position ("+x+","+y+","+z+") is not in this block grid! check contains(...) first!");
        blocks[x-this.x][y-this.y][z-this.z] = block;
    }
    public int getVolume(){
        return getWidth()*getHeight()*getDepth();
    }
    public void forEachPosition(BlockPosConsumer func){
        for(int x = 0; x<getWidth(); x++){
            for(int y = 0; y<getHeight(); y++){
                for(int z = 0; z<getDepth(); z++){
                    func.accept(this.x+x, this.y+y, this.z+z);
                }
            }
        }
    }
}