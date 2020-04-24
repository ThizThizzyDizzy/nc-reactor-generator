package overhaul;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JTextArea;
import common.JSON;
import common.JSON.JSONObject;
import common.JSON.JSONArray;
public abstract class Reactor{
    //The export format is based on this version of hellrage's reactor planner: (Saved in the json file)
    public static final int MAJOR_VERSION = 2;
    public static final int MINOR_VERSION = 0;
    public static final int BUILD = 37;
    public static final int REVISION = 0;
    public static final int MAJOR_REVISION = 0;
    public static final int MINOR_REVISION = 0;
    public static final Object synchronizer = new Object();//I have a lot of these...
    public static long totalReactors;
    public static long startTime;
    private static final int leniency = 10;
    private static final double thresholdRatio = .75;
    private static final double minimumMult = .5;
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand){
        return random(fuel, type, x, y, z, rand, null);
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, ArrayList<ReactorPart> allowedParts){
        return new Reactor(fuel, type, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.random(rand, allowedParts);
            }
        };
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, boolean xSymm, boolean ySymm, boolean zSymm){
        return random(fuel, type, x, y, z, rand, xSymm, ySymm, zSymm, null);
    }
    public static Reactor random(Fuel fuel, Fuel.Type type, int x, int y, int z, Random rand, boolean xSymm, boolean ySymm, boolean zSymm, ArrayList<ReactorPart> allowedParts){
        return new Reactor(fuel, type, x, y, z, xSymm, ySymm, zSymm){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.random(rand, allowedParts);
            }
        };
    }
    private static final ArrayList<ReactorBit> clusterables = new ArrayList<>();
    static{//There's probably a much easier and better way of doing this
        for(ReactorPart.Type t : ReactorPart.Type.values()){
            if(t.canCluster)clusterables.add(t);
        }
    }
    static boolean isbetter(Reactor reactor, Reactor other){
        for(Priority p : Priority.priorities){
            double comparison = p.compare(reactor, other);
            if(comparison>0)return true;
            if(comparison<0)return false;
        }
        return false;
    }
    public static Reactor parse(JTextArea textAreaImport, Fuel fuel, Fuel.Type type, int x, int y, int z){
        return parse(textAreaImport.getText(), fuel, type, x, y, z);
    }
    public static Reactor parse(String text, Fuel fuel, Fuel.Type type, int x, int y, int z){
        String error = "";
        if(text.startsWith("{")){
            try{
                return parseJSON(text, fuel, type, x, y, z);
            }catch(Exception ex){
                Main.instance.textAreaImportOutput.setText(ex.getMessage());
            }
        }
        ReactorPart[][][] prts = new ReactorPart[x][y][z];
        int X = 0;
        int Y = 0;
        int Z = 0;
        while(!text.isEmpty()){
            if(text.startsWith(" ")){
                X++;
                text = text.substring(1);
                continue;
            }
            if(text.startsWith("\n\n")){
                Y++;
                X = 0;
                Z = 0;
                text = text.substring(2);
                continue;
            }
            if(text.startsWith("\n")){
                Z++;
                X = 0;
                text = text.substring(1);
                continue;
            }
            String part = text.split("\n")[0].split(" ")[0];
            try{
                prts[X][Y][Z] = ReactorPart.parse(part);
                if(prts[X][Y][Z] == null){
                    error += "Unknown part: "+part;
                }
            }catch(ArrayIndexOutOfBoundsException ex){
                error += "Cannot set part "+X+" "+Y+" "+Z+"!\nReactor is only "+x+" "+y+" "+z+"!";
            }catch(Exception ex){
                prts[X][Y][Z] = ReactorPart.AIR;
            }
            text = text.substring(part.length());
        }
        Main.instance.textAreaImportOutput.setText(error);
        return new Reactor(fuel, type, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return prts[X][Y][Z]==null?ReactorPart.AIR:prts[X][Y][Z];
            }
        };
    }
    private static Reactor parseJSON(String text, Fuel fuel, Fuel.Type type, int x, int y, int z) throws IOException{
        Main.instance.textAreaImportOutput.setText("");
        String error = "";
        JSONObject json;
        json = JSON.parse(text);
        if(json==null)return null;
        JSONObject dim = json.getJSONObject("InteriorDimensions");
        if(dim==null)return null;
        int X = dim.getInt("X");
        int Y = dim.getInt("Y");
        int Z = dim.getInt("Z");
        ReactorPart[][][] prts = new ReactorPart[X][Y][Z];
        if(X!=x||Y!=y||Z!=z){
            error = "Incorrect dimensions! Found "+X+" "+Y+" "+Z+", expected "+x+" "+y+" "+z+"!";
        }else{
            String[] names = {"HeatSinks", "Moderators", "Conductors", "Reflectors", "FuelCells"};
            for(String name : names){
                Object o = json.get(name);
                if(o instanceof JSONObject){
                    JSONObject reactor = (JSONObject)o;
                    for(String s : reactor.keySet()){
                        JSONArray array = reactor.getJSONArray(s);
                        if(array==null){
                            error+="; Unknown reactor object: "+s;
                        }
                        ReactorPart part = ReactorPart.parse(s);
                        if(part == null){
                            error = "Unknown part: "+s;
                            part = ReactorPart.AIR;
                        }
                        for(Object obj : array){
                            if(obj instanceof JSONObject){
                                X = ((JSONObject)obj).getInt("X");
                                Y = ((JSONObject)obj).getInt("Y");
                                Z = ((JSONObject)obj).getInt("Z");
                                prts[X-1][Y-1][Z-1] = part;
                            }else{
                                error+="; Unknown object in reactor array: "+obj.toString();
                            }
                        }
                    }
                }else if(o instanceof JSONArray){
                    JSONArray array = (JSONArray)o;
                    ReactorPart part = ReactorPart.parse(name);
                    if(part == null){
                        error = "Unknown part: "+name;
                        part = ReactorPart.AIR;
                    }
                    for(Object obj : array){
                        if(obj instanceof JSONObject){
                            X = ((JSONObject)obj).getInt("X");
                            Y = ((JSONObject)obj).getInt("Y");
                            Z = ((JSONObject)obj).getInt("Z");
                            prts[X-1][Y-1][Z-1] = part;
                        }else{
                            error+="; Unknown object in reactor array: "+obj.toString();
                        }
                    }
                }
            }
        }
        Main.instance.textAreaImportOutput.setText(error.startsWith("; ")?error.substring(2):error);
        if(!error.isEmpty())return null;
        return new Reactor(fuel, type, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return prts[X][Y][Z]==null?ReactorPart.AIR:prts[X][Y][Z];
            }
        };
    }
    public static Reactor empty(Fuel fuel, Fuel.Type type, int x, int y, int z){
        return new Reactor(fuel, type, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.AIR;
            }
        };
    }
    public final Fuel fuel;
    public final Fuel.Type fuelType;
    public final int x;
    public final int y;
    public final int z;
    public final ReactorPart[][][] parts;
    public boolean[][][] active;
    public boolean[][][] blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved;
    private int[][][] neutronFlux;
    private double[][][] efficiency;
    private double[][][] positionalEfficiency;
    private int[][][] heatMult;
    private ArrayList<Cluster> clusters;
    public int rawOutput;//required for JSON export... Output without sparsity penalty, I'm assuming?
    public int totalOutput;
    public int totalHeat;
    public int totalCooling;
    public int netHeat;
    public double totalEfficiency;
    public double totalHeatMult;
    private double sparsityMult = 1;
    private int functionalBlocks;//required for JSON export
    public Reactor(Fuel fuel, Fuel.Type type, int x, int y, int z){
        this(fuel, type, x, y, z, Main.instance.checkBoxSymmetryX.isSelected(), Main.instance.checkBoxSymmetryY.isSelected(), Main.instance.checkBoxSymmetryZ.isSelected());
    }
    public Reactor(Fuel fuel, Fuel.Type type, int x, int y, int z, boolean symmetryX, boolean symmetryY, boolean symmetryZ){
        synchronized(synchronizer){
            totalReactors++;
        }
        if(fuel==null||type==null)throw new IllegalArgumentException("You can't have a reactor without fuel...");
        this.fuel = fuel;
        this.fuelType = type;
        this.x = x;
        this.y = y;
        this.z = z;
        parts = new ReactorPart[x][y][z];
        for(int X = 0; X<x; X++){
            for(int Y = 0; Y<y; Y++){
                for(int Z = 0; Z<z; Z++){
                    if(parts[X][Y][Z]!=null)continue;
                    ReactorPart part = build(X,Y,Z);
                    parts[X][Y][Z] = part;
                    if(symmetryX)parts[x-X-1][Y][Z] = part;
                    if(symmetryY)parts[X][y-Y-1][Z] = part;
                    if(symmetryZ)parts[X][Y][z-Z-1] = part;
                    if(symmetryX&&symmetryY)parts[x-X-1][y-Y-1][Z] = part;
                    if(symmetryY&&symmetryZ)parts[X][y-Y-1][z-Z-1] = part;
                    if(symmetryX&&symmetryZ)parts[x-X-1][Y][z-Z-1] = part;
                    if(symmetryX&&symmetryY&&symmetryZ)parts[x-X-1][y-Y-1][z-Z-1] = part;
                }
            }
        }
        applyExtraTransformations();
        build();
    }
    public boolean isValid(){
        return totalOutput>0;
    }
    /**
     * Use to apply extra symmetries on the reactor before it's built
     */
    protected void applyExtraTransformations(){}
    protected abstract ReactorPart build(int X, int Y, int Z);
    private ReactorPart get(int x, int y, int z){
        if(x==-1||y==-1||z==-1||x==this.x||y==this.y||z==this.z)return ReactorPart.CASING;
        return parts[x][y][z];
    }
    private void build(){
        clusters = new ArrayList<>();
        active = new boolean[x][y][z];
        blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved = new boolean[x][y][z];
        neutronFlux = new int[x][y][z];
        efficiency = new double[x][y][z];
        positionalEfficiency = new double[x][y][z];
        heatMult = new int[x][y][z];
        boolean[][][] fluxed = new boolean[x][y][z];
        boolean somethingChanged;
        if(false){
        //<editor-fold defaultstate="collapsed" desc="Removing fundamentally invalid parts">
        do{
            somethingChanged = false;
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    for(int z = 0; z<this.z; z++){
                        ReactorPart p = get(x, y, z);
                        if(p.type==ReactorPart.Type.REFLECTOR){
                            if(!hasLineOfSight(x, y, z, 2, ReactorPart.Type.MODERATOR, ReactorPart.Type.FUEL_CELL)){//if a reflector is fundamentally invalid, replace it with air
                                parts[x][y][z] = ReactorPart.AIR;
                                somethingChanged = true;
                                continue;
                            }
                        }
                        if(p.type==ReactorPart.Type.MODERATOR){
                            if(!hasLineOfSight(x, y, z, 1, ReactorPart.Type.MODERATOR, ReactorPart.Type.FUEL_CELL)){//if a moderator is fundamentally invalid, replace it with air
                                parts[x][y][z] = ReactorPart.AIR;
                                somethingChanged = true;
                                continue;
                            }
                        }
                        if(p.type==ReactorPart.Type.FUEL_CELL){
                            if(!hasLineOfSight(x,y,z,2, ReactorPart.Type.MODERATOR, ReactorPart.Type.REFLECTOR)&&!hasLineOfSight(x,y,z,4, ReactorPart.Type.MODERATOR, ReactorPart.Type.FUEL_CELL)){//if a cell is fundamentally invalid, replace it with air
                                parts[x][y][z] = ReactorPart.AIR;
                                somethingChanged = true;
                                continue;
                            }
                        }
                        if(p.type==ReactorPart.Type.FUEL_CELL){
                            if(((FuelCell)p).efficiency!=0&&!hasLineOfSight(x,y,z)){//if a cell has a source, but has no line-of-sight, remove the source
                                parts[x][y][z] = ReactorPart.FUEL_CELL;
                                somethingChanged = true;
                                continue;
                            }
                        }
//                        if(p.type==ReactorPart.Type.HEATSINK){//if a heatsink is fundamentally invalid, replace it with air
//                            for(PlacementRule rule : ((Heatsink)p).rules){
//                                if(!rule.isValid(this,x,y,z)){
//                                    parts[x][y][z] = ReactorPart.AIR;
//                                    somethingChanged = true;
//                                    break;
//                                }
//                            }
//                        }//this causes problems with magnesium!
                    }
                }
            }
        }while(somethingChanged);
//</editor-fold>
        }
        //<editor-fold defaultstate="collapsed" desc="Adding base cell efficiencies">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part.type==ReactorPart.Type.FUEL_CELL){
                        efficiency[x][y][z] = fuel.efficiency.get(fuelType).doubleValue();
                        positionalEfficiency[x][y][z] = 0;
                    }
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Fuel cells fluxing stuff">
        boolean hasDoneThePrimedOnes = false;
        do{
            somethingChanged = false;
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    for(int z = 0; z<this.z; z++){
                        if(fluxed[x][y][z])continue;//already processed this one, skip!
                        ReactorPart part = get(x, y, z);
                        if(part.type==ReactorPart.Type.FUEL_CELL){
                            FuelCell cell = (FuelCell)part;
                            if(cell.efficiency==0&&!hasDoneThePrimedOnes)continue;//do the primed ones first
                            if(cell.efficiency>0||active[x][y][z]){//if primed or has been activated
                                DIRECTION:for(Direction d : Direction.values()){
                                    int distance = 0;
                                    int X = x+d.x;
                                    int Y = y+d.y;
                                    int Z = z+d.z;
                                    int flux = 0;
                                    double modEfficiency = 0;
                                    WHILE:while(true){
                                        ReactorPart otherPart = get(X,Y,Z);
                                        switch(otherPart.type){
                                            case MODERATOR://continue
                                                X+=d.x;
                                                Y+=d.y;
                                                Z+=d.z;
                                                distance++;
                                                flux+=((Moderator)otherPart).fluxFactor;
                                                modEfficiency+=((Moderator)otherPart).efficiencyFactor;
                                                continue;
                                            case HEATSINK:
                                                if(isMSR()){
                                                    X+=d.x;
                                                    Y+=d.y;
                                                    Z+=d.z;
                                                    distance++;
                                                    continue;
                                                }
                                            case AIR:
                                            case CASING:
                                            case CONDUCTOR:
                                                continue DIRECTION;
                                            case FUEL_CELL:
                                                if(distance<1||distance>4)continue DIRECTION;//too far away!
                                                if(modEfficiency==0)continue DIRECTION;//no moderators!
                                                neutronFlux[X][Y][Z]+=flux;
                                                positionalEfficiency[X][Y][Z]+=(modEfficiency/distance);
                                                heatMult[X][Y][Z]++;
                                                somethingChanged = true;
                                                blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[x][y][z] = true;
                                                break WHILE;
                                            case REFLECTOR:
                                                if(distance<1||distance>2)continue DIRECTION;//too far away!
                                                neutronFlux[x][y][z]+=flux*((Reflector)otherPart).reflectivity*2;
                                                positionalEfficiency[x][y][z]+=(modEfficiency/distance)*((Reflector)otherPart).efficiency;
                                                heatMult[x][y][z]++;
                                                somethingChanged = true;
                                                break WHILE;
                                            default:
                                                throw new IllegalArgumentException("I don't know what this is!");//continue DIRECTION if this hits
                                        }
                                    }
                                    if(neutronFlux[X][Y][Z]>=fuel.criticality.get(fuelType).intValue()){
                                        active[X][Y][Z] = true;
                                        somethingChanged = true;
                                    }
                                    if(neutronFlux[x][y][z]>=fuel.criticality.get(fuelType).intValue()){
                                        active[x][y][z] = true;
                                        somethingChanged = true;
                                    }
                                }
                                fluxed[x][y][z] = true;
                            }
                        }
                    }
                }
            }
            hasDoneThePrimedOnes = true;
        }while(somethingChanged);
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Activating Moderators and reflectors">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part.type==ReactorPart.Type.FUEL_CELL){
                        if(active[x][y][z]){//if cell is active
                            DIRECTION:for(Direction d : Direction.values()){
                                int distance = 0;
                                int X = x+d.x;
                                int Y = y+d.y;
                                int Z = z+d.z;
                                WHILE:while(true){
                                    ReactorPart otherPart = get(X,Y,Z);
                                    switch(otherPart.type){
                                        case MODERATOR://continue
                                            X+=d.x;
                                            Y+=d.y;
                                            Z+=d.z;
                                            distance++;
                                            continue;
                                        case HEATSINK:
                                            if(isMSR()){
                                                X+=d.x;
                                                Y+=d.y;
                                                Z+=d.z;
                                                distance++;
                                                continue;
                                            }
                                        case AIR:
                                        case CASING:
                                        case CONDUCTOR:
                                            continue DIRECTION;
                                        case FUEL_CELL:
                                            if(distance<1||distance>4)continue DIRECTION;//too far away!
                                            if(!active[X][Y][Z])continue DIRECTION;//cell is inactive!
                                            if(parts[X-d.x*distance][Y-d.y*distance][Z-d.z*distance].type==ReactorPart.Type.MODERATOR)
                                                active[X-d.x*distance][Y-d.y*distance][Z-d.z*distance] = true;//activate first moderator in the chain
                                            if(parts[X-d.x][Y-d.y][Z-d.z].type==ReactorPart.Type.MODERATOR)
                                                active[X-d.x][Y-d.y][Z-d.z] = true;//activate last moderator in the chain
                                            for(int i = 1; i<=distance; i++){
                                                blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[X-d.x*i][Y-d.y*i][Z-d.z*i] = true;
                                            }
                                            break WHILE;
                                        case REFLECTOR:
                                            if(distance<1||distance>2)continue DIRECTION;//too far away!
                                            if(parts[X-d.x*distance][Y-d.y*distance][Z-d.z*distance].type==ReactorPart.Type.MODERATOR)
                                                active[X-d.x*distance][Y-d.y*distance][Z-d.z*distance] = true;//activate first moderator in the chain
                                            for(int i = 1; i<=distance; i++){
                                                blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[X-d.x*i][Y-d.y*i][Z-d.z*i] = true;
                                            }
                                            active[X][Y][Z] = true;//activate the reflector
                                            break WHILE;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Applying Positional efficiency, source efficiency, and Over-fluxing penalty">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part.type==ReactorPart.Type.FUEL_CELL){
                        efficiency[x][y][z] *= positionalEfficiency[x][y][z];
                        double sourceEff = ((FuelCell)part).efficiency;
                        if(sourceEff==0)sourceEff = 1;
                        efficiency[x][y][z] *= sourceEff;
                        efficiency[x][y][z] *= (1/(1+Math.exp(2*(neutronFlux[x][y][z]-2*fuel.criticality.get(fuelType).doubleValue()))));
                    }
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Activating Heatsinks">
        do{
            somethingChanged = false;
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    PART:for(int z = 0; z<this.z; z++){
                        ReactorPart part = get(x, y, z);
                        if(part.type==ReactorPart.Type.HEATSINK){
                            Heatsink sink = (Heatsink)part;
                            for(PlacementRule rule : sink.rules){
                                if(!rule.isActive(this, x, y, z))continue PART;//one of the rules is not met
                            }
                            if(!active[x][y][z]){
                                active[x][y][z] = true;//all rules are met!
                                somethingChanged = true;
                            }
                        }
                    }
                }
            }
        }while(somethingChanged);
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Removing invalid parts">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part==ReactorPart.AIR)continue;
                    if(part.matches(ReactorPart.Type.CONDUCTOR))continue;//I'll let conductors be anywhere, and won't really worry about it
                    if(!active[x][y][z]&&!blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[x][y][z]){
                        parts[x][y][z] = ReactorPart.AIR;
                    }
                }
            }
        }
//</editor-fold>
        if(Main.instance.jCheckBox1.isSelected()){
            //<editor-fold defaultstate="collapsed" desc="Fill with conductors">
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    for(int z = 0; z<this.z; z++){
                        ReactorPart part = get(x, y, z);
                        if(part.matches(ReactorPart.Type.AIR)){
                            parts[x][y][z] = ReactorPart.CONDUCTOR;
                        }
                    }
                }
            }
//</editor-fold>
        }
        //<editor-fold defaultstate="collapsed" desc="Detect Clusters">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    Cluster cluster = getCluster(x,y,z);
                    if(cluster==null)continue;//that's definitely not a cluster
                    if(clusters.contains(cluster))continue;//already did that one!
                    clusters.add(cluster);
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Deactivate clusters not adjacent to the wall">
        for(Cluster cluster : clusters){
            if(!cluster.isConnectedToWall){
                for(int[] b : cluster.blocks){
                    active[b[0]][b[1]][b[2]] = false;
                }
            }
        }
        //<editor-fold defaultstate="collapsed" desc="Deactivate clusters containing no fuel cells">
        FOR:for(Iterator<Cluster> it = clusters.iterator(); it.hasNext();){
            Cluster cluster = it.next();
            for(int[] b : cluster.blocks){
                if(get(b[0], b[1], b[2]).matches(ReactorPart.Type.FUEL_CELL)){
                    continue FOR;
                }
            }
            for(int[] b : cluster.blocks){
                active[b[0]][b[1]][b[2]] = false;
                parts[b[0]][b[1]][b[2]] = ReactorPart.AIR;
            }
            it.remove();
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Calculate Cluster Stats">
        for(Cluster cluster : clusters){
            cluster.calculateStats();
        }
//</editor-fold>
        for(Cluster cluster : clusters){
            rawOutput+=cluster.totalOutput;
            totalOutput+=cluster.totalOutput;
            totalCooling+=cluster.totalCooling;
            totalHeat+=cluster.totalHeat;
            netHeat+=cluster.netHeat;
            totalEfficiency+=cluster.efficiency;
            totalHeatMult+=cluster.heatMult;
        }
        totalEfficiency/=clusters.size();
        totalHeatMult/=clusters.size();
        if(Double.isNaN(totalEfficiency))totalEfficiency = 0;
        if(Double.isNaN(totalHeatMult))totalHeatMult = 0;
        functionalBlocks = 0;
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    if((active[x][y][z]||(parts[x][y][z].matches(ReactorPart.Type.MODERATOR)&&blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[x][y][z]))&&parts[x][y][z]!=ReactorPart.AIR)functionalBlocks++;
                }
            }
        }
        int volume = x*y*z;
        sparsityMult = functionalBlocks/(double)volume >= thresholdRatio ? 1 : minimumMult + (1 - minimumMult)*Math.sin(Math.PI*functionalBlocks/(2*volume*thresholdRatio));
        totalOutput*=sparsityMult;
        totalEfficiency*=sparsityMult;
        totalOutput/=16;//TODO add the recipes in- this is high pressure steam
    }
    private boolean hasLineOfSight(int x, int y, int z){
        return hasLineOfSight(x, y, z, 1, 0, 0)
            || hasLineOfSight(x, y, z, 0, 1, 0)
            || hasLineOfSight(x, y, z, 0, 0, 1)
            || hasLineOfSight(x, y, z, -1, 0, 0)
            || hasLineOfSight(x, y, z, 0, -1, 0)
            || hasLineOfSight(x, y, z, 0, 0, -1);
    }
    private boolean hasLineOfSight(int x, int y, int z, int dx, int dy, int dz){
        ReactorPart part = null;
        while(part==null||(part.type!=ReactorPart.Type.FUEL_CELL&&part.type!=ReactorPart.Type.REFLECTOR&&part.type!=ReactorPart.Type.CASING)){
            x+=dx;
            y+=dy;
            z+=dz;
            part = get(x, y, z);
        }
        return part.type==ReactorPart.Type.CASING;
    }
    private boolean hasLineOfSight(int x, int y, int z, int maxLength, ReactorPart.Type bridge, ReactorPart.Type... end){
        return hasLineOfSight(x, y, z, 1, 0, 0, maxLength, bridge, end)
            || hasLineOfSight(x, y, z, 0, 1, 0, maxLength, bridge, end)
            || hasLineOfSight(x, y, z, 0, 0, 1, maxLength, bridge, end)
            || hasLineOfSight(x, y, z, -1, 0, 0, maxLength, bridge, end)
            || hasLineOfSight(x, y, z, 0, -1, 0, maxLength, bridge, end)
            || hasLineOfSight(x, y, z, 0, 0, -1, maxLength, bridge, end);
    }
    private boolean hasLineOfSight(int x, int y, int z, int dx, int dy, int dz, int maxLength, ReactorPart.Type bridge, ReactorPart.Type... end){
        ReactorPart part = null;
        int bridgeLength = -1;
        while(part==null||part.type==bridge){
            x+=dx;
            y+=dy;
            z+=dz;
            bridgeLength++;
            part = get(x, y, z);
        }
        if(bridgeLength>maxLength)return false;
        for(ReactorPart.Type t : end){
            if(part.type==t)return true;
        }
        return false;
    }
    private boolean hasAdjacent(int x, int y, int z, ReactorBit... end){
        return hasAdjacent(x,y,z,1, 0, 0, end)
            || hasAdjacent(x,y,z,0, 1, 0, end)
            || hasAdjacent(x,y,z,0, 0, 1, end)
            || hasAdjacent(x,y,z,-1, 0, 0, end)
            || hasAdjacent(x,y,z,0, -1, 0, end)
            || hasAdjacent(x,y,z,0, 0, -1, end);
    }
    private boolean hasAdjacent(int x, int y, int z, int dx, int dy, int dz, ReactorBit... end){
        ReactorPart part = get(x+dx, y+dy, z+dz);
        for(ReactorBit t : end){
            if(part.matches(t))return true;
        }
        return false;
    }
    boolean matches(int x, int y, int z, ReactorBit bit, boolean checkActive){
        ReactorPart part = get(x,y,z);
        if(!part.matches(bit))return false;
        if(checkActive){
            if(part.type==ReactorPart.Type.CASING)return true;
            return active[x][y][z];
        }
        return true;
    }
    private Cluster getCluster(int x, int y, int z){
        ReactorPart part = get(x, y, z);
        if(!part.type.canCluster)return null;//That block can't cluster!
        for(Cluster cluster : clusters){
            if(cluster.contains(x,y,z))return cluster;//this block is already part of a cluster!
        }
        return new Cluster(x, y, z);
    }
    public String getDetails(){
        return getDetails(Main.instance.checkBoxShowClusters.isSelected(), false);
    }
    public String getDetails(boolean showClusters, boolean showParts){
        String s = "Total output: "+totalOutput+" mb/t of High Pressure Steam\n"
                + "Total Heat: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heat: "+netHeat+"H/t\n"
                + "Overall Efficiency: "+Math.round(totalEfficiency*100)+"%\n"
                + "Overall Heat Multiplier: "+Math.round(totalHeatMult*100)+"%\n"
                + "Sparsity Penalty Multiplier: "+Math.round(sparsityMult*10000)/10000d;
        if(showClusters){
            for(Cluster c : clusters){
                s+="\n\n"+c.getDetails();
            }
        }
        if(showParts){
            for(int y = this.y-1; y>=0; y--){
                s+="\n\n";
                for(int z = 0; z<this.z; z++){
                    s+="\n";
                    for(int x = 0; x<this.x; x++){
                        ReactorPart part = parts[x][y][z];
                        String nam = part==null?"   ":part.toString().substring(0,3);
                        s+=" "+nam;
                    }
                }
            }
        }
        return s;
    }
    public int getClusterID(int x, int y, int z){
        return clusters.indexOf(getCluster(x, y, z));
    }
    public JSONObject exportJSON(){
        JSONObject obj = new JSONObject();
        //<editor-fold defaultstate="collapsed" desc="SaveVersion">
        JSONObject saveVersion = new JSONObject();
        saveVersion.set("Major", MAJOR_VERSION);
        saveVersion.set("Minor", MINOR_VERSION);
        saveVersion.set("Build", BUILD);
        saveVersion.set("Revision", REVISION);
        saveVersion.set("MajorRevision", MAJOR_REVISION);
        saveVersion.set("MinorRevision", MINOR_REVISION);
        obj.set("SaveVersion", saveVersion);
//</editor-fold>
        HashMap<ReactorPart, ArrayList<int[]>> prts = new HashMap<>();
        for(int X = 0; X<x; X++){
            for(int Y = 0; Y<y; Y++){
                for(int Z = 0; Z<z; Z++){
                    ReactorPart part = parts[X][Y][Z];
                    if(prts.containsKey(part)){
                        prts.get(part).add(new int[]{X,Y,Z});
                    }else{
                        ArrayList<int[]> prt = new ArrayList<>();
                        prt.add(new int[]{X,Y,Z});
                        prts.put(part, prt);
                    }
                }
            }
        }
        JSONObject heatSinks = new JSONObject();
        JSONObject moderators = new JSONObject();
        JSONArray conductors = new JSONArray();
        JSONObject reflectors = new JSONObject();
        JSONObject fuelCells = new JSONObject();
        for(ReactorPart part : prts.keySet()){
            switch(part.type){
                case AIR:
                case CASING:
                    break;
                case CONDUCTOR:
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        conductors.add(partt);
                    }
                    break;
                case FUEL_CELL:
                    JSONArray array = new JSONArray();
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        array.add(partt);
                    }
                    fuelCells.set(part.jsonName.replace("{FUEL}", "["+fuelType.name()+"]"+fuel.toString()), array);
                    break;
                case HEATSINK:
                    array = new JSONArray();
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        array.add(partt);
                    }
                    heatSinks.set(part.jsonName, array);
                    break;
                case MODERATOR:
                    array = new JSONArray();
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        array.add(partt);
                    }
                    moderators.set(part.jsonName, array);
                    break;
                case REFLECTOR:
                    array = new JSONArray();
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        array.add(partt);
                    }
                    reflectors.set(part.jsonName, array);
                    break;
                default:
                    throw new IllegalArgumentException("I don't know what this part is: "+part.toString()+"!");
            }
        }
        obj.set("HeatSinks", heatSinks);
        obj.set("Moderators", moderators);
        obj.set("Conductors", conductors);
        obj.set("Reflectors", reflectors);
        obj.set("FuelCells", fuelCells);
        JSONObject interiorDimensions = new JSONObject();
        interiorDimensions.set("X", x);
        interiorDimensions.set("Y", y);
        interiorDimensions.set("Z", z);
        obj.set("InteriorDimensions", interiorDimensions);
        obj.set("CoolantRecipeName", "Water to High Pressure Steam");
        JSONObject reactorStats = new JSONObject();
        reactorStats.set("TotalOutput", totalOutput);
        reactorStats.set("RawOutput", rawOutput);
        reactorStats.set("TotalHeatPerTick", totalHeat);
        reactorStats.set("TotalCoolingPerTick", totalCooling);
        reactorStats.set("NetHeatPerTick", netHeat);
        reactorStats.set("OverallEfficiency", totalEfficiency);
        reactorStats.set("OverallHeatMultiplier", totalHeatMult);
        reactorStats.set("FunctionalBlocks", functionalBlocks);
        reactorStats.set("TotalInteriorBlocks", x*y*z);
        reactorStats.set("SparsityPenaltyMultiplier", sparsityMult);
        obj.set("ReactorOverallStats", reactorStats);
        return obj;
    }
    private class Cluster{
        public ArrayList<int[]> blocks = new ArrayList<>();
        public boolean isConnectedToWall = false;
        public double totalOutput = 0;
        public double efficiency;
        public int totalHeat;
        public int totalCooling;
        public int netHeat;
        public double heatMult;
        public double coolingPenaltyMult;
        public Cluster(int x, int y, int z){
            blocks.addAll(toList(getBlocks(clusterables, x, y, z)));
            ArrayList<ReactorBit> conductorList = new ArrayList<>();
            conductorList.add(ReactorPart.Type.CONDUCTOR);
            WALLCHECK:for(int[] b : blocks){
                if(hasAdjacent(b[0], b[1], b[2], ReactorPart.Type.CASING)){
                    isConnectedToWall = true;
                    break WALLCHECK;//don't need to check anything else now
                }
                for(int[] c : toList(getBlocks(conductorList, b[0], b[1], b[2]))){
                    if(hasAdjacent(c[0], c[1], c[2], ReactorPart.Type.CASING)){
                        isConnectedToWall = true;
                        break WALLCHECK;//don't need to check anything else now
                    }
                }
            }
        }
        public boolean contains(int x, int y, int z){
            for(int[] block : blocks){
                if(block[0]==x&&block[1]==y&&block[2]==z)return true;
            }
            return false;
        }
        private void calculateStats(){
            int fuelCells = 0;
            for(int[] block : blocks){
                if(!active[block[0]][block[1]][block[2]])continue;
                ReactorPart part = get(block[0], block[1], block[2]);
                switch(part.type){
                    case FUEL_CELL:
                        fuelCells++;
                        totalOutput+=fuel.heat.get(fuelType).intValue()*Reactor.this.efficiency[block[0]][block[1]][block[2]];
                        efficiency+=Reactor.this.efficiency[block[0]][block[1]][block[2]];
                        totalHeat+=Reactor.this.heatMult[block[0]][block[1]][block[2]]*fuel.heat.get(fuelType).intValue();
                        heatMult+=Reactor.this.heatMult[block[0]][block[1]][block[2]];
                        break;
                    case HEATSINK:
                        Heatsink sink = (Heatsink)part;
                        totalCooling+=sink.cooling;
                        break;
                    default:
                        throw new IllegalArgumentException("Wait, I didn't know "+part.type.toString()+" could be in a cluster! WHAT DO I DO?!?!?");
                }
            }
            efficiency/=fuelCells;
            heatMult/=fuelCells;
            netHeat = totalHeat-totalCooling;
            if(totalCooling==0){
                coolingPenaltyMult = 1;
            }else{
                coolingPenaltyMult = Math.min(1, (totalHeat+leniency)/(double)totalCooling);
            }
            efficiency*=coolingPenaltyMult;
        }
        private String getDetails(){
            return "Total output: "+Math.round(totalOutput)+"\n"
                + "Efficiency: "+Math.round(efficiency*100)+"%\n"
                + "Total Heating: "+totalHeat+"H/t\n"
                + "Total Cooling: "+totalCooling+"H/t\n"
                + "Net Heating: "+netHeat+"H/t\n"
                + "Heat Multiplier: "+Math.round(heatMult*100)+"%\n"
                + "Cooling penalty mult: "+Math.round(coolingPenaltyMult*10000)/10000d;
        }
    }
    private static enum Direction{
        XP(1,0,0),
        XN(-1,0,0),
        YP(0,1,0),
        YN(0,-1,0),
        ZP(0,0,1),
        ZN(0,0,-1);
        public final int x;
        public final int y;
        public final int z;
        private Direction(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    /**
     * Block search algorithm from my Tree Feller for Bukkit.
     */
    private HashMap<Integer, ArrayList<int[]>> getBlocks(ArrayList<ReactorBit> types, int x, int y, int z){
        //layer zero
        HashMap<Integer, ArrayList<int[]>> results = new HashMap<>();
        ArrayList<int[]> zero = new ArrayList<>();
        if(get(x,y,z).matches(types)){
            zero.add(new int[]{x,y,z});
        }
        results.put(0, zero);
        //all the other layers
        int maxDistance = this.x*this.y*this.z;//the algorithm requires a max search distance. Rather than changing that, I'll just be lazy and give it a big enough number
        for(int i = 0; i<maxDistance; i++){
            ArrayList<int[]> layer = new ArrayList<>();
            ArrayList<int[]> lastLayer = new ArrayList<>(results.get(i));
            if(i==0&&lastLayer.isEmpty()){
                lastLayer.add(new int[]{x,y,z});
            }
            for(int[] block : lastLayer){
                FOR:for(int j = 0; j<6; j++){
                    int dx=0,dy=0,dz=0;
                    switch(j){//lol this is a primitive version of the Direction class used in other places here, but I'll just leave it as it is
                        case 0:
                            dx = -1;
                            break;
                        case 1:
                            dx = 1;
                            break;
                        case 2:
                            dy = -1;
                            break;
                        case 3:
                            dy = 1;
                            break;
                        case 4:
                            dz = -1;
                            break;
                        case 5:
                            dz = 1;
                            break;
                        default:
                            throw new IllegalArgumentException("How did this happen?");
                    }
                    int[] newBlock = new int[]{block[0]+dx,block[1]+dy,block[2]+dz};
                    if(!get(newBlock[0], newBlock[1], newBlock[2]).matches(types)){//that's not part of this bunch
                        continue;
                    }
                    for(int[] oldbl : lastLayer){//if(lastLayer.contains(newBlock))continue;//if the new block is on the same layer, ignore
                        if(oldbl[0]==newBlock[0]&&oldbl[1]==newBlock[1]&&oldbl[2]==newBlock[2]){
                            continue FOR;
                        }
                    }
                    if(i>0){
                        for(int[] oldbl : results.get(i-1)){//if(i>0&&results.get(i-1).contains(newBlock))continue;//if the new block is on the previous layer, ignore
                            if(oldbl[0]==newBlock[0]&&oldbl[1]==newBlock[1]&&oldbl[2]==newBlock[2]){
                                continue FOR;
                            }
                        }
                    }
                    for(int[] oldbl : layer){//if(layer.contains(newBlock))continue;//if the new block is on the next layer, but already processed, ignore
                        if(oldbl[0]==newBlock[0]&&oldbl[1]==newBlock[1]&&oldbl[2]==newBlock[2]){
                            continue FOR;
                        }
                    }
                    layer.add(newBlock);
                }
            }
            if(layer.isEmpty())break;
            results.put(i+1, layer);
        }
        return results;
    }
    /**
     * Converts the tiered search returned by getBlocks into a list of blocks.<br>
     * Also from my tree feller
     */
    private static ArrayList<int[]> toList(HashMap<Integer, ArrayList<int[]>> blocks){
        ArrayList<int[]> list = new ArrayList<>();
        for(int i : blocks.keySet()){
            list.addAll(blocks.get(i));
        }
        return list;
    }
    private boolean isMSR(){
        return fuelType.isMSR();
    }
}