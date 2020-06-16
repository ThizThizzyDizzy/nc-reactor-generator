package planner.configuration.overhaul.fissionsfr;
public class Block extends RuleContainer{
    public String name;
    public int cooling = 0;
    public boolean cluster = false;
    public boolean createCluster = false;
    public boolean conductor = false;
    public boolean fuelCell = false;
    public boolean reflector = false;
    public boolean irradiator = false;
    public boolean moderator = false;
    public boolean activeModerator = false;
    public boolean shield = false;
    public int flux;
    public float efficiency;
    public float reflectivity;
    public int heatMult;
    public boolean blocksLOS = false;
    public boolean functional;
    public Block(String name){
        this.name = name;
    }
}