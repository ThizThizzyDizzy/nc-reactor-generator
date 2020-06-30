package planner.file;
import java.util.ArrayList;
import java.util.HashMap;
import planner.multiblock.Multiblock;
import planner.configuration.Configuration;
public class NCPFFile{
    public Configuration configuration;
    public ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public HashMap<String, String> metadata = new HashMap<>();
}