package net.ncplanner.plannerator.planner.file;
import java.util.HashMap;
public class NCPFHeader{
    public byte version;
    public int multiblocks;
    public HashMap<String, String> metadata = new HashMap<>();
}