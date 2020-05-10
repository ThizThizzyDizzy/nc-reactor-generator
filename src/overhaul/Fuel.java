package overhaul;
import java.util.ArrayList;
import java.util.HashMap;
public class Fuel{
    public static final ArrayList<Fuel> fuels = new ArrayList<>();
    private final String name;
    public HashMap<Type, Number> efficiency = new HashMap<>();
    public HashMap<Type, Number> heat = new HashMap<>();
    public HashMap<Type, Number> criticality = new HashMap<>();
    //I hope I don't end up needing the time and regretting this setup
    /**
     * Creates a new Fuel<br>
     * Stats are efficiency multiplier, heat, and criticality, in order of fuel type.<br>
     * oxEfficiency, oxHeat, oxCriticality, niEfficiency, niHeat, niCriticality, etc.<br>
     * @param name the name of the fuel
     * @param stats The fuel's stats
     */
    public Fuel(String name, Number... stats){
        this.name = name;
        if(stats.length!=3*Type.values().length)throw new IllegalArgumentException("Incorrect number of stats for "+name+"! ("+stats.length+"!="+3*Type.values().length);
        for(int t = 0; t<Type.values().length; t++){
            Type type = Type.values()[t];
            efficiency.put(type, stats[t*3]);
            heat.put(type, stats[t*3+1]);
            criticality.put(type, stats[t*3+2]);
        }
    }
    @Override
    public String toString(){
        return name;
    }
    public static enum Type{
        OX(" Oxide", false),
        NI(" Nitride", false),
        ZA("-Zirconium Alloy", false),
        F4(" Fluoride)", true);
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
    }
}