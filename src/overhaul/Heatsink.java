package overhaul;
public class Heatsink extends ReactorPart{
    public int cooling;
    public final PlacementRule[] rules;
    public Heatsink(String name, String jsonName, PlacementRule... rules){
        super(Type.HEATSINK, name+" Heatsink", jsonName, "heatsink/"+name.toLowerCase().replace(" ", "_"));
        this.rules = rules;
    }
}