package planner.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
public class Block{
    public String name;
    public int cooling = 0;
    public ArrayList<PlacementRule> rules = new ArrayList<>();
    public boolean fuelCell = false;
    public boolean moderator = false;
    public BufferedImage texture;
}