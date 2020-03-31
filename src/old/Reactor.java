package old;
import java.awt.Color;
import java.util.ArrayList;
import simplelibraryextended.JSON;
public abstract class Reactor{
    public static Reactor parse(Fuel fuel, int X, int Y, int Z, String text) {
        try{
            final String txt = text.trim();
            return new Reactor(fuel, X, Y, Z) {
                @Override
                protected ReactorParts build(int x, int y, int z) {
                    int row = z+(Y-y-1)*(Z+1);
                    int column = x;
                    System.out.println(column+" "+row+" = "+x+" "+y+" "+z);
                    ReactorParts part = ReactorParts.fromChar(txt.split("\n")[row].charAt(column));
                    if(part==null){
                        System.out.println("WHAT IS "+txt.split("\n")[row].charAt(column));
                        throw new NullPointerException();
                    }
                    return part;
                }
            };
        }catch(NullPointerException | ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException ex){
        }
//        JSON.JSONObject json = JSON.parse(text);
//        JSON.JSONObject dim = json.getJSONObject("InteriorDimensions");
//        if(dim==null)return null;
//        int x = dim.getInt("X");
//        int y = dim.getInt("X");
//        int z = dim.getInt("X");
//        if(x!=X||y!=Y||z!=Z){
//            System.err.println("Incorrect dimensions! Found "+x+" "+y+" "+z+", expected "+X+" "+Y+" "+Z+"!");
//        }
//        JSON.JSONObject reactor = json.getJSONObject("CompressedReactor");
//        ReactorParts[][][] parts
//        for(String s : reactor.keySet()){
//            JSONObject o = reactor.getJSONObject(s);
//        }
        return null;
    }
    public final ReactorParts[][][] parts;
    public float power, heat;
    private final Fuel fuel;
    int[][][] efficiency;
    double totalEfficiency;
    public static ArrayList<Priority> priorities = new ArrayList<>();
    public Color color = null;
    public Reactor(Fuel fuel, int x, int y, int z){
        if(fuel==null||x<0||y<0||z<0){
            throw new IllegalArgumentException("Invalid reactor! Fuel: "+fuel+", "+x+"x"+y+"x"+z);
        }
        parts = new ReactorParts[x][y][z];
        for(int X = 0; X<x; X++){
            for(int Y = 0; Y<y; Y++){
                for(int Z = 0; Z<z; Z++){
                    parts[X][Y][Z] = build(X,Y,Z);
                }
            }
        }
        this.fuel = fuel;
        build();
    }
    protected abstract ReactorParts build(int X, int Y, int Z);
    private void build(){
        power = heat = 0;
        int cells = 0;
        efficiency = new int [parts.length][parts[0].length][parts[0][0].length];
        //<editor-fold defaultstate="collapsed" desc="Cells">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.CELL){
                        cells++;
                        int eff = 1;
                        if(getDistance(x,y,z,ReactorParts.CELL,1,0,0, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        if(getDistance(x,y,z,ReactorParts.CELL,0,1,0, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        if(getDistance(x,y,z,ReactorParts.CELL,0,0,1, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        if(getDistance(x,y,z,ReactorParts.CELL,-1,0,0, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        if(getDistance(x,y,z,ReactorParts.CELL,0,-1,0, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        if(getDistance(x,y,z,ReactorParts.CELL,0,0,-1, ReactorParts.MODERATOR)<=5){
                            eff++;
                        }
                        efficiency[x][y][z] = eff;
                        power+=eff*fuel.power;
                        heat+=(eff*(eff+1))/2d*fuel.heat;
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Moderators">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.MODERATOR){
                        boolean hasCell = false;
                        if(getPart(x+1, y, z)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x+1][y][z]*fuel.power/6;
                            heat+=efficiency[x+1][y][z]*fuel.heat/3;
                        }
                        if(getPart(x, y+1, z)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x][y+1][z]*fuel.power/6;
                            heat+=efficiency[x][y+1][z]*fuel.heat/3;
                        }
                        if(getPart(x, y, z+1)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x][y][z+1]*fuel.power/6;
                            heat+=efficiency[x][y][z+1]*fuel.heat/3;
                        }
                        if(getPart(x-1, y, z)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x-1][y][z]*fuel.power/6;
                            heat+=efficiency[x-1][y][z]*fuel.heat/3;
                        }
                        if(getPart(x, y-1, z)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x][y-1][z]*fuel.power/6;
                            heat+=efficiency[x][y-1][z]*fuel.heat/3;
                        }
                        if(getPart(x, y, z-1)==ReactorParts.CELL){
                            hasCell = true;
                            power+=efficiency[x][y][z-1]*fuel.power/6;
                            heat+=efficiency[x][y][z-1]*fuel.heat/3;
                        }
                        if(hasCell)efficiency[x][y][z] = 1;
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Water">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.WATER){
                        if(getAdjacent(x,y,z,ReactorParts.CELL, ReactorParts.MODERATOR)>0){
                            heat-=60;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Redstone">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.REDSTONE){
                        if(getAdjacent(x,y,z,ReactorParts.CELL)>0){
                            heat-=90;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Quartz">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.QUARTZ){
                        if(getAdjacent(x,y,z,ReactorParts.MODERATOR)>0){
                            heat-=90;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Gold">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.GOLD){
                        if(getAdjacent(x,y,z,ReactorParts.WATER)>0&&getAdjacent(x,y,z,ReactorParts.REDSTONE)>0){
                            heat-=120;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Glowstone">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.GLOWSTONE){
                        if(getAdjacent(x,y,z,ReactorParts.MODERATOR)>=2){
                            heat-=130;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Lapis">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.LAPIS){
                        if(getAdjacent(x,y,z,ReactorParts.CELL)>=1&&getAdjacent(x,y,z,(ReactorParts)null)>=1){
                            heat-=120;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Diamond">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.DIAMOND){
                        if(getAdjacent(x,y,z,ReactorParts.WATER)>=1&&getAdjacent(x,y,z,ReactorParts.QUARTZ)>=1){
                            heat-=150;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Helium">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.HELIUM){
                        if(getAdjacent(x,y,z,ReactorParts.REDSTONE)==1&&getAdjacent(x,y,z,(ReactorParts)null)>=1){
                            heat-=140;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Enderium">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.ENDERIUM){
                        if(getAdjacent(x,y,z,(ReactorParts)null)>=3&&parts.length>=2&&parts[0].length>=2&&parts[0][0].length>=2){
                            heat-=120;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Cryotheum">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.CRYOTHEUM){
                        if(getAdjacent(x,y,z,ReactorParts.CELL)>=2){
                            heat-=160;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Iron">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.IRON){
                        if(getAdjacent(x,y,z,ReactorParts.GOLD)>=1){
                            heat-=80;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Emerald">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.EMERALD){
                        if(getAdjacent(x,y,z,ReactorParts.CELL)>=1&&getAdjacent(x,y,z,ReactorParts.MODERATOR)>=1){
                            heat-=160;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Copper">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.COPPER){
                        if(getAdjacent(x,y,z,ReactorParts.GLOWSTONE)>=1){
                            heat-=80;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Tin">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.TIN){
                        boolean valid = false;
                        if(isPart(x-1, y, z, ReactorParts.LAPIS)&&isPart(x+1,y,z, ReactorParts.LAPIS)){
                            valid = true;
                        }
                        if(isPart(x, y-1, z, ReactorParts.LAPIS)&&isPart(x,y+1,z, ReactorParts.LAPIS)){
                            valid = true;
                        }
                        if(isPart(x, y, z-1, ReactorParts.LAPIS)&&isPart(x,y,z+1, ReactorParts.LAPIS)){
                            valid = true;
                        }
                        if(valid){
                            heat-=120;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Magnesium">
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[0].length; y++){
                for(int z = 0; z<parts[0][0].length; z++){
                    if(parts[x][y][z]==ReactorParts.MAGNESIUM){
                        if(getAdjacent(x,y,z,ReactorParts.MODERATOR)>=1&&getAdjacent(x,y,z,(ReactorParts)null)>=1){
                            heat-=110;
                            efficiency[x][y][z] = 1;
                        }
                    }
                }
            }
        }
        //</editor-fold>
        totalEfficiency = power/cells/fuel.power;
    }
    private ReactorParts getPart(int x, int y, int z){
        if(x<0||y<0||z<0)return null;
        if(x>=parts.length||y>=parts[0].length||z>=parts[0][0].length)return null;
        return parts[x][y][z];
    }
    private int getDistance(int x, int y, int z, ReactorParts part, int xDiff, int yDiff, int zDiff, ReactorParts... possiblePaths){
        int dist = 0;
        while(true){
            dist++;
            x+=xDiff;
            y+=yDiff;
            z+=zDiff;
            ReactorParts p = getPart(x, y, z);
            if(p==null)return Integer.MAX_VALUE;
            if(p==part)return dist;
            boolean yay = false;
            for(ReactorParts a : possiblePaths){
                if(p==a)yay = true;
            }
            if(possiblePaths.length>0&&!yay)return Integer.MAX_VALUE;
        }
    }
    private boolean isPart(int x, int y, int z, ReactorParts... valid) {
        try{
            if(efficiency[x][y][z]<1)return false;
        }catch(ArrayIndexOutOfBoundsException ex){}
        ReactorParts actual = getPart(x, y, z);
        for(ReactorParts part : valid){
            if(actual==part)return true;
        }
        return false;
    }
    private int getAdjacent(int x, int y, int z, ReactorParts... valid) {
        int adjacent = 0;
        if(isPart(x+1, y, z, valid)){
            adjacent++;
        }
        if(isPart(x, y+1, z, valid)){
            adjacent++;
        }
        if(isPart(x, y, z+1, valid)){
            adjacent++;
        }
        if(isPart(x-1, y, z, valid)){
            adjacent++;
        }
        if(isPart(x, y-1, z, valid)){
            adjacent++;
        }
        if(isPart(x, y, z-1, valid)){
            adjacent++;
        }
        return adjacent;
    }
    public String getConfiguration() {
        String config = "";
        for(int y = parts[0].length-1; y>=0; y--){
            for(int z = 0; z<parts[0][0].length; z++){
                for(int x = 0; x<parts.length; x++){
                    if(efficiency[x][y][z]==0){
                        config+=' ';
                    }else{
                        config+=parts[x][y][z].c;
                    }
                }
                config+="\n";
            }
            config+="\n";
        }
        return config;
    }
    public boolean isBetter(Reactor other) {
        if(other==null)return true;
        else{
            if(other.power<=0&&power>0)return true;
            else{
                if(other.heat>0&&heat<other.heat)return true;
                else{
                    if(isValid()){
                        for(Priority priority : priorities){
                            double better = priority.compare(this, other);
                            if(better>0)return true;
                            if(better<0)return false;
                        }
                    }
                }
            }
        }
        return false;
    }
    public String getDetails(){
        return "Power generation: "+power+"RF/t\n"
                + "Heat: "+(int)heat+" H/t\n"
                + "Efficiency: "+Math.round(totalEfficiency*1000)/10d+"%\n"
                + "Fuel cells: "+getFuelCells()+"\n"
                + "Configuration:\n"
                + ""+getConfiguration();
    }
    private boolean isValid(){
        return heat<=0&&power>0;
    }
    int getFuelCells(){
        int cells = 0;
        for(int x = 0; x<parts.length; x++){
            for(int y = 0; y<parts[x].length; y++){
                for(int z = 0; z<parts[x][y].length; z++){
                    if(parts[x][y][z]==ReactorParts.CELL){
                        cells++;
                    }
                }
            }
        }
        return cells;
    }
    double getFuelSpeed(){
        return getFuelCells()/totalEfficiency;
    }
}
