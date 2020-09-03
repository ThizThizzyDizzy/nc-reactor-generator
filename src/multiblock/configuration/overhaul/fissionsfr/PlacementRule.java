package multiblock.configuration.overhaul.fissionsfr;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Vertex;
import multiblock.configuration.Configuration;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer{
    public static PlacementRule parseNC(FissionSFRConfiguration configuration, String str){
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
            for(Block b : configuration.allBlocks){
                if(b.name.toLowerCase(Locale.ENGLISH).contains(strs[0].toLowerCase(Locale.ENGLISH).replace("_", " "))){
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
    public Config save(Configuration parent, FissionSFRConfiguration configuration){
        Config config = Config.newConfig();
        byte blockIndex = (byte)(configuration.blocks.indexOf(block)+1);
        if(parent!=null){
            if(parent.overhaul!=null&&parent.overhaul.fissionSFR!=null){
                blockIndex+=parent.overhaul.fissionSFR.blocks.size();
            }
            for(Configuration addon : parent.addons){
                if(addon.overhaul!=null&&addon.overhaul.fissionSFR!=null){
                    if(addon.overhaul.fissionSFR==configuration)break;
                    else blockIndex+=addon.overhaul.fissionSFR.blocks.size();
                }
            }
        }
        switch(ruleType){
            case BETWEEN:
                config.set("type", (byte)0);
                config.set("block", blockIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL:
                config.set("type", (byte)1);
                config.set("block", blockIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case VERTEX:
                config.set("type", (byte)2);
                config.set("block", blockIndex);
                break;
            case BETWEEN_GROUP:
                config.set("type", (byte)3);
                config.set("block", (byte)blockType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL_GROUP:
                config.set("type", (byte)4);
                config.set("block", (byte)blockType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case VERTEX_GROUP:
                config.set("type", (byte)5);
                config.set("block", (byte)blockType.ordinal());
                break;
            case OR:
                config.set("type", (byte)6);
                ConfigList ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save(parent, configuration));
                }
                config.set("rules", ruls);
                break;
            case AND:
                config.set("type", (byte)7);
                ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save(parent, configuration));
                }
                config.set("rules", ruls);
                break;
        }
        return config;
    }
    @Override
    public String toString(){
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
            case VERTEX:
                return "Three "+block.name+" at the same vertex";
            case VERTEX_GROUP:
                return "Three "+blockType.name+" at the same vertex";
            case AND:
                String s = "";
                for(PlacementRule rule : rules){
                    s+=" AND "+rule.toString();
                }
                return s.isEmpty()?s:s.substring(5);
            case OR:
                s = "";
                for(PlacementRule rule : rules){
                    s+=" OR "+rule.toString();
                }
                return s.isEmpty()?s:s.substring(4);
        }
        return "Unknown Rule";
    }
    public boolean isValid(multiblock.overhaul.fissionsfr.Block block, OverhaulSFR reactor){
        int num = 0;
        switch(ruleType){
            case BETWEEN:
                for(multiblock.overhaul.fissionsfr.Block b : block.getActiveAdjacent(reactor)){
                    if(b.template==this.block)num++;
                }
                return num>=min&&num<=max;
            case BETWEEN_GROUP:
                switch(blockType){
                    case AIR:
                        num = 6-block.getAdjacent(reactor).size();
                        break;
                    default:
                        for(multiblock.overhaul.fissionsfr.Block b : block.getActiveAdjacent(reactor)){
                            switch(blockType){
                                case CASING:
                                    if(b.isCasing())num++;
                                    break;
                                case CONDUCTOR:
                                    if(b.isConductor())num++;
                                    break;
                                case FUEL_CELL:
                                    if(b.isFuelCell())num++;
                                    break;
                                case HEATSINK:
                                    if(b.isHeatsink())num++;
                                    break;
                                case IRRADIATOR:
                                    if(b.isIrradiator())num++;
                                    break;
                                case MODERATOR:
                                    if(b.isModeratorActive())num++;
                                    break;
                                case REFLECTOR:
                                    if(b.isReflector())num++;
                                    break;
                                case SHIELD:
                                    if(b.isShield())num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case AXIAL:
                for(Axis axis : Axis.values()){
                    multiblock.overhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                    multiblock.overhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                    if(b1!=null&&b1.template==this.block&&b1.isActive()&&b2!=null&&b2.template==this.block&&b2.isActive())num++;
                }
                return num>=min&&num<=max;
            case AXIAL_GROUP:
                switch(blockType){
                    case AIR:
                        for(Axis axis : Axis.values()){
                            multiblock.overhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null&&b2==null)num++;
                        }
                        break;
                    default:
                        for(Axis axis : Axis.values()){
                            multiblock.overhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null||b2==null)continue;
                            if(!b1.isActive()||!b2.isActive())continue;
                            switch(blockType){
                                case CASING:
                                    if(b1.isCasing()&&b2.isCasing())num++;
                                    break;
                                case HEATSINK:
                                    if(b1.isHeatsink()&&b2.isHeatsink())num++;
                                    break;
                                case FUEL_CELL:
                                    if(b1.isFuelCell()&&b2.isFuelCell())num++;
                                    break;
                                case MODERATOR:
                                    if(b1.isModeratorActive()&&b2.isModeratorActive())num++;
                                    break;
                                case CONDUCTOR:
                                    if(b1.isConductor()&&b2.isConductor())num++;
                                    break;
                                case IRRADIATOR:
                                    if(b1.isIrradiator()&&b2.isIrradiator())num++;
                                    break;
                                case REFLECTOR:
                                    if(b1.isReflector()&&b2.isReflector())num++;
                                    break;
                                case SHIELD:
                                    if(b1.isShield()&&b2.isShield())num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case VERTEX:
                ArrayList<Direction> dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.overhaul.fissionsfr.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    if(b.template==this.block)dirs.add(d);
                }
                for(Vertex e : Vertex.values()){
                    boolean missingOne = false;
                    for(Direction d : e.directions){
                        if(!dirs.contains(d))missingOne = true;
                    }
                    if(!missingOne)return true;
                }
                return false;
            case VERTEX_GROUP:
                dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.overhaul.fissionsfr.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    switch(blockType){
                        case AIR:
                            if(b==null){
                                dirs.add(d);
                                continue;
                            }
                            break;
                        case CASING:
                            if(!b.isCasing())continue;
                            break;
                        case CONDUCTOR:
                            if(!b.isConductor())continue;
                            break;
                        case HEATSINK:
                            if(!b.isHeatsink())continue;
                            break;
                        case IRRADIATOR:
                            if(!b.isIrradiator())continue;
                            break;
                        case MODERATOR:
                            if(!b.isModerator())continue;
                            break;
                        case REFLECTOR:
                            if(!b.isReflector())continue;
                            break;
                        case SHIELD:
                            if(!b.isShield())continue;
                            break;
                        case FUEL_CELL:
                            if(!b.isFuelCell())continue;
                            break;
                    }
                    if(b.template==this.block)dirs.add(d);
                }
                for(Vertex e : Vertex.values()){
                    boolean missingOne = false;
                    for(Direction d : e.directions){
                        if(!dirs.contains(d))missingOne = true;
                    }
                    if(!missingOne)return true;
                }
                return false;
            case AND:
                for(PlacementRule rule : rules){
                    if(!rule.isValid(block, reactor))return false;
                }
                return true;
            case OR:
                for(PlacementRule rule : rules){
                    if(rule.isValid(block, reactor))return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: "+ruleType);
    }
    public static enum RuleType{
        BETWEEN("Between"),
        AXIAL("Axial"),
        VERTEX("Vertex"),
        BETWEEN_GROUP("Between (Group)"),
        AXIAL_GROUP("Axial (Group)"),
        VERTEX_GROUP("Vertex (Group)"),
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
    @Override
    public boolean stillEquals(RuleContainer rc){
        PlacementRule pr = (PlacementRule)rc;
        return pr.ruleType==ruleType&&Objects.equals(pr.block,block)&&pr.min==min&&pr.max==max;
    }
}