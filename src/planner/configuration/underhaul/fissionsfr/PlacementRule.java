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
    public static PlacementRule atLeast(int min, BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN_GROUP;
        rule.blockType = block;
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public static PlacementRule atLeast(int min, Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN;
        rule.block = block;
        rule.min = (byte)Math.min(6,Math.max(1,min));
        rule.max = 6;
        return rule;
    }
    public static PlacementRule exactly(int num, BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN_GROUP;
        rule.blockType = block;
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public static PlacementRule exactly(int num, Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.BETWEEN;
        rule.block = block;
        rule.min = rule.max = (byte)Math.min(6,Math.max(1,num));
        return rule;
    }
    public static PlacementRule axis(BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AXIAL_GROUP;
        rule.blockType = block;
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public static PlacementRule axis(Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AXIAL;
        rule.block = block;
        rule.min = 1;
        rule.max = 3;
        return rule;
    }
    public static PlacementRule or(PlacementRule... rules){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.OR;
        for(PlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public static PlacementRule and(PlacementRule... rules){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.AND;
        for(PlacementRule r : rules){
            rule.rules.add(r);
        }
        return rule;
    }
    public static PlacementRule noPancake(){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.NO_PANCAKES;
        return rule;
    }
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
    public String getDetails(){
        switch(ruleType){
            case BETWEEN:
                if(max==6)return "At least "+min+" "+block.name;
                if(min==max)return "Exactly "+min+" "+block.name;
                return "Between "+min+" and "+max+" "+block.name;
            case BETWEEN_GROUP:
                if(max==6)return "At least "+min+" "+blockType.name;
                if(min==max)return "Exactly "+min+" "+blockType.name;
                return "Between "+min+" and "+max+" "+blockType.name;
            case AXIAL:
                if(max==6)return "At least "+min+" Axial pairs of "+block.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+block.name;
                return "Between "+min+" and "+max+" Axial pairs of "+block.name;
            case AXIAL_GROUP:
                if(max==6)return "At least "+min+" Axial pairs of "+blockType.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+blockType.name;
                return "Between "+min+" and "+max+" Axial pairs of "+blockType.name;
            case NO_PANCAKES:
                return "No Pancakes";
            case AND:
                String s = "";
                for(PlacementRule rule : rules){
                    s+=" AND "+rule.getDetails();
                }
                return s.isEmpty()?s:s.substring(5);
            case OR:
                s = "";
                for(PlacementRule rule : rules){
                    s+=" OR "+rule.getDetails();
                }
                return s.isEmpty()?s:s.substring(5);
        }
        return "Unknown Rule";
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