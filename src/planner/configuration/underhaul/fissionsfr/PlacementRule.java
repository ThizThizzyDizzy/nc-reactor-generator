package planner.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
public class PlacementRule{
    public RuleType ruleType;
    public BlockType blockType;
    public int min;
    public int max;
    public ArrayList<PlacementRule> rules = new ArrayList<>();
    public static enum RuleType{
        BETWEEN,
        AXIAL,
        BETWEEN_GROUP,
        AXIAL_GROUP,
        NO_PANCAKES,
        OR,
        AND;
    }
    public static enum BlockType{
        AIR,
        CASING,
        COOLER,
        FUEL_CELL,
        MODERATOR;
    }
}