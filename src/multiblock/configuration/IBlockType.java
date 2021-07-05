package multiblock.configuration;

import multiblock.Block;
import multiblock.Multiblock;

public interface IBlockType {
    boolean isAir();
    String getDisplayName();
    boolean blockMatches(Multiblock<?> reactor, Block b);
    int ordinal();


}
