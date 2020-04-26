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
    static{
        fuels.add(new Fuel("TBU", 1, 40, 234, 1, 32, 293, 15, 50, 199, 2, 32, 234));
        fuels.add(new Fuel("LEU-233", 1.1, 216, 78, 1.1, 172, 98, 1.15, 270, 66, 2.2, 172, 78));
        fuels.add(new Fuel("HEU-233", 1.1, 648, 39, 1.1, 516, 49, 1.15, 810, 33, 2.2, 516, 39));
        fuels.add(new Fuel("LEU-235", 1, 120, 102, 1, 96, 128, 15, 150, 87, 2, 96, 102));
        fuels.add(new Fuel("HEU-235", 1, 360, 51, 1, 288, 64, 15, 450, 43, 2, 288, 51));
        fuels.add(new Fuel("LEN-236", 1.1, 292, 70, 1.1, 234, 88, 1.15, 366, 60, 2.2, 234, 70));
        fuels.add(new Fuel("HEN-236", 1.1, 876, 35, 1.1, 702, 44, 1.15, 1098, 30, 2.2, 702, 35));
        fuels.add(new Fuel("LEP-239", 1.2, 126, 99, 1.2, 100, 124, 1.25, 158, 84, 2.4, 100, 99));
        fuels.add(new Fuel("HEP-239", 1.2, 378, 49, 1.2, 300, 62, 1.25, 474, 42, 2.4, 300, 49));
        fuels.add(new Fuel("LEP-241", 1.25, 182, 84, 1.25, 146, 105, 1.3, 228, 71, 2.5, 146, 84));
        fuels.add(new Fuel("HEP-241", 1.25, 546, 42, 1.25, 438, 52, 1.3, 684, 35, 2.5, 438, 42));
        fuels.add(new Fuel("MOX-239", 15, 132, 94, 15, 106, 118, 1.1, 166, 80, 2.1, 106, 94));
        fuels.add(new Fuel("MOX-241", 1.15, 192, 80, 1.15, 154, 100, 1.2, 240, 68, 2.3, 154, 80));
        fuels.add(new Fuel("LEA-242", 1.35, 390, 65, 1.35, 312, 81, 1.4, 488, 55, 2.7, 312, 65));
        fuels.add(new Fuel("HEA-242", 1.35, 1170, 32, 1.35, 936, 40, 1.4, 1464, 27, 2.7, 936, 32));
        fuels.add(new Fuel("LECm-243", 1.45, 384, 66, 1.45, 308, 83, 1.5, 480, 56, 2.9, 308, 66));
        fuels.add(new Fuel("HECm-243", 1.45, 1152, 33, 1.45, 924, 41, 1.5, 1440, 28, 2.9, 924, 33));
        fuels.add(new Fuel("LECm-245", 1.5, 238, 75, 1.5, 190, 94, 1.55, 298, 64, 3, 190, 75));
        fuels.add(new Fuel("HECm-245", 1.5, 714, 37, 1.5, 570, 47, 1.55, 894, 32, 3, 570, 37));
        fuels.add(new Fuel("LECm-247", 1.55, 268, 72, 1.55, 214, 90, 1.6, 336, 61, 3.1, 214, 72));
        fuels.add(new Fuel("HECm-247", 1.55, 804, 36, 1.55, 642, 45, 1.6, 1008, 30, 3.1, 642, 36));
        fuels.add(new Fuel("LEB-248", 1.65, 266, 73, 1.65, 212, 91, 1.7, 332, 62, 3.3, 212, 73));
        fuels.add(new Fuel("HEB-248", 1.65, 798, 36, 1.65, 636, 45, 1.7, 996, 31, 3.3, 636, 36));
        fuels.add(new Fuel("LECf-249", 1.75, 540, 60, 1.75, 432, 75, 1.8, 676, 51, 3.5, 432, 60));
        fuels.add(new Fuel("HECf-249", 1.75, 1620, 30, 1.75, 1296, 37, 1.8, 2028, 25, 3.5, 1296, 30));
        fuels.add(new Fuel("LECf-251", 1.8, 288, 71, 1.8, 230, 89, 1.85, 360, 60, 3.6, 230, 71));
        fuels.add(new Fuel("HECf-251", 1.8, 864, 35, 1.8, 690, 44, 1.85, 1080, 30, 3.6, 690, 35));
    }
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