package planner.configuration.overhaul.fissionsfr;
import planner.Core;
import planner.configuration.Configuration;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer{
    public static PlacementRule parseNC(FissionSFRConfiguration configuration, String str){
        if(str.contains("||")){
            PlacementRule rule = new PlacementRule();
            rule.ruleType = RuleType.OR;
            for(String sub : str.split("||")){
                PlacementRule rul = parseNC(configuration, sub.trim());
                rule.rules.add(rul);
            }
            return rule;
        }
        if(str.contains("&&")){
            PlacementRule rule = new PlacementRule();
            rule.ruleType = RuleType.AND;
            for(String sub : str.split("&&")){
                PlacementRule rul = parseNC(configuration, sub.trim());
                rule.rules.add(rul);
            }
            return rule;
        }
        boolean exactly = str.startsWith("exactly");
        if(exactly)str = str.substring(7).trim();
        int amount = 0;
        if(str.startsWith("one")){
            amount = 1;
            str = str.substring(3).trim();
        }else if(str.startsWith("two")){
            amount = 2;
            str = str.substring(3).trim();
        }else if(str.startsWith("three")){
            amount = 3;
            str = str.substring(5).trim();
        }else if(str.startsWith("four")){
            amount = 4;
            str = str.substring(4).trim();
        }else if(str.startsWith("five")){
            amount = 5;
            str = str.substring(4).trim();
        }else if(str.startsWith("six")){
            amount = 6;
            str = str.substring(3).trim();
        }
        boolean axial = str.startsWith("axial");
        if(axial)str = str.substring(5).trim();
        BlockType type = null;
        Block block = null;
        if(str.startsWith("cell"))type = BlockType.FUEL_CELL;
        else if(str.startsWith("moderator"))type = BlockType.MODERATOR;
        else if(str.startsWith("reflector"))type = BlockType.REFLECTOR;
        else if(str.startsWith("casing"))type = BlockType.CASING;
        else if(str.startsWith("air"))type = BlockType.AIR;
        else if(str.startsWith("conductor"))type = BlockType.CONDUCTOR;
        else if(str.startsWith("sink"))type = BlockType.HEATSINK;
        else if(str.startsWith("shield"))type = BlockType.SHIELD;
        else{
            String[] strs = str.split(" ");
            if(strs.length!=2||!strs[1].startsWith("sink")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(Block b : configuration.blocks){
                if(b.name.toLowerCase().contains(strs[0].toLowerCase().replace("_", " "))){
                    block = b;
                }
            }
        }
        if(exactly&&axial){
            PlacementRule rule = new PlacementRule();
            rule.ruleType = RuleType.AND;
            PlacementRule rul1 = new PlacementRule();
            PlacementRule rul2 = new PlacementRule();
            if(type!=null){
                rul1.ruleType = RuleType.BETWEEN_GROUP;
                rul2.ruleType = RuleType.AXIAL_GROUP;
                rul1.blockType = rul2.blockType = type;
            }else{
                rul1.ruleType = RuleType.BETWEEN;
                rul2.ruleType = RuleType.AXIAL;
                rul1.block = rul2.block = block;
            }
            rul1.min = rul1.max = (byte) amount;
            rul2.min = rul2.max = (byte) (amount/2);
            rule.rules.add(rul1);
            rule.rules.add(rul2);
            return rule;
        }
        int min = amount;
        int max = 6;
        if(exactly)max = min;
        PlacementRule rule = new PlacementRule();
        if(type!=null){
            rule.ruleType = axial?RuleType.AXIAL_GROUP:RuleType.BETWEEN_GROUP;
            rule.blockType = type;
        }else{
            rule.ruleType = axial?RuleType.AXIAL:RuleType.BETWEEN;
            rule.block = block;
        }
        if(axial){
            min/=2;
            max/=2;
        }
        rule.min = (byte) min;
        rule.max = (byte) max;
        return rule;
    }
    public RuleType ruleType = RuleType.BETWEEN;
    public BlockType blockType = BlockType.AIR;
    public Block block;
    public byte min;
    public byte max;
    public Config save(FissionSFRConfiguration configuration){
        Config config = Config.newConfig();
        switch(ruleType){
            case BETWEEN:
                config.set("type", (byte)0);
                config.set("block", (byte)(configuration.blocks.indexOf(block)+1));
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL:
                config.set("type", (byte)1);
                config.set("block", (byte)(configuration.blocks.indexOf(block)+1));
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
                    ruls.add(rule.save(configuration));
                }
                config.set("rules", ruls);
                break;
            case AND:
                config.set("type", (byte)6);
                ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save(configuration));
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