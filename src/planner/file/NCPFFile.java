package planner.file;
import java.util.ArrayList;
import java.util.HashMap;
import planner.multiblock.Multiblock;
import planner.configuration.Configuration;
public class NCPFFile{
    public static byte SAVE_VERSION = (byte)2;
    public Configuration configuration;
    public ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public HashMap<String, String> metadata = new HashMap<>();
}