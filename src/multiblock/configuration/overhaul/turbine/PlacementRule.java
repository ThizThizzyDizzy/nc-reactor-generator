package multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Edge;
import multiblock.configuration.Configuration;
import multiblock.overhaul.turbine.OverhaulTurbine;
import planner.menu.component.Searchable;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer implements Searchable{
    public static PlacementRule parseNC(TurbineConfiguration configuration, String str){
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
        }
        boolean axial = str.startsWith("axial");
        if(axial)str = str.substring(5).trim();
        if(str.startsWith("of "))str = str.substring(3);
        if(str.startsWith("any "))str = str.substring(4);
        BlockType type = null;
        Block block = null;
        int shortest = 0;
        if(str.startsWith("coil"))type = BlockType.COIL;
        else if(str.startsWith("bearing"))type = BlockType.BEARING;
        else if(str.startsWith("connector"))type = BlockType.CONNECTOR;
        else if(str.startsWith("casing"))type = BlockType.CASING;
        else{
            String[] strs = str.split(" ");
            if(strs.length!=2||!strs[1].startsWith("coil")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(Block b : configuration.allBlocks){
                for(String s : b.getLegacyNames()){
                    if(s.toLowerCase(Locale.ENGLISH).contains("coil")&&s.toLowerCase(Locale.ENGLISH).matches("[\\s^]"+strs[0].toLowerCase(Locale.ENGLISH).replace("_", "[_ ]")+"[\\s$]")){
                        int len = s.length();
                        if(block==null||len<shortest){
                            block = b;
                            shortest = len;
                        }
                    }
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
    public BlockType blockType = BlockType.CASING;
    public Block block;
    public byte min;
    public byte max;
    public Config save(Configuration parent, TurbineConfiguration configuration){
        Config config = Config.newConfig();
        byte blockIndex = (byte)(configuration.blocks.indexOf(block)+1);
        if(parent!=null){
            blockIndex = (byte)(parent.overhaul.turbine.allBlocks.indexOf(block)+1);
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
            case EDGE:
                config.set("type", (byte)2);
                config.set("block", blockIndex);
                config.set("min", min);
                config.set("max", max);
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
            case EDGE_GROUP:
                config.set("type", (byte)5);
                config.set("block", (byte)blockType.ordinal());
                config.set("min", min);
                config.set("max", max);
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
                if(max==6)return "At least "+min+" "+block.getDisplayName();
                if(min==max)return "Exactly "+min+" "+block.getDisplayName();
                return "Between "+min+" and "+max+" "+block.getDisplayName();
            case BETWEEN_GROUP:
                if(max==6)return "At least "+min+" "+blockType.name;
                if(min==max)return "Exactly "+min+" "+blockType.name;
                return "Between "+min+" and "+max+" "+blockType.name;
            case AXIAL:
                if(max==3)return "At least "+min+" Axial pairs of "+block.getDisplayName();
                if(min==max)return "Exactly "+min+" Axial pairs of "+block.getDisplayName();
                return "Between "+min+" and "+max+" Axial pairs of "+block.getDisplayName();
            case AXIAL_GROUP:
                if(max==3)return "At least "+min+" Axial pairs of "+blockType.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+blockType.name;
                return "Between "+min+" and "+max+" Axial pairs of "+blockType.name;
            case EDGE:
                return "Two "+block.getDisplayName()+" at the same edge";
            case EDGE_GROUP:
                return "Two "+blockType.name+" at the same edge";
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
    public boolean isValid(multiblock.overhaul.turbine.Block block, OverhaulTurbine turbine){
        int num = 0;
        switch(ruleType){
            case BETWEEN:
                for(multiblock.overhaul.turbine.Block b : block.getActiveAdjacent(turbine)){
                    if(b.template==this.block)num++;
                }
                return num>=min&&num<=max;
            case BETWEEN_GROUP:
                switch(blockType){
                    case CASING:
                        num = 6-block.getAdjacent(turbine).size();
                        break;
                    default:
                        for(multiblock.overhaul.turbine.Block b : block.getActiveAdjacent(turbine)){
                            switch(blockType){
                                case BEARING:
                                    if(b.isBearing())num++;
                                    break;
                                case COIL:
                                    if(b.isCoil())num++;
                                    break;
                                case CONNECTOR:
                                    if(b.isConnector())num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case AXIAL:
                for(Axis axis : axes){
                    if(axis==Axis.Z)continue;
                    multiblock.overhaul.turbine.Block b1 = turbine.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                    multiblock.overhaul.turbine.Block b2 = turbine.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                    if(b1!=null&&b1.template==this.block&&b1.isActive()&&b2!=null&&b2.template==this.block&&b2.isActive())num++;
                }
                return num>=min&&num<=max;
            case AXIAL_GROUP:
                switch(blockType){
                    case CASING:
                        for(Axis axis : axes){
                            if(axis==Axis.Z)continue;
                            multiblock.overhaul.turbine.Block b1 = turbine.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.turbine.Block b2 = turbine.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null&&b2==null)num++;
                        }
                        break;
                    default:
                        for(Axis axis : axes){
                            if(axis==Axis.Z)continue;
                            multiblock.overhaul.turbine.Block b1 = turbine.getBlock(block.x-axis.x, block.y-axis.y, block.z-axis.z);
                            multiblock.overhaul.turbine.Block b2 = turbine.getBlock(block.x+axis.x, block.y+axis.y, block.z+axis.z);
                            if(b1==null||b2==null)continue;
                            if(!b1.isActive()||!b2.isActive())continue;
                            switch(blockType){
                                case BEARING:
                                    if(b1.isBearing()&&b2.isBearing())num++;
                                    break;
                                case COIL:
                                    if(b1.isCoil()&&b2.isCoil())num++;
                                    break;
                                case CONNECTOR:
                                    if(b1.isConnector()&&b2.isConnector())num++;
                                    break;
                            }
                        }
                        break;
                }
                return num>=min&&num<=max;
            case EDGE:
                ArrayList<Direction> dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.overhaul.turbine.Block b = turbine.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    if(b.template==this.block)dirs.add(d);
                }
                for(Edge e : Edge.values()){
                    boolean missingOne = false;
                    for(Direction d : e.directions){
                        if(!dirs.contains(d))missingOne = true;
                    }
                    if(!missingOne)return true;
                }
                return false;
            case EDGE_GROUP:
                dirs = new ArrayList<>();
                for(Direction d : Direction.values()){
                    multiblock.overhaul.turbine.Block b = turbine.getBlock(block.x+d.x, block.y+d.y, block.z+d.z);
                    switch(blockType){
                        case CASING:
                            if(b==null){
                                dirs.add(d);
                                continue;
                            }
                            break;
                        case BEARING:
                            if(b!=null&&!b.isBearing())continue;
                            break;
                        case COIL:
                            if(b!=null&&!b.isCoil())continue;
                            break;
                        case CONNECTOR:
                            if(b!=null&&!b.isConnector())continue;
                            break;
                    }
                    if(b.template==this.block)dirs.add(d);
                }
                for(Edge e : Edge.values()){
                    boolean missingOne = false;
                    for(Direction d : e.directions){
                        if(!dirs.contains(d))missingOne = true;
                    }
                    if(!missingOne)return true;
                }
                return false;
            case AND:
                for(PlacementRule rule : rules){
                    if(!rule.isValid(block, turbine))return false;
                }
                return true;
            case OR:
                for(PlacementRule rule : rules){
                    if(rule.isValid(block, turbine))return true;
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
            case EDGE:
            case AXIAL:
                nams.addAll(block.getLegacyNames());
                nams.add(block.getDisplayName());
                break;
            case BETWEEN_GROUP:
            case EDGE_GROUP:
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
        EDGE("Edge"),
        BETWEEN_GROUP("Between (Group)"),
        AXIAL_GROUP("Axial (Group)"),
        EDGE_GROUP("Edge (Group)"),
        OR("Or"),
        AND("And");
        public final String name;
        private RuleType(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
    }
    public static enum BlockType{
        CASING("Casing"),
        COIL("Coil"),
        BEARING("Bearing"),
        CONNECTOR("Connector");
        public final String name;
        private BlockType(String name){
            this.name = name;
        }
        @Override
        public String toString(){
            return name;
        }
    }
    @Override
    public boolean stillEquals(RuleContainer rc){
        PlacementRule pr = (PlacementRule)rc;
        return pr.ruleType==ruleType&&Objects.equals(pr.block,block)&&pr.min==min&&pr.max==max;
    }
}