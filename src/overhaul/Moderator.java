package overhaul;
public class Moderator extends ReactorPart{
    public int fluxFactor;
    public double efficiencyFactor;
    public Moderator(String name, String jsonName){
        super(Type.MODERATOR, name+" Moderator", jsonName, "moderator/"+name.replace(" ", "_").toLowerCase());
    }
}