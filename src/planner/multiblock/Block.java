package planner.multiblock;
import java.awt.image.BufferedImage;
import simplelibrary.Queue;
public abstract class Block extends MultiblockBit{
    public final int x;
    public final int y;
    public final int z;
    public Block(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public abstract Block newInstance(int x, int y, int z);
    public abstract BufferedImage getBaseTexture();
    public abstract BufferedImage getTexture();
    public abstract String getName();
    public abstract void clearData();
    public abstract boolean isActive();
    public <T extends Block> Queue<T> getAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null)adjacent.enqueue(b);
        }
        return adjacent;
    }
    public <T extends Block> Queue<T> getActiveAdjacent(Multiblock<T> multiblock){
        Queue<T> adjacent = new Queue<>();
        for(Direction direction : directions){
            T b = multiblock.getBlock(x+direction.x, y+direction.y, z+direction.z);
            if(b!=null&&b.isActive())adjacent.enqueue(b);
        }
        return adjacent;
    }
    public abstract String getTooltip();
}