package multiblock.configuration;

import simplelibrary.image.Image;

import java.util.ArrayList;

public interface IBlockTemplate {
    ArrayList<String> getLegacyNames();
    String getDisplayName();
    Image getDisplayTexture();
}
