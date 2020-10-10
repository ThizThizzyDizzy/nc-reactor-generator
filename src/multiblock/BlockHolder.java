package multiblock;
import java.awt.image.BufferedImage;
import multiblock.configuration.Block;
@Deprecated
public class BlockHolder<T extends Block>{
    public final int x;
    public final int y;
    public final int z;
    public final T block;
    @Deprecated
    public BlockHolder(int x, int y, int z, T block){
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
    }
    public BufferedImage getBaseTexture(){
        return block.getBaseTexture();
    }
}