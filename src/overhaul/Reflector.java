package overhaul;
public class Reflector extends ReactorPart{
    public final double reflectivity;
    public final double efficiency;
    public Reflector(String name, double reflectivity, String jsonName, double efficiency){
        super(Type.REFLECTOR, name+" Reflector", jsonName, "reflector\\"+name.toLowerCase().replace(" ", "_"));
        this.reflectivity = reflectivity;
        this.efficiency = efficiency;
    }
}