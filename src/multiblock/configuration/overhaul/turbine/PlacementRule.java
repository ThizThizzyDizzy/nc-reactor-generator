package multiblock.configuration.overhaul.turbine;
import java.util.ArrayList;
import java.util.Locale;
import multiblock.Axis;
import multiblock.Direction;
import multiblock.Edge;
import multiblock.configuration.Configuration;
import multiblock.overhaul.turbine.OverhaulTurbine;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
public class PlacementRule extends RuleContainer{
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
        }
        boolean axial = str.startsWith("axial");
        if(axial)str = str.substring(5).trim();
        if(str.startsWith("of "))str = str.substring(3);
        if(str.startsWith("any "))str = str.substring(4);
        CoilType type = null;
        Coil coil = null;
        if(str.startsWith("coil"))type = CoilType.COIL;
        else if(str.startsWith("bearing"))type = CoilType.BEARING;
        else if(str.startsWith("connector"))type = CoilType.CONNECTOR;
        else if(str.startsWith("casing"))type = CoilType.CASING;
        else{
            String[] strs = str.split(" ");
            if(strs.length!=2||!strs[1].startsWith("coil")){
                throw new IllegalArgumentException("Unknown rule bit: "+str);
            }
            for(Coil c : configuration.allCoils){
                if(c.name.toLowerCase(Locale.ENGLISH).contains(strs[0].toLowerCase(Locale.ENGLISH).replace("_", " "))){
                    coil = c;
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
                rul1.coilType = rul2.coilType = type;
            }else{
                rul1.ruleType = RuleType.BETWEEN;
                rul2.ruleType = RuleType.AXIAL;
                rul1.coil = rul2.coil = coil;
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
            rule.coilType = type;
        }else{
            rule.ruleType = axial?RuleType.AXIAL:RuleType.BETWEEN;
            rule.coil = coil;
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
    public CoilType coilType = CoilType.CASING;
    public Coil coil;
    public byte min;
    public byte max;
    public Config save(Configuration parent, TurbineConfiguration configuration){
        Config config = Config.newConfig();
        byte coilIndex = (byte)(configuration.coils.indexOf(coil)+1);
        if(parent!=null){
            if(parent.overhaul!=null&&parent.overhaul.turbine!=null){
                coilIndex+=parent.overhaul.turbine.coils.size();
            }
            for(Configuration addon : parent.addons){
                if(addon.overhaul!=null&&addon.overhaul.turbine!=null){
                    if(addon.overhaul.turbine==configuration)break;
                    else coilIndex+=addon.overhaul.turbine.coils.size();
                }
            }
        }
        switch(ruleType){
            case BETWEEN:
                config.set("type", (byte)0);
                config.set("block", coilIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL:
                config.set("type", (byte)1);
                config.set("block", coilIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case EDGE:
                config.set("type", (byte)2);
                config.set("block", coilIndex);
                config.set("min", min);
                config.set("max", max);
                break;
            case BETWEEN_GROUP:
                config.set("type", (byte)3);
                config.set("block", (byte)coilType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case AXIAL_GROUP:
                config.set("type", (byte)4);
                config.set("block", (byte)coilType.ordinal());
                config.set("min", min);
                config.set("max", max);
                break;
            case EDGE_GROUP:
                config.set("type", (byte)5);
                config.set("block", (byte)coilType.ordinal());
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
                if(max==6)return "At least "+min+" "+coil.name;
                if(min==max)return "Exactly "+min+" "+coil.name;
                return "Between "+min+" and "+max+" "+coil.name;
            case BETWEEN_GROUP:
                if(max==6)return "At least "+min+" "+coilType.name;
                if(min==max)return "Exactly "+min+" "+coilType.name;
                return "Between "+min+" and "+max+" "+coilType.name;
            case AXIAL:
                if(max==6)return "At least "+min+" Axial pairs of "+coil.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+coil.name;
                return "Between "+min+" and "+max+" Axial pairs of "+coil.name;
            case AXIAL_GROUP:
                if(max==6)return "At least "+min+" Axial pairs of "+coilType.name;
                if(min==max)return "Exactly "+min+" Axial pairs of "+coilType.name;
                return "Between "+min+" and "+max+" Axial pairs of "+coilType.name;
            case EDGE:
                return "Two "+coil.name+" at the same edge";
            case EDGE_GROUP:
                return "Two "+coilType.name+" at the same edge";
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
    public boolean isValid(multiblock.overhaul.turbine.Block coil, OverhaulTurbine turbine){
        int num = 0;
        switch(ruleType){
            case BETWEEN:
                for(multiblock.overhaul.turbine.Block b : coil.getActiveAdjacent(turbine)){
                    if(b.coil==this.coil)num++;
                }
                return num>=min&&num<=max;
            case BETWEEN_GROUP:
                switch(coilType){
                    case CASING:
                        num = 6-coil.getAdjacent(turbine).size();
                        break;
                    default:
                        for(multiblock.overhaul.turbine.Block b : coil.getActiveAdjacent(turbine)){
                            switch(coilType){
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
                for(Axis axis : Axis.values()){
                    if(axis==Axis.Z)continue;
                    multiblock.overhaul.turbine.Block b1 = turbine.getBlock(coil.x-axis.x, coil.y-axis.y, coil.z-axis.z);
                    multiblock.overhaul.turbine.Block b2 = turbine.getBlock(coil.x+axis.x, coil.y+axis.y, coil.z+axis.z);
                    if(b1!=null&&b1.coil==this.coil&&b1.isActive()&&b2!=null&&b2.coil==this.coil&&b2.isActive())num++;
                }
                return num>=min&&num<=max;
            case AXIAL_GROUP:
                switch(coilType){
                    case CASING:
                        for(Axis axis : Axis.values()){
                            if(axis==Axis.Z)continue;
                            multiblock.overhaul.turbine.Block b1 = turbine.getBlock(coil.x-axis.x, coil.y-axis.y, coil.z-axis.z);
                            multiblock.overhaul.turbine.Block b2 = turbine.getBlock(coil.x+axis.x, coil.y+axis.y, coil.z+axis.z);
                            if(b1==null&&b2==null)num++;
                        }
                        break;
                    default:
                        for(Axis axis : Axis.values()){
                            if(axis==Axis.Z)continue;
                            multiblock.overhaul.turbine.Block b1 = turbine.getBlock(coil.x-axis.x, coil.y-axis.y, coil.z-axis.z);
                            multiblock.overhaul.turbine.Block b2 = turbine.getBlock(coil.x+axis.x, coil.y+axis.y, coil.z+axis.z);
                            if(b1==null||b2==null)continue;
                            if(!b1.isActive()||!b2.isActive())continue;
                            switch(coilType){
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
                    multiblock.overhaul.turbine.Block b = turbine.getBlock(coil.x+d.x, coil.y+d.y, coil.z+d.z);
                    if(b.coil==this.coil)dirs.add(d);
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
                    multiblock.overhaul.turbine.Block b = turbine.getBlock(coil.x+d.x, coil.y+d.y, coil.z+d.z);
                    switch(coilType){
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
                    if(b.coil==this.coil)dirs.add(d);
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
                    if(!rule.isValid(coil, turbine))return false;
                }
                return true;
            case OR:
                for(PlacementRule rule : rules){
                    if(rule.isValid(coil, turbine))return true;
                }
                return false;
        }
        throw new IllegalArgumentException("Unknown rule type: "+ruleType);
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
    public static enum CoilType{
        CASING("Casing"),
        COIL("Coil"),
        BEARING("Bearing"),
        CONNECTOR("Connector");
        private final String name;
        private CoilType(String name){
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