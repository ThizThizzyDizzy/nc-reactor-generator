package pre_overhaul;
public class Cooler extends ReactorPart{
    public int cooling;
    public final PlacementRule[] rules;
    public Cooler(String name, String jsonName, PlacementRule... rules){
        super(Type.COOLER, name+" Cooler", jsonName, name.toLowerCase().replace(" ", "_"));
        this.rules = rules;
    }
}