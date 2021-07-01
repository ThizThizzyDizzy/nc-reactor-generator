package multiblock.configuration;

import java.util.ArrayList;

public interface IBlockTemplate {
    ArrayList<String> getLegacyNames();
    String getDisplayName();
}
