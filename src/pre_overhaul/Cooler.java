package pre_overhaul;
public class Cooler extends ReactorPart{
    public final int cooling;
    public final PlacementRule[] rules;
    public Cooler(String name, String jsonName, int cooling, PlacementRule... rules){
        super(Type.COOLER, name+" Heatsink", jsonName, "heatsink/"+name.toLowerCase().replace(" ", "_"));
        this.cooling = cooling;
        this.rules = rules;
    }
}