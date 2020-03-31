package overhaul;
public class Moderator extends ReactorPart{
    public final int fluxFactor;
    public final double efficiencyFactor;
    public Moderator(String name, int fluxFactor, String jsonName, double efficiencyFactor){
        super(Type.MODERATOR, name+" Moderator", jsonName, "moderator/"+name.replace(" ", "_").toLowerCase());
        this.fluxFactor = fluxFactor;
        this.efficiencyFactor = efficiencyFactor;
    }
}