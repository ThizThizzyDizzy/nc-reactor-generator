package planner.configuration.overhaul.fissionsfr;
public class PlacementRule extends RuleContainer{
    public RuleType ruleType = RuleType.BETWEEN;
    public BlockType blockType = BlockType.AIR;
    public Block block;
    public int min;
    public int max;
    public static enum RuleType{
        BETWEEN("Between"),
        AXIAL("Axial"),
        BETWEEN_GROUP("Between (Group)"),
        AXIAL_GROUP("Axial (Group)"),
        NO_PANCAKES("No Pancakes"),
        OR("Or"),
        AND("And");
        private final String name;
        private RuleType(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
        public static String[] getStringList(){
            String[] strs = new String[values().length];
            for(int i = 0; i<strs.length; i++){
                strs[i] = values()[i].toString();
            }
            return strs;
        }
    }
    public static enum BlockType{
        AIR("Air"),
        CASING("Casing"),
        HEATSINK("Heatsink"),
        FUEL_CELL("Fuel Cell"),
        MODERATOR("Moderator"),
        REFLECTOR("Reflector"),
        SHIELD("Neutron Shield"),
        IRRADIATOR("Irradiator"),
        CONDUCTOR("Conductor");
        private final String name;
        private BlockType(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
        public static String[] getStringList(){
            String[] strs = new String[values().length];
            for(int i = 0; i<strs.length; i++){
                strs[i] = values()[i].toString();
            }
            return strs;
        }
    }
}