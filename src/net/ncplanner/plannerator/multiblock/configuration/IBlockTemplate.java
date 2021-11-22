package net.ncplanner.plannerator.multiblock.configuration;

import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.image.Image;

public interface IBlockTemplate {
    String getName();
    ArrayList<String> getLegacyNames();
    String getDisplayName();
    Image getDisplayTexture();
}
