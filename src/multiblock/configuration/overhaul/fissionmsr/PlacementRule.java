package multiblock.configuration.overhaul.fissionmsr;
import java.util.Locale;

import multiblock.configuration.AbstractBlockContainer;
import multiblock.configuration.AbstractPlacementRule;
import multiblock.configuration.Configuration;
import multiblock.configuration.IBlockType;

public class PlacementRule extends AbstractPlacementRule<PlacementRule.BlockType, Block> {
    public static PlacementRule parseNC(FissionMSRConfiguration configuration, String str){
        if(str.contains("||")){
            PlacementRule rule = new PlacementRule();
            rule.ruleType = RuleType.OR;
            for(String sub : str.split("\\|\\|")){
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
        if(str.startsWith("at least "))str = str.substring("at least ".length());
        boolean exactly = str.startsWith("exactly");
        if(exactly)str = str.substring(7).trim();
        int amount = 0;
        if(str.startsWith("zero")){
            amount = 0;
            str = str.substring(4).trim();
        }else if(str.startsWith("one")){
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
        int shortest = 0;
        if(str.startsWith("cell"))type = BlockType.VESSEL;
        else if(str.startsWith("vessel"))type = BlockType.VESSEL;
        else if(str.startsWith("moderator"))type = BlockType.MODERATOR;
        else if(str.startsWith("reflector"))type = BlockType.REFLECTOR;
        else if(str.startsWith("casing"))type = BlockType.CASING;
        else if(str.startsWith("air"))type = BlockType.AIR;
        else if(str.startsWith("conductor"))type = BlockType.CONDUCTOR;
        else if(str.startsWith("sink"))type = BlockType.HEATER;
        else if(str.startsWith("heater"))type = BlockType.HEATER;
        else if(str.startsWith("shield"))type = BlockType.SHIELD;
        else{
            String[] strs = str.split(" ");
            if(strs.length!=2||!strs[1].startsWith("heater")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(Block b : configuration.allBlocks){
                if(b.parent!=null)continue;
                for(String s : b.getLegacyNames()){
                    if(s.toLowerCase(Locale.ENGLISH).contains("heater")&&s.toLowerCase(Locale.ENGLISH).matches("[\\s^]?"+strs[0].toLowerCase(Locale.ENGLISH).replace("_", "[_ ]")+"[\\s$]?.*")){
                        int len = s.length();
                        if(block==null||len<shortest){
                            block = b;
                            shortest = len;
                        }
                    }
                }
            }
            if(block==null)throw new IllegalArgumentException("Could not find block matching rule bit "+str+"!");
        }
        if(type==null&&block==null)throw new IllegalArgumentException("Failed to parse rule "+str+": block is null!");
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

    @Override
    protected AbstractBlockContainer<Block> getContainerFromParent(Configuration parent) {
        return parent.overhaul.fissionMSR;
    }

    @Override
    protected byte saveBlockType(BlockType type) {
        return (byte) type.ordinal();
    }

    @Override
    public BlockType loadBlockType(byte type) {
        return BlockType.values()[type];
    }

    public enum BlockType implements IBlockType<Block> {
        AIR("Air"),
        CASING("Casing"),
        HEATER("Heater"),
        VESSEL("Fuel Vessel"),
        MODERATOR("Moderator"),
        REFLECTOR("Reflector"),
        SHIELD("Neutron Shield"),
        IRRADIATOR("Irradiator"),
        CONDUCTOR("Conductor");

        public final String name;
        BlockType(String name){
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }
        @Override
        public boolean isAir() {
            return this == AIR;
        }
        @Override
        public String getDisplayName() {
            return name;
        }
        @Override
        public boolean blockMatches(multiblock.Block rb) {
            multiblock.overhaul.fissionmsr.Block b = (multiblock.overhaul.fissionmsr.Block) rb;
            switch(this) {
                case CASING:
                    return b.isCasing();
                case CONDUCTOR:
                    return b.isConductor();
                case HEATER:
                    return b.isHeater();
                case IRRADIATOR:
                    return b.isIrradiator();
                case MODERATOR:
                    return b.isModerator();
                case REFLECTOR:
                    return b.isReflector();
                case SHIELD:
                    return b.isShield();
                case VESSEL:
                    return b.isFuelVessel();
                default:
                    throw new RuntimeException("Invalid enum type?");
            }
        }
    }
}