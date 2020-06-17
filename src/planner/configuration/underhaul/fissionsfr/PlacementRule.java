package planner.configuration.underhaul.fissionsfr;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer{
    public RuleType ruleType = RuleType.BETWEEN;
    public BlockType blockType = BlockType.AIR;
    public Block block;
    public byte min;
    public byte max;
    public Config save(){
        Config config = Config.newConfig();
        switch(ruleType){
            case BETWEEN:
                config.set("type", (byte)0);
                config.set("block", (byte)Core.configuration.underhaul.fissionSFR.blocks.indexOf(block));
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL:
                config.set("type", (byte)1);
                config.set("block", (byte)Core.configuration.underhaul.fissionSFR.blocks.indexOf(block));
                config.set("min", min);
                config.set("max", max);
                break;
            case BETWEEN_GROUP:
                config.set("type", (byte)2);
                config.set("block", (byte)blockType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL_GROUP:
                config.set("type", (byte)3);
                config.set("block", (byte)blockType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case NO_PANCAKES:
                config.set("type", (byte)4);
                break;
            case OR:
                config.set("type", (byte)5);
                ConfigList ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save());
                }
                config.set("rules", ruls);
                break;
            case AND:
                config.set("type", (byte)6);
                ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save());
                }
                config.set("rules", ruls);
                break;
        }
        return config;
    }
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
        COOLER("Cooler"),
        FUEL_CELL("Fuel Cell"),
        MODERATOR("Moderator");
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