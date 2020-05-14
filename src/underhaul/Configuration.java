package underhaul;
import java.util.ArrayList;
import java.util.HashMap;
public abstract class Configuration{
    public static final Configuration DEFAULT = new Configuration(){
        @Override
        public void init(ArrayList<Fuel> fuels, HashMap<Cooler, Integer> cooling){
            fuels.add(new Fuel("TBU", 60, 18, 144000));
            fuels.add(new Fuel("TBU Oxide", 84, 22.5, 144000));
            fuels.add(new Fuel("LEU-233", 144, 60, 64000));
            fuels.add(new Fuel("LEU-233 Oxide",201.6,75.0, 64000));
            fuels.add(new Fuel("HEU-233",576.0,360.0, 64000));
            fuels.add(new Fuel("HEU-233 Oxide",806.4,450.0, 64000));
            fuels.add(new Fuel("LEU-235",120.0,50.0, 72000));
            fuels.add(new Fuel("LEU-235 Oxide",168.0,62.5, 72000));
            fuels.add(new Fuel("HEU-235",480.0,300.0, 72000));
            fuels.add(new Fuel("HEU-235 Oxide",672.0,375.0, 72000));
            fuels.add(new Fuel("LEN-236",90.0,36.0, 102000));
            fuels.add(new Fuel("LEN-236 Oxide",126.0,45.0, 102000));
            fuels.add(new Fuel("HEN-236",360.0,216.0, 102000));
            fuels.add(new Fuel("HEN-236 Oxide",504.0,270.0, 102000));
            fuels.add(new Fuel("LEP-239",105.0,40.0, 92000));
            fuels.add(new Fuel("LEP-239 Oxide",147.0,50.0, 92000));
            fuels.add(new Fuel("HEP-239",420.0,240.0, 92000));
            fuels.add(new Fuel("HEP-239 Oxide",588.0,300.0, 92000));
            fuels.add(new Fuel("LEP-241",165.0,70.0, 60000));
            fuels.add(new Fuel("LEP-241 Oxide",231.0,87.5, 60000));
            fuels.add(new Fuel("HEP-241",660.0,420.0, 60000));
            fuels.add(new Fuel("HEP-241 Oxide",924.0,525.0, 60000));
            fuels.add(new Fuel("MOX-239",155.4,57.5, 84000));
            fuels.add(new Fuel("MOX-241",243.6,97.5, 56000));
            fuels.add(new Fuel("LEA-242",192.0,94.0, 54000));
            fuels.add(new Fuel("LEA-242 Oxide",268.8,117.5, 54000));
            fuels.add(new Fuel("HEA-242",768.0,564.0,54000));
            fuels.add(new Fuel("HEA-242 Oxide",1_075.2,705.0, 54000));
            fuels.add(new Fuel("LECm-243",210.0,112.0, 52000));
            fuels.add(new Fuel("LECm-243 Oxide",294.0,140.0, 52000));
            fuels.add(new Fuel("HECm-243",840.0,672.0, 52000));
            fuels.add(new Fuel("HECm-243 Oxide",1_176.0,840.0, 52000));
            fuels.add(new Fuel("LECm-245",162.0,68.0, 68000));
            fuels.add(new Fuel("LECm-245 Oxide",226.8,85.0, 68000));
            fuels.add(new Fuel("HECm-245",648.0,408.0, 68000));
            fuels.add(new Fuel("HECm-245 Oxide",907.2,510.0, 68000));
            fuels.add(new Fuel("LECm-247",138.0,54.0, 78000));
            fuels.add(new Fuel("LECm-247 Oxide",193.2,67.5, 78000));
            fuels.add(new Fuel("HECm-247",552.0,324.0, 78000));
            fuels.add(new Fuel("HECm-247 Oxide",772.8,405.0, 78000));
            fuels.add(new Fuel("LEB-248",135.0,52.0, 86000));
            fuels.add(new Fuel("LEB-248 Oxide",189.0,65.0, 86000));
            fuels.add(new Fuel("HEB-248",540.0,312.0, 86000));
            fuels.add(new Fuel("HEB-248 Oxide",756.0,398.0, 86000));
            fuels.add(new Fuel("LECf-249",216.0,116.0, 60000));
            fuels.add(new Fuel("LECf-249 Oxide",302.4,145.0, 60000));
            fuels.add(new Fuel("HECf-249",864.0,696.0, 60000));
            fuels.add(new Fuel("HECf-249 Oxide",1_209.6,870.0, 60000));
            fuels.add(new Fuel("LECf-251",225.0,120.0, 58000));
            fuels.add(new Fuel("LECf-251 Oxide",315.0,150.0, 58000));
            fuels.add(new Fuel("HECf-251",900.0,720.0, 58000));
            fuels.add(new Fuel("HECf-251 Oxide",1_260.0,900.0, 58000));
        }
    };
    public static final Configuration E2E = new Configuration() {
        @Override
        public void init(ArrayList<Fuel> fuels, HashMap<Cooler, Integer> cooling){
            fuels.add(new Fuel("TBU", 60, 18, 144000));
            fuels.add(new Fuel("TBU Oxide", 84, 22.5, 144000));
            fuels.add(new Fuel("LEU-233", 144, 60, 64000));
            fuels.add(new Fuel("LEU-233 Oxide",201.6,75.0, 64000));
            fuels.add(new Fuel("HEU-233",576.0,360.0, 64000));
            fuels.add(new Fuel("HEU-233 Oxide",806.4,450.0, 64000));
            fuels.add(new Fuel("LEU-235",120.0,50.0, 72000));
            fuels.add(new Fuel("LEU-235 Oxide",168.0,62.5, 72000));
            fuels.add(new Fuel("HEU-235",480.0,300.0, 72000));
            fuels.add(new Fuel("HEU-235 Oxide",672.0,375.0, 72000));
            fuels.add(new Fuel("LEN-236",90.0,36.0, 102000));
            fuels.add(new Fuel("LEN-236 Oxide",126.0,45.0, 102000));
            fuels.add(new Fuel("HEN-236",360.0,216.0, 102000));
            fuels.add(new Fuel("HEN-236 Oxide",504.0,270.0, 102000));
            fuels.add(new Fuel("LEP-239",105.0,40.0, 92000));
            fuels.add(new Fuel("LEP-239 Oxide",147.0,50.0, 92000));
            fuels.add(new Fuel("HEP-239",420.0,240.0, 92000));
            fuels.add(new Fuel("HEP-239 Oxide",588.0,300.0, 92000));
            fuels.add(new Fuel("LEP-241",165.0,70.0, 60000));
            fuels.add(new Fuel("LEP-241 Oxide",231.0,87.5, 60000));
            fuels.add(new Fuel("HEP-241",660.0,420.0, 60000));
            fuels.add(new Fuel("HEP-241 Oxide",924.0,525.0, 60000));
            fuels.add(new Fuel("MOX-239",155.4,57.5, 84000));
            fuels.add(new Fuel("MOX-241",243.6,97.5, 56000));
            fuels.add(new Fuel("LEA-242",192.0,94.0, 54000));
            fuels.add(new Fuel("LEA-242 Oxide",268.8,117.5, 54000));
            fuels.add(new Fuel("HEA-242",768.0,564.0,54000));
            fuels.add(new Fuel("HEA-242 Oxide",1_075.2,705.0, 54000));
            fuels.add(new Fuel("LECm-243",210.0,112.0, 52000));
            fuels.add(new Fuel("LECm-243 Oxide",294.0,140.0, 52000));
            fuels.add(new Fuel("HECm-243",840.0,672.0, 52000));
            fuels.add(new Fuel("HECm-243 Oxide",1_176.0,840.0, 52000));
            fuels.add(new Fuel("LECm-245",162.0,68.0, 68000));
            fuels.add(new Fuel("LECm-245 Oxide",226.8,85.0, 68000));
            fuels.add(new Fuel("HECm-245",648.0,408.0, 68000));
            fuels.add(new Fuel("HECm-245 Oxide",907.2,510.0, 68000));
            fuels.add(new Fuel("LECm-247",138.0,54.0, 78000));
            fuels.add(new Fuel("LECm-247 Oxide",193.2,67.5, 78000));
            fuels.add(new Fuel("HECm-247",552.0,324.0, 78000));
            fuels.add(new Fuel("HECm-247 Oxide",772.8,405.0, 78000));
            fuels.add(new Fuel("LEB-248",135.0,52.0, 86000));
            fuels.add(new Fuel("LEB-248 Oxide",189.0,65.0, 86000));
            fuels.add(new Fuel("HEB-248",540.0,312.0, 86000));
            fuels.add(new Fuel("HEB-248 Oxide",756.0,398.0, 86000));
            fuels.add(new Fuel("LECf-249",216.0,116.0, 60000));
            fuels.add(new Fuel("LECf-249 Oxide",302.4,145.0, 60000));
            fuels.add(new Fuel("HECf-249",864.0,696.0, 60000));
            fuels.add(new Fuel("HECf-249 Oxide",1_209.6,870.0, 60000));
            fuels.add(new Fuel("LECf-251",225.0,120.0, 58000));
            fuels.add(new Fuel("LECf-251 Oxide",315.0,150.0, 58000));
            fuels.add(new Fuel("HECf-251",900.0,720.0, 58000));
            fuels.add(new Fuel("HECf-251 Oxide",1_260.0,900.0, 58000));
            powerMult(6);
            fuelRate(2);
            heatGen(1.2f);
            cooling.put(Cooler.COOLER_WATER, 20);
            cooling.put(Cooler.COOLER_REDSTONE, 80);
            cooling.put(Cooler.COOLER_QUARTZ, 80);
            cooling.put(Cooler.COOLER_GOLD, 120);
            cooling.put(Cooler.COOLER_GLOWSTONE, 120);
            cooling.put(Cooler.COOLER_LAPIS, 100);
            cooling.put(Cooler.COOLER_DIAMOND, 120);
            cooling.put(Cooler.COOLER_HELIUM, 120);
            cooling.put(Cooler.COOLER_ENDERIUM, 140);
            cooling.put(Cooler.COOLER_CRYOTHEUM, 140);
            cooling.put(Cooler.COOLER_IRON, 60);
            cooling.put(Cooler.COOLER_EMERALD, 140);
            cooling.put(Cooler.COOLER_COPPER, 60);
            cooling.put(Cooler.COOLER_TIN, 80);
            cooling.put(Cooler.COOLER_MAGNESIUM, 100);
        }
    };
    public static final Configuration PO3 = new Configuration() {
        @Override
        public void init(ArrayList<Fuel> fuels, HashMap<Cooler, Integer> cooling){
            fuels.add(new Fuel("TBU", 600, 18, 144000));
            fuels.add(new Fuel("TBU Oxide", 840, 22.5, 144000));
            fuels.add(new Fuel("LEU-233", 1440, 60, 64000));
            fuels.add(new Fuel("LEU-233 Oxide",2010.6,75.0, 64000));
            fuels.add(new Fuel("HEU-233",5760.0,360.0, 64000));
            fuels.add(new Fuel("HEU-233 Oxide",8060.4,450.0, 64000));
            fuels.add(new Fuel("LEU-235",1200.0,50.0, 72000));
            fuels.add(new Fuel("LEU-235 Oxide",1680.0,62.5, 72000));
            fuels.add(new Fuel("HEU-235",4800.0,300.0, 72000));
            fuels.add(new Fuel("HEU-235 Oxide",6720.0,375.0, 72000));
            fuels.add(new Fuel("LEN-236",900.0,36.0, 102000));
            fuels.add(new Fuel("LEN-236 Oxide",1260.0,45.0, 102000));
            fuels.add(new Fuel("HEN-236",3600.0,216.0, 102000));
            fuels.add(new Fuel("HEN-236 Oxide",5040.0,270.0, 102000));
            fuels.add(new Fuel("LEP-239",1050.0,40.0, 92000));
            fuels.add(new Fuel("LEP-239 Oxide",1470.0,50.0, 92000));
            fuels.add(new Fuel("HEP-239",4200.0,240.0, 92000));
            fuels.add(new Fuel("HEP-239 Oxide",5880.0,300.0, 92000));
            fuels.add(new Fuel("LEP-241",1650.0,70.0, 60000));
            fuels.add(new Fuel("LEP-241 Oxide",2310.0,87.5, 60000));
            fuels.add(new Fuel("HEP-241",6600.0,420.0, 60000));
            fuels.add(new Fuel("HEP-241 Oxide",9240.0,525.0, 60000));
            fuels.add(new Fuel("MOX-239",1550.4,57.5, 84000));
            fuels.add(new Fuel("MOX-241",2430.6,97.5, 56000));
            fuels.add(new Fuel("LEA-242",1920.0,94.0, 54000));
            fuels.add(new Fuel("LEA-242 Oxide",2680.8,117.5, 54000));
            fuels.add(new Fuel("HEA-242",7680.0,564.0,54000));
            fuels.add(new Fuel("HEA-242 Oxide",1_0750.2,705.0, 54000));
            fuels.add(new Fuel("LECm-243",2100.0,112.0, 52000));
            fuels.add(new Fuel("LECm-243 Oxide",2940.0,140.0, 52000));
            fuels.add(new Fuel("HECm-243",8400.0,672.0, 52000));
            fuels.add(new Fuel("HECm-243 Oxide",1_1760.0,840.0, 52000));
            fuels.add(new Fuel("LECm-245",1620.0,68.0, 68000));
            fuels.add(new Fuel("LECm-245 Oxide",2260.8,85.0, 68000));
            fuels.add(new Fuel("HECm-245",6480.0,408.0, 68000));
            fuels.add(new Fuel("HECm-245 Oxide",9070.2,510.0, 68000));
            fuels.add(new Fuel("LECm-247",1380.0,54.0, 78000));
            fuels.add(new Fuel("LECm-247 Oxide",1930.2,67.5, 78000));
            fuels.add(new Fuel("HECm-247",5520.0,324.0, 78000));
            fuels.add(new Fuel("HECm-247 Oxide",7720.8,405.0, 78000));
            fuels.add(new Fuel("LEB-248",1350.0,52.0, 86000));
            fuels.add(new Fuel("LEB-248 Oxide",1890.0,65.0, 86000));
            fuels.add(new Fuel("HEB-248",5400.0,312.0, 86000));
            fuels.add(new Fuel("HEB-248 Oxide",7560.0,398.0, 86000));
            fuels.add(new Fuel("LECf-249",2160.0,116.0, 60000));
            fuels.add(new Fuel("LECf-249 Oxide",3020.4,145.0, 60000));
            fuels.add(new Fuel("HECf-249",8640.0,696.0, 60000));
            fuels.add(new Fuel("HECf-249 Oxide",1_2090.6,870.0, 60000));
            fuels.add(new Fuel("LECf-251",2250.0,120.0, 58000));
            fuels.add(new Fuel("LECf-251 Oxide",3150.0,150.0, 58000));
            fuels.add(new Fuel("HECf-251",9000.0,720.0, 58000));
            fuels.add(new Fuel("HECf-251 Oxide",1_2600.0,900.0, 58000));
            powerMult(30);
            fuelRate(4);
            heatGen(4);
            cooling.put(Cooler.COOLER_WATER, 40);
            cooling.put(Cooler.COOLER_REDSTONE, 160);
            cooling.put(Cooler.COOLER_QUARTZ, 160);
            cooling.put(Cooler.COOLER_GOLD, 240);
            cooling.put(Cooler.COOLER_GLOWSTONE, 240);
            cooling.put(Cooler.COOLER_LAPIS, 200);
            cooling.put(Cooler.COOLER_DIAMOND, 240);
            cooling.put(Cooler.COOLER_HELIUM, 240);
            cooling.put(Cooler.COOLER_ENDERIUM, 280);
            cooling.put(Cooler.COOLER_CRYOTHEUM, 800);
            cooling.put(Cooler.COOLER_IRON, 120);
            cooling.put(Cooler.COOLER_EMERALD, 280);
            cooling.put(Cooler.COOLER_COPPER, 120);
            cooling.put(Cooler.COOLER_TIN, 160);
            cooling.put(Cooler.COOLER_MAGNESIUM, 200);
        }
    };
    public static final Configuration CUSTOM = new Configuration(){
        @Override
        public void init(ArrayList<Fuel> fuels, HashMap<Cooler, Integer> cooling){
            DEFAULT.init(fuels, cooling);
        }
    };
    public ArrayList<Fuel> fuels = new ArrayList<>();
    public HashMap<Cooler, Integer> cooling = new HashMap<>();
    public static boolean load(Configuration config){
        if(Main.running)return false;
        Fuel.fuels.clear();
        Fuel.fuels.addAll(config.fuels);
        for(Cooler h : config.cooling.keySet()){
            h.cooling = config.cooling.get(h);
        }
        if(Main.instance!=null){
            Main.instance.boxFuel.setModel(Main.instance.getFuels());
        }
        return true;
    }
    public Configuration(){
        cooling.put(ReactorPart.COOLER_WATER, 60);
        cooling.put(ReactorPart.COOLER_REDSTONE, 90);
        cooling.put(ReactorPart.COOLER_QUARTZ, 90);
        cooling.put(ReactorPart.COOLER_GOLD, 120);
        cooling.put(ReactorPart.COOLER_GLOWSTONE, 130);
        cooling.put(ReactorPart.COOLER_LAPIS, 120);
        cooling.put(ReactorPart.COOLER_DIAMOND, 150);
        cooling.put(ReactorPart.COOLER_HELIUM, 140);
        cooling.put(ReactorPart.COOLER_ENDERIUM, 120);
        cooling.put(ReactorPart.COOLER_CRYOTHEUM, 160);
        cooling.put(ReactorPart.COOLER_IRON, 80);
        cooling.put(ReactorPart.COOLER_EMERALD, 160);
        cooling.put(ReactorPart.COOLER_COPPER, 80);
        cooling.put(ReactorPart.COOLER_TIN, 120);
        cooling.put(ReactorPart.COOLER_MAGNESIUM, 110);
        init(fuels, cooling);
        for(Fuel f : fuels){
            f.power = Math.round(f.power*1000)/1000d;
            f.heat = Math.round(f.heat*1000)/1000d;
        }
    }
    public abstract void init(ArrayList<Fuel> fuels, HashMap<Cooler, Integer> cooling);
    protected void powerMult(float mult){
        for(Fuel f : fuels){
            f.power*=mult;
        }
    }
    protected void fuelRate(float mult){
        for(Fuel f : fuels){
            f.time/=mult;
        }
    }
    protected void heatGen(float mult){
        for(Fuel f : fuels){
            f.heat*=mult;
        }
    }
}
