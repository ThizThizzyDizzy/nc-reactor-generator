package net.ncplanner.plannerator.multiblock.configuration;

import net.ncplanner.plannerator.multiblock.AbstractBlock;
import net.ncplanner.plannerator.multiblock.Multiblock;

public interface IBlockType {
    boolean isAir();
    String getDisplayName();
    boolean blockMatches(Multiblock<?> reactor, AbstractBlock b);
    int ordinal();


}
