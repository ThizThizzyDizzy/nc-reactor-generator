package multiblock.configuration;

import multiblock.Block;

public interface IBlockType<Template extends IBlockTemplate> {
    boolean isAir();
    String getDisplayName();
    boolean blockMatches(Block b);
}
