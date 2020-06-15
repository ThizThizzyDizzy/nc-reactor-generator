package planner.configuration.overhaul.fissionsfr;
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
        HEATSINK,
        FUEL_CELL,
        MODERATOR,
        REFLECTOR,
        SHIELD,
        IRRADIATOR,
        CONDUCTOR;
    }
}