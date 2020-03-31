package pre_overhaul;
import java.util.ArrayList;
public class Fuel{
    public static final ArrayList<Fuel> fuels = new ArrayList<>();
    static{
        fuel("TBU", 60, 18, 144000);
        fuel("TBU Oxide", 84, 22.5, 144000);
        fuel("LEU-233", 144, 60, 64000);
        fuel("LEU-233 Oxide",201.6,75.0, 64000);
        fuel("HEU-233",576.0,360.0, 64000);
        fuel("HEU-233 Oxide",806.4,450.0, 64000);
        fuel("LEU-235",120.0,50.0, 72000);
        fuel("LEU-235 Oxide",168.0,62.5, 72000);
        fuel("HEU-235",480.0,300.0, 72000);
        fuel("HEU-235 Oxide",672.0,375.0, 72000);
        fuel("LEN-236",90.0,36.0, 102000);
        fuel("LEN-236 Oxide",126.0,45.0, 102000);
        fuel("HEN-236",360.0,216.0, 102000);
        fuel("HEN-236 Oxide",504.0,270.0, 102000);
        fuel("LEP-239",105.0,40.0, 92000);
        fuel("LEP-239 Oxide",147.0,50.0, 92000);
        fuel("HEP-239",420.0,240.0, 92000);
        fuel("HEP-239 Oxide",588.0,300.0, 92000);
        fuel("LEP-241",165.0,70.0, 60000);
        fuel("LEP-241 Oxide",231.0,87.5, 60000);
        fuel("HEP-241",660.0,420.0, 60000);
        fuel("HEP-241 Oxide",924.0,525.0, 60000);
        fuel("MOX-239",155.4,57.5, 84000);
        fuel("MOX-241",243.6,97.5, 56000);
        fuel("LEA-242",192.0,94.0, 54000);
        fuel("LEA-242 Oxide",268.8,117.5, 54000);
        fuel("HEA-242",768.0,564.0,54000);
        fuel("HEA-242 Oxide",1_075.2,705.0, 54000);
        fuel("LECm-243",210.0,112.0, 52000);
        fuel("LECm-243 Oxide",294.0,140.0, 52000);
        fuel("HECm-243",840.0,672.0, 52000);
        fuel("HECm-243 Oxide",1_176.0,840.0, 52000);
        fuel("LECm-245",162.0,68.0, 68000);
        fuel("LECm-245 Oxide",226.8,85.0, 68000);
        fuel("HECm-245",648.0,408.0, 68000);
        fuel("HECm-245 Oxide",907.2,510.0, 68000);
        fuel("LECm-247",138.0,54.0, 78000);
        fuel("LECm-247 Oxide",193.2,67.5, 78000);
        fuel("HECm-247",552.0,324.0, 78000);
        fuel("HECm-247 Oxide",772.8,405.0, 78000);
        fuel("LEB-248",135.0,52.0, 86000);
        fuel("LEB-248 Oxide",189.0,65.0, 86000);
        fuel("HEB-248",540.0,312.0, 86000);
        fuel("HEB-248 Oxide",756.0,398.0, 86000);
        fuel("LECf-249",216.0,116.0, 60000);
        fuel("LECf-249 Oxide",302.4,145.0, 60000);
        fuel("HECf-249",864.0,696.0, 60000);
        fuel("HECf-249 Oxide",1_209.6,870.0, 60000);
        fuel("LECf-251",225.0,120.0, 58000);
        fuel("LECf-251 Oxide",315.0,150.0, 58000);
        fuel("HECf-251",900.0,720.0, 58000);
        fuel("HECf-251 Oxide",1_260.0,900.0, 58000);
    }
    private static void fuel(String name, double power, double heat, int time){
        fuels.add(new Fuel(name, power, heat, time));
    }
    private final String name;
    public final double power;
    public final double heat;
    public final int time;//the planner wants the fuel time
    public Fuel(String name, double power, double heat, int time){
        this.name = name;
        this.power = power;
        this.heat = heat;
        this.time = time;
    }
    @Override
    public String toString(){
        return name;
    }
}