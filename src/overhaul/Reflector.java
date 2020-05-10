package overhaul;
public class Reflector extends ReactorPart{
    public float reflectivity;
    public float efficiency;
    public Reflector(String name, String jsonName){
        super(Type.REFLECTOR, name+" Reflector", jsonName, "reflector\\"+name.toLowerCase().replace(" ", "_"));
    }
}