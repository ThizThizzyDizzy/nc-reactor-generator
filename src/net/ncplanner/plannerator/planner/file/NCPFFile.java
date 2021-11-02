package net.ncplanner.plannerator.planner.file;
import java.util.ArrayList;
import java.util.HashMap;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.configuration.Configuration;
public class NCPFFile{
    public static byte SAVE_VERSION = (byte)11;
    public Configuration configuration;
    public ArrayList<Multiblock> multiblocks = new ArrayList<>();
    public HashMap<String, String> metadata = new HashMap<>();
}