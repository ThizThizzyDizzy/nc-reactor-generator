package net.ncplanner.plannerator.multiblock.configuration;

import simplelibrary.image.Image;

import java.util.ArrayList;

public interface IBlockTemplate {
    String getName();
    ArrayList<String> getLegacyNames();
    String getDisplayName();
    Image getDisplayTexture();
}
