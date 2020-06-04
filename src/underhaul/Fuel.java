package underhaul;
import java.util.ArrayList;
public class Fuel{
    public static final ArrayList<Fuel> fuels = new ArrayList<>();
    public static Fuel get(String name){
        for(Fuel f : fuels){
            if(f.name.equalsIgnoreCase(name)||f.toString().equalsIgnoreCase(name))return f;
        }
        return null;
    }
    private final String name;
    public double power;
    public double heat;
    public int time;//the planner wants the fuel time
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