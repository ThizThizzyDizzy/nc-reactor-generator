package planner.multiblock;
import java.awt.image.BufferedImage;
public interface Block{
    public Block newInstance();
    public BufferedImage getTexture();
    public String getName();
}