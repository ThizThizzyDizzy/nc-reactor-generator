package net.ncplanner.plannerator.multiblock.configuration;

import java.util.ArrayList;
import simplelibrary.image.Image;

public interface IBlockTemplate {
    String getName();
    ArrayList<String> getLegacyNames();
    String getDisplayName();
    Image getDisplayTexture();
}
