package overhaul;
public class Heatsink extends ReactorPart{
    public final int cooling;
    public final PlacementRule[] rules;
    public Heatsink(String name, String jsonName, int cooling, PlacementRule... rules){
        super(Type.HEATSINK, name+" Heatsink", jsonName, "heatsink/"+name.toLowerCase().replace(" ", "_"));
        this.cooling = cooling;
        this.rules = rules;
    }
}