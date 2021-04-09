package multiblock.configuration.overhaul.fusion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Vertex;
import multiblock.configuration.Configuration;
import multiblock.overhaul.fusion.OverhaulFusionReactor;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer implements Searchable{
    public RuleType ruleType = RuleType.BETWEEN;
    public BlockType blockType = BlockType.AIR;
    public Block block;
    public byte min;
    public byte max;
    public Config save(Configuration parent, FusionConfiguration configuration){
        Config config = Config.newConfig();
        byte blockIndex = (byte)(configuration.blocks.indexOf(block)+1);
        if(parent!=null){
            blockIndex = (byte)(parent.overhaul.fusion.allBlocks.indexOf(block)+1);
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
    public boolean isValid(multiblock.overhaul.fusion.Block block, OverhaulFusionReactor reactor){
        int num = 0;
        switch(ruleType){
            case BETWEEN:
                for(multiblock.overhaul.fusion.Block b : block.getActiveAdjacent(reactor)){
                    if(b.template==this.block)num++;
                }
                return num>=min&&num<=max;
            case BETWEEN_GROUP:
                switch(blockType){
                    case AIR:
                        num = 6-block.getAdjacent(reactor).size();
                        break;
                    default:
                        for(multiblock.overhaul.fusion.Block b : block.getActiveAdjacent(reactor)){
                            switch(blockType){
                                case BREEDING_BLANKET:
                                    if(b.isBreedingBlanket())num++;
                                    break;
                                case CONDUCTOR:
                                    if(b.isConductor())num++;
                                    break;
                                case CONNECTOR:
                                    if(b.isConnector())num++;
                                    break;
                                case HEATING_BLANKET:
                                    if(b.isHeatingBlanket())num++;
                                    break;
                                case HEAT_SINK:
                                    if(b.isHeatsink())num++;
                                    break;
                                case POLOIDAL_ELECTROMAGNET:
                                    if(b.isElectromagnet()&&reactor.isPoloidal(b))num++;
                                    break;
                                case REFLECTOR:
                                    if(b.isReflector())num++;
                                    break;
                                case SHIELDING:
                                    if(b.isShielding())num++;
                                    break;
                                case TOROIDAL_ELECTROMAGNET:
                                    if(b.isElectromagnet()&&reactor.isToroidal(b))num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case AXIAL:
                for(Axis axis : axes){
                    multiblock.overhaul.fusion.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                    multiblock.overhaul.fusion.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                    if(b1!=null&&b1.template==this.block&&b1.isActive()&&b2!=null&&b2.template==this.block&&b2.isActive())num++;
                }
                return num>=min&&num<=max;
            case AXIAL_GROUP:
                switch(blockType){
                    case AIR:
                        for(Axis axis : axes){
                            multiblock.overhaul.fusion.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.fusion.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null&&b2==null)num++;
                        }
                        break;
                    default:
                        for(Axis axis : axes){
                            multiblock.overhaul.fusion.Block b1 = reactor.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.fusion.Block b2 = reactor.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null||b2==null)continue;
                            if(!b1.isActive()||!b2.isActive())continue;
                            switch(blockType){
                                case BREEDING_BLANKET:
                                    if(b1.isBreedingBlanket()&&b2.isBreedingBlanket())num++;
                                    break;
                                case CONDUCTOR:
                                    if(b1.isConductor()&&b2.isConductor())num++;
                                    break;
                                case CONNECTOR:
                                    if(b1.isConnector()&&b2.isConnector())num++;
                                    break;
                                case HEATING_BLANKET:
                                    if(b1.isHeatingBlanket()&&b2.isHeatingBlanket())num++;
                                    break;
                                case HEAT_SINK:
                                    if(b1.isHeatsink()&&b2.isHeatsink())num++;
                                    break;
                                case POLOIDAL_ELECTROMAGNET:
                                    if(b1.isElectromagnet()&&reactor.isPoloidal(b1)&&b2.isElectromagnet()&&reactor.isPoloidal(b2))num++;
                                    break;
                                case REFLECTOR:
                                    if(b1.isReflector()&&b2.isReflector())num++;
                                    break;
                                case SHIELDING:
                                    if(b1.isShielding()&&b2.isShielding())num++;
                                    break;
                                case TOROIDAL_ELECTROMAGNET:
                                    if(b1.isElectromagnet()&&reactor.isToroidal(b1)&&b2.isElectromagnet()&&reactor.isToroidal(b2))num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case VERTEX:
                ArrayList<Direction> dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.overhaul.fusion.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
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
                    multiblock.overhaul.fusion.Block b = reactor.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    switch(blockType){
                        case AIR:
                            if(b==null){
                                dirs.add(d);
                                continue;
                            }
                            break;
                        case BREEDING_BLANKET:
                            if(!b.isBreedingBlanket())continue;
                            break;
                        case CONDUCTOR:
                            if(!b.isConductor())continue;
                            break;
                        case CONNECTOR:
                            if(!b.isConnector())continue;
                            break;
                        case HEATING_BLANKET:
                            if(!b.isHeatingBlanket())continue;
                            break;
                        case HEAT_SINK:
                            if(!b.isHeatsink())continue;
                            break;
                        case POLOIDAL_ELECTROMAGNET:
                            if(!b.isElectromagnet()||!reactor.isPoloidal(b))continue;
                            break;
                        case REFLECTOR:
                            if(!b.isReflector())continue;
                            break;
                        case SHIELDING:
                            if(!b.isShielding())continue;
                            break;
                        case TOROIDAL_ELECTROMAGNET:
                            if(!b.isElectromagnet()||!reactor.isToroidal(b))continue;
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
    @Override
    public ArrayList<String> getSearchableNames(){
        ArrayList<String> nams = new ArrayList<>();
        switch(ruleType){
            case BETWEEN:
            case VERTEX:
            case AXIAL:
                nams.addAll(block.getLegacyNames());
                nams.add(block.getDisplayName());
                break;
            case BETWEEN_GROUP:
            case VERTEX_GROUP:
            case AXIAL_GROUP:
                nams.add(blockType.name);
                break;
            case AND:
            case OR:
                for(PlacementRule r : rules)nams.addAll(r.getSearchableNames());
                break;
        }
        return nams;
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
        TOROIDAL_ELECTROMAGNET("Toroidal Electromagnet"),
        POLOIDAL_ELECTROMAGNET("Poloidal Electromagnet"),
        HEATING_BLANKET("Heating Blanket"),
        BREEDING_BLANKET("Breeding Blanket"),
        REFLECTOR("Reflector"),
        HEAT_SINK("Heat Sink"),
        SHIELDING("Shielding"),
        CONDUCTOR("Conductor"),
        CONNECTOR("Connector");
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