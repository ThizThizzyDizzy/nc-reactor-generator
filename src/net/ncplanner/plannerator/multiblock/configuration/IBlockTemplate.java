package net.ncplanner.plannerator.multiblock.configuration;

import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;

public interface IBlockTemplate extends ThingWithLegacyNames{
    String getName();
    String getDisplayName();
    Image getDisplayTexture();
}
