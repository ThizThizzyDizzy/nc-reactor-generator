package planner.configuration.overhaul.fissionsfr;
public class Fuel{
    public String name;
    public float efficiency;
    public float heat;
    public int time;
    public int criticality;
    public boolean selfPriming;
    public Fuel(String name, float efficiency, float heat, int time, int criticality, boolean selfPriming){
        this.name = name;
        this.efficiency = efficiency;
        this.heat = heat;
        this.time = time;
        this.criticality = criticality;
        this.selfPriming = selfPriming;
    }
}