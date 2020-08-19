package multiblock.configuration.underhaul.fissionsfr;
import java.util.ArrayList;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Vertex;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
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
    public static PlacementRule vertex(BlockType block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.VERTEX_GROUP;
        rule.blockType = block;
        return rule;
    }
    public static PlacementRule vertex(Block block){
        PlacementRule rule = new PlacementRule();
        rule.ruleType = RuleType.VERTEX;
        rule.block = block;
        return rule;
    }
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
            case VERTEX:
                config.set("type", (byte)2);
                config.set("block", (byte)(configuration.blocks.indexOf(block)+1));
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
                    ruls.add(rule.save(configuration));
                }
                config.set("rules", ruls);
                break;
            case AND:
                config.set("type", (byte)7);
                ruls = new ConfigList();
                for(PlacementRule rule : rules){
                    ruls.add(rule.save(configuration));
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
                if(max==3)return "At least "+min+" Axial pairs of "+block.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+block.name;
                return "Between "+min+" and "+max+" Axial pairs of "+block.name;
            case AXIAL_GROUP:
                if(max==3)return "At least "+min+" Axial pairs of "+blockType.name;
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
    public boolean isValid(multiblock.underhaul.fissionsfr.Block block, UnderhaulSFR reactor){
        int num = 0;
        switch(ruleType){
            case BETWEEN:
                for(multiblock.underhaul.fissionsfr.Block b : block.getActiveAdjacent(reactor)){
                    if(b.template==this.block)num++;
                }
                return num>=min&&num<=max;
            case BETWEEN_GROUP:
                switch(blockType){
                    case AIR:
                        num = 6-block.getAdjacent(reactor).size();
                        break;
                    default:
                        for(multiblock.underhaul.fissionsfr.Block b : block.getActiveAdjacent(reactor)){
                            switch(blockType){
                                case CASING:
                                    if(b.isCasing())num++;
                                    break;
                                case COOLER:
                                    if(b.isCooler())num++;
                                    break;
                                case FUEL_CELL:
                                    if(b.isFuelCell())num++;
                                    break;
                                case MODERATOR:
                                    if(b.isModerator())num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case AXIAL:
                for(Axis axis : Axis.values()){
                    multiblock.underhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                    multiblock.underhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                    if(b1!=null&&b1.template==this.block&&b1.isActive()&&b2!=null&&b2.template==this.block&&b2.isActive())num++;
                }
                return num>=min&&num<=max;
            case AXIAL_GROUP:
                switch(blockType){
                    case AIR:
                        for(Axis axis : Axis.values()){
                            multiblock.underhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.underhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null&&b2==null)num++;
                        }
                        break;
                    default:
                        for(Axis axis : Axis.values()){
                            multiblock.underhaul.fissionsfr.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.underhaul.fissionsfr.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null||b2==null)continue;
                            if(!b1.isActive()||!b2.isActive())continue;
                            switch(blockType){
                                case CASING:
                                    if(b1.isCasing()&&b2.isCasing())num++;
                                    break;
                                case COOLER:
                                    if(b1.isCooler()&&b2.isCooler())num++;
                                    break;
                                case FUEL_CELL:
                                    if(b1.isFuelCell()&&b2.isFuelCell())num++;
                                    break;
                                case MODERATOR:
                                    if(b1.isModerator()&&b2.isModerator())num++;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case VERTEX:
                ArrayList<Direction> dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.underhaul.fissionsfr.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
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
                    multiblock.underhaul.fissionsfr.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    switch(blockType){
                        case AIR:
                            if(b==null){
                                dirs.add(d);
                                continue;
                            }
                            break;
                        case CASING:
                            if(b==null)continue;
                            if(!b.isCasing())continue;
                            break;
                        case COOLER:
                            if(b==null)continue;
                            if(!b.isCooler())continue;
                            break;
                        case MODERATOR:
                            if(b==null)continue;
                            if(!b.isModerator())continue;
                            break;
                        case FUEL_CELL:
                            if(b==null)continue;
                            if(!b.isFuelCell())continue;
                            break;
                    }
                    dirs.add(d);
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