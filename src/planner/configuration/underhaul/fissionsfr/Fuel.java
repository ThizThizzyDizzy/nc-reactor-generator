package planner.configuration.underhaul.fissionsfr;
public class Fuel{
    public String name;
    public float power;
    public float heat;
    public int time;
    public Fuel(String name, float power, float heat, int time){
        this.name = name;
        this.power = power;
        this.heat = heat;
        this.time = time;
    }
}