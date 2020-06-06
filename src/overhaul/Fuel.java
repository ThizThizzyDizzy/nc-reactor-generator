package overhaul;
import java.util.ArrayList;
import java.util.HashMap;
public class Fuel{
    public static final ArrayList<Fuel> fuels = new ArrayList<>();
    public static Fuel parse(String str){
        if(str.startsWith("[")&&str.contains("]"))str = str.substring(str.indexOf("]")+1);
        for(Fuel f : fuels){
            if(f.name.equalsIgnoreCase(str))return f;
        }
        return null;
    }
    private final String name;
    public HashMap<Type, Number> efficiency = new HashMap<>();
    public HashMap<Type, Number> heat = new HashMap<>();
    public HashMap<Type, Number> criticality = new HashMap<>();
    public HashMap<Type, Number> selfPriming = new HashMap<>();
    private static final int numStats = 4;
    //I hope I don't end up needing the time and regretting this setup
    /**
     * Creates a new Fuel<br>
     * Stats are efficiency multiplier, heat, criticality, and self-priming (efficiency mult AKA 1/0) in order of fuel type.<br>
     * oxEfficiency, oxHeat, oxCriticality, oxPrime, niEfficiency, niHeat, etc.<br>
     * @param name the name of the fuel
     * @param stats The fuel's stats
     */
    public Fuel(String name, Number... stats){
        this.name = name;
        if(stats.length!=4*Type.values().length)throw new IllegalArgumentException("Incorrect number of stats for "+name+"! ("+stats.length+"!="+4*Type.values().length);
        for(int t = 0; t<Type.values().length; t++){
            Type type = Type.values()[t];
            efficiency.put(type, stats[t*4]);
            heat.put(type, stats[t*4+1]);
            criticality.put(type, stats[t*4+2]);
            selfPriming.put(type, stats[t*4+3]);
        }
    }
    @Override
    public String toString(){
        return name;
    }
    public Fuel getSelfPrimingFuel(){
        Number[] stats = new Number[numStats*Type.values().length];
        int i = 0;
        for(Type t : Type.values()){
            stats[i] = efficiency.get(t);
            i++;
            stats[i] = heat.get(t);
            i++;
            stats[i] = criticality.get(t);
            i++;
            stats[i] = 1;
            i++;
        }
        return new Fuel(name, stats);
    }
    public static enum Type{
        OX(" Oxide", false),
        NI(" Nitride", false),
        ZA("-Zirconium Alloy", false),
        F4(" Fluoride", true);
        private final boolean msr;
        private final String name;
        private Type(String name, boolean msr){
            this.name = name;
            this.msr = msr;
        }
        @Override
        public String toString(){
            return name;
        }
        boolean isMSR(){
            return msr;
        }
        public static Type parse(String str){
            if(str.startsWith("[")&&str.contains("]"))str = str.substring(1, str.indexOf("]"));
            for(Type t : values()){
                if(t.name().equalsIgnoreCase(str))return t;
            }
            return null;
        }
    }
    public static class Group{
        public static Group test(Fuel fuel, Type type, Fuel.Group backup){
            if(fuel==null||type==null)return backup;
            return new Group(fuel, type);
        }
        public final Fuel fuel;
        public final Fuel.Type type;
        public Group(Fuel fuel, Fuel.Type type){
            if(fuel==null||type==null)throw new IllegalArgumentException("Invalid fuel");
            this.fuel = fuel;
            this.type = type;
        }
        @Override
        public String toString(){
            return fuel.toString()+" "+type.toString();
        }
    }
}