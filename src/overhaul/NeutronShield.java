package overhaul;
public class NeutronShield extends Moderator{
    public int heatMult;
    public NeutronShield(String name, String jsonName){
        super(name+" Neutron Shield", jsonName, "shield/"+name.replace(" ", "_").toLowerCase());
    }
    @Override
    public boolean canBeActive(boolean msr){
        return msr;
    }
}