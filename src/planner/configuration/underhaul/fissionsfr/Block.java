package planner.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
public class Block extends RuleContainer{
    public String name;
    public int cooling = 0;
    public boolean fuelCell = false;
    public boolean moderator = false;
    public BufferedImage texture;
    public Block(String name){
        this.name = name;
    }
}