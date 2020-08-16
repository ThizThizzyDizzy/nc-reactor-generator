package generator;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Range;
public interface Settings{
    public ArrayList<Range<Block>> getAllowedBlocks();
}