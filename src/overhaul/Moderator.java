package overhaul;
public class Moderator extends ReactorPart{
    public int fluxFactor;
    public double efficiencyFactor;
    public Moderator(String name, String jsonName, String texture){
        super(Type.MODERATOR, name, jsonName, texture);
    }
    public Moderator(String name, String jsonName){
        this(name+" Moderator", jsonName, "moderator/"+name.replace(" ", "_").toLowerCase());
    }
    public boolean canBeActive(boolean msr){
        return true;
    }
}