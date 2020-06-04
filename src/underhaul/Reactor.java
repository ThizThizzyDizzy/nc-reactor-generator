package underhaul;
import common.Direction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.JTextArea;
import common.JSON;
import common.JSON.JSONObject;
import common.JSON.JSONArray;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
public abstract class Reactor{
    //The export format is based on this version of hellrage's reactor planner: (Saved in the json file)
    public static final int MAJOR_VERSION = 1;
    public static final int MINOR_VERSION = 2;
    public static final int BUILD = 23;
    public static final int REVISION = 0;
    public static final int MAJOR_REVISION = 0;
    public static final int MINOR_REVISION = 0;
    public static final Object synchronizer = new Object();//I have a lot of these...
    public static long totalReactors;
    public static long startTime;
    private static final int leniency = 10;
    private static final double thresholdRatio = .75;
    private static final double minimumMult = .5;
    public static Reactor random(Fuel fuel, int x, int y, int z, Random rand){
        return new Reactor(fuel, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.random(rand);
            }
        };
    }
    private static final ArrayList<ReactorBit> clusterables = new ArrayList<>();
    static{//There's probably a much easier and better way of doing this
        for(ReactorPart.Type t : ReactorPart.Type.values()){
            if(t.canCluster)clusterables.add(t);
        }
    }
    public static boolean isbetter(Reactor reactor, Reactor other){
        return compare(reactor, other)==1;
    }
    static int compare(Reactor reactor, Reactor other){
        for(Priority p : Priority.priorities){
            double comparison = p.compare(reactor, other);
            if(comparison>0)return 1;
            if(comparison<0)return -1;
        }
        return 0;
    }
    public static Reactor parse(JTextArea textAreaImport, Fuel fuel, int x, int y, int z){
        return parse(textAreaImport.getText(), fuel, x, y, z);
    }
    public static Reactor parse(String text, Fuel fuel, int x, int y, int z){
        String error = "";
        if(text.startsWith("{")){
            try{
                return parseJSON(text, fuel, x, y, z);
            }catch(Exception ex){
                Main.instance.textAreaImportOutput.setText(ex.getMessage());
                return null;
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
        if(!error.isEmpty())return null;
        return new Reactor(fuel, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return prts[X][Y][Z]==null?ReactorPart.AIR:prts[X][Y][Z];
            }
        };
    }
    private static Reactor parseJSON(String text, Fuel fuel, int x, int y, int z) throws IOException{
        JSONObject json;
        json = JSON.parse(text);
        return parseJSON(json, fuel, x, y, z);
    }
    public static Reactor parseJSON(JSONObject json, Fuel fuel) throws IOException{
        int x = 0,y = 0,z = 0;
        try{
            if(json.containsKey("Data"))json = json.getJSONObject("Data");
            Object dim = json.get("InteriorDimensions");
            if(dim instanceof JSONObject){
                x = ((JSONObject)dim).getInt("X");
                y = ((JSONObject)dim).getInt("Y");
                z = ((JSONObject)dim).getInt("Z");
            }
            if(dim instanceof String){
                String[] strs = ((String)dim).split(",");
                x = Integer.parseInt(strs[0]);
                y = Integer.parseInt(strs[1]);
                z = Integer.parseInt(strs[2]);
            }
        }catch(Exception ex){}
        return parseJSON(json, fuel, x, y, z);
    }
    public static Reactor parseJSON(JSONObject json, Fuel fuel, int x, int y, int z) throws IOException{
        if(Main.instance!=null)Main.instance.textAreaImportOutput.setText("");
        String error = "";
        if(json==null)return null;
        if(json.containsKey("Data"))json = json.getJSONObject("Data");
        Object dim = json.get("InteriorDimensions");
        int X,Y,Z;
        if(dim==null)return null;
        if(dim instanceof JSONObject){
            X = ((JSONObject)dim).getInt("X");
            Y = ((JSONObject)dim).getInt("Y");
            Z = ((JSONObject)dim).getInt("Z");
        }else if(dim instanceof String){
            String[] strs = ((String)dim).split(",");
            X = Integer.parseInt(strs[0]);
            Y = Integer.parseInt(strs[1]);
            Z = Integer.parseInt(strs[2]);
        }else return null;
        ReactorPart[][][] prts = new ReactorPart[X][Y][Z];
        if(X!=x||Y!=y||Z!=z){
            error = "Incorrect dimensions! Found "+X+" "+Y+" "+Z+", expected "+x+" "+y+" "+z+"!";
        }else{
            JSONObject reactor = json.getJSONObject("CompressedReactor");
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
                    }else if(obj instanceof String){
                        String[] strs = ((String)obj).split(",");
                        X = Integer.parseInt(strs[0]);
                        Y = Integer.parseInt(strs[1]);
                        Z = Integer.parseInt(strs[2]);
                        prts[X-1][Y-1][Z-1] = part;
                    }else{
                        error+="; Unknown object in reactor array: "+obj.toString();
                    }
                }
            }
        }
        if(Main.instance!=null)Main.instance.textAreaImportOutput.setText(error.startsWith("; ")?error.substring(2):error);
        if(!error.isEmpty())return null;
        return new Reactor(fuel, x, y, z, false, false, false){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return prts[X][Y][Z]==null?ReactorPart.AIR:prts[X][Y][Z];
            }
        };
    }
    public static Reactor empty(Fuel fuel, int x, int y, int z){
        return new Reactor(fuel, x, y, z){
            @Override
            protected ReactorPart build(int X, int Y, int Z){
                return ReactorPart.AIR;
            }
        };
    }
    public final Fuel fuel;
    public final int x;
    public final int y;
    public final int z;
    public final ReactorPart[][][] parts;
    public boolean[][][] active;
    public boolean[][][] blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved;
    private double[][][] efficiency;
    public double power;
    public int heat;
    public double totalEfficiency;
    public Reactor(Fuel fuel, int x, int y, int z){
        this(fuel, x, y, z, Main.instance==null?false:Main.instance.checkBoxSymmetryX.isSelected(), Main.instance==null?false:Main.instance.checkBoxSymmetryY.isSelected(), Main.instance==null?false:Main.instance.checkBoxSymmetryZ.isSelected());
    }
    public Reactor(Fuel fuel, int x, int y, int z, boolean symmetryX, boolean symmetryY, boolean symmetryZ){
        synchronized(synchronizer){
            totalReactors++;
        }
        if(fuel==null)throw new IllegalArgumentException("You can't have a reactor without fuel...");
        this.fuel = fuel;
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
        build();
    }
    public boolean isValid(){
        return power>0;
    }
    protected abstract ReactorPart build(int X, int Y, int Z);
    private ReactorPart get(int x, int y, int z){
        if(x==-1||y==-1||z==-1||x==this.x||y==this.y||z==this.z)return ReactorPart.CASING;
        return parts[x][y][z];
    }
    private void build(){
        active = new boolean[x][y][z];
        blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved = new boolean[x][y][z];
        efficiency = new double[x][y][z];
        int cells = 0;
        boolean somethingChanged;
        //<editor-fold defaultstate="collapsed" desc="Removing fundamentally invalid parts">
        do{
            somethingChanged = false;
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    for(int z = 0; z<this.z; z++){
                        ReactorPart p = get(x, y, z);
                        if(p.type==ReactorPart.Type.MODERATOR){
                            if(!hasLineOfSight(x, y, z, 1, ReactorPart.Type.MODERATOR, ReactorPart.Type.FUEL_CELL)){//if a moderator is fundamentally invalid, replace it with air
                                parts[x][y][z] = ReactorPart.AIR;
                                somethingChanged = true;
                                continue;
                            }
                        }
                        if(p.type==ReactorPart.Type.COOLER){//if a cooler is fundamentally invalid, replace it with air
                            for(PlacementRule rule : ((Cooler)p).rules){
                                if(!rule.isValid(this,x,y,z)){
                                    parts[x][y][z] = ReactorPart.AIR;
                                    somethingChanged = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }while(somethingChanged);
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Fuel cells">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part.type==ReactorPart.Type.FUEL_CELL){
                        cells++;
                        int eff = 1;
                        //<editor-fold defaultstate="collapsed" desc="Marking Moderators for nondeletion">
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
                                    case AIR:
                                    case CASING:
                                    case COOLER:
                                        continue DIRECTION;
                                    case FUEL_CELL:
                                        if(distance<1||distance>4)continue DIRECTION;//too far away!
                                        for(int i = 1; i<=distance; i++){
                                            blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[X-d.x*i][Y-d.y*i][Z-d.z*i] = true;
                                        }
                                        break WHILE;
                                    default:
                                        throw new IllegalArgumentException("I don't know what this is!");//continue DIRECTION if this hits
                                }
                            }
                        }
//</editor-fold>
                        for(Direction d : Direction.values()){
                            if(hasLineOfSight(x, y, z, d.x, d.y, d.z, 4, ReactorPart.Type.MODERATOR, ReactorPart.Type.FUEL_CELL)){
                                eff++;
                            }
                        }
                        efficiency[x][y][z] = eff;
                        power+=eff*fuel.power;
                        heat+=(eff*(eff+1))/2d*fuel.heat;
                        active[x][y][z] = true;
                    }
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Moderators">
        for(int x = 0; x<this.x; x++){
            for(int y = 0; y<this.y; y++){
                for(int z = 0; z<this.z; z++){
                    ReactorPart part = get(x, y, z);
                    if(part.type==ReactorPart.Type.MODERATOR){
                        int adjacent = 0;
                        for(Direction d : Direction.values()){
                            if(get(x+d.x, y+d.y, z+d.z).matches(ReactorPart.Type.FUEL_CELL)){
                                adjacent++;
                                power+=efficiency[x+d.x][y+d.y][z+d.z]*fuel.power/6;
                                heat+=efficiency[x+d.x][y+d.y][z+d.z]*fuel.heat/3;
                            }
                        }
                        if(adjacent>0)active[x][y][z] = true;
                    }
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Activating Coolers">
        do{
            somethingChanged = false;
            for(int x = 0; x<this.x; x++){
                for(int y = 0; y<this.y; y++){
                    PART:for(int z = 0; z<this.z; z++){
                        ReactorPart part = get(x, y, z);
                        if(part.type==ReactorPart.Type.COOLER){
                            if(active[x][y][z])continue;//already did this one
                            Cooler cooler = (Cooler)part;
                            for(PlacementRule rule : cooler.rules){
                                if(!rule.isActive(this, x, y, z))continue PART;//one of the rules is not met
                            }
                            heat-=cooler.cooling;
                            active[x][y][z] = true;//all rules are met!
                            somethingChanged = true;
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
                    if(!active[x][y][z]&&!blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[x][y][z]){
                        parts[x][y][z] = ReactorPart.AIR;
                    }
                }
            }
        }
//</editor-fold>
        if(cells==0)totalEfficiency = 0;
        else totalEfficiency = power/cells/fuel.power;
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
        return countAdjacent(x, y, z, end)>0;
    }
    private int countAdjacent(int x, int y, int z, ReactorBit... end){
        return (hasAdjacent(x,y,z,1, 0, 0, end)?1:0)
             + (hasAdjacent(x,y,z,0, 1, 0, end)?1:0)
             + (hasAdjacent(x,y,z,0, 0, 1, end)?1:0)
             + (hasAdjacent(x,y,z,-1, 0, 0, end)?1:0)
             + (hasAdjacent(x,y,z,0, -1, 0, end)?1:0)
             + (hasAdjacent(x,y,z,0, 0, -1, end)?1:0);
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
    public String getDetails(){
        return getDetails(false);
    }
    public String getDetails(boolean showParts){
        String s = "Power Generation: "+(int)power+"RF/t\n"
                + "Heat: "+(int)heat+"H/t\n"
                + "Efficiency: "+Math.round(totalEfficiency*1000)/10d+"%\n"
                + "Fuel cells: "+getFuelCells();
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
        JSONObject compressedReactor = new JSONObject();
        for(ReactorPart part : prts.keySet()){
            switch(part.type){
                case AIR:
                case CASING:
                    break;
                case FUEL_CELL:
                case COOLER:
                case MODERATOR:
                    JSONArray array = new JSONArray();
                    for(int[] i : prts.get(part)){
                        JSONObject partt = new JSONObject();
                        partt.set("X", i[0]+1);
                        partt.set("Y", i[1]+1);
                        partt.set("Z", i[2]+1);
                        array.add(partt);
                    }
                    compressedReactor.set(part.jsonName, array);
                    break;
                default:
                    throw new IllegalArgumentException("I don't know what this part is: "+part.toString()+"!");
            }
        }
        obj.set("CompressedReactor", compressedReactor);
        JSONObject interiorDimensions = new JSONObject();
        interiorDimensions.set("X", x);
        interiorDimensions.set("Y", y);
        interiorDimensions.set("Z", z);
        obj.set("InteriorDimensions", interiorDimensions);
        JSONObject usedFuel = new JSONObject();
        usedFuel.set("Name", fuel.toString());
        usedFuel.set("BasePower", fuel.power);
        usedFuel.set("BaseHeat", fuel.heat);
        usedFuel.set("FuelTime", fuel.time);
        obj.set("UsedFuel", usedFuel);
        return obj;
    }
    public int getFuelCells(){
        int cells = 0;
        for(int X = 0; X<x; X++){
            for(int Y = 0; Y<y; Y++){
                for(int Z = 0; Z<z; Z++){
                    if(parts[X][Y][Z].matches(ReactorPart.Type.FUEL_CELL))cells++;
                }
            }
        }
        return cells;
    }
    public BufferedImage getImage(){
        int blockSize = 16;
        BufferedImage image = new BufferedImage(blockSize*x, blockSize*(((z+1)*y)-1), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.createGraphics();
        g.setColor(new Color(0,0,0,0));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        int yOff = 0;
        for(int Y = y-1; Y>=0; Y--){
            for(int Z = 0; Z<z; Z++){
                for(int X = 0; X<x; X++){
                    g.drawImage(parts[X][Y][Z].getImage(), X*blockSize, yOff, blockSize, blockSize, null);
                    if(parts[X][Y][Z]==ReactorPart.AIR)continue;//ignore
//                    if(active[x][y][z]){
//                        g.setColor(new Color(0, 255, 0, 127));
//                        g.fillRect(x*blockSize, yOff, blockSize, blockSize);
//                    }

//                    if(!active[x][y][z]&&!blocksThatAreNotNeccesarilyActiveButHaveBeenUsedSoTheyShouldNotBeRemoved[x][y][z]){
//                        g.setColor(new Color(255, 0, 0, 127));
//                        g.fillRect(x*blockSize, yOff, blockSize, blockSize);
//                    }
                    
//                    int id = getClusterID(x,y,z);
//                    if(id>-1){
//                        g.setColor(new Color(id*80, 0, 0, 127));
//                        g.fillRect(x*blockSize, yOff, blockSize, blockSize);
//                    }
                }
                yOff+=blockSize;
            }
            yOff+=blockSize;
        }
        return image;
    }
}