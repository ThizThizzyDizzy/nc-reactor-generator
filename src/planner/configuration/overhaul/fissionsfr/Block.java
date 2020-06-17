package planner.configuration.overhaul.fissionsfr;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
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
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        if(cooling!=0)config.set("cooling", cooling);
        if(cluster)config.set("cluster", cluster);
        if(createCluster)config.set("createCluster", createCluster);
        if(conductor)config.set("conductor", conductor);
        if(fuelCell)config.set("fuelCell", fuelCell);
        if(reflector)config.set("reflector", reflector);
        if(irradiator)config.set("irradiator", irradiator);
        if(moderator)config.set("moderator", moderator);
        if(activeModerator)config.set("activeModerator", activeModerator);
        if(shield)config.set("shield", shield);
        if(moderator||shield)config.set("flux", flux);
        if(moderator||shield||reflector)config.set("efficiency", efficiency);
        if(reflector)config.set("reflectivity", reflectivity);
        if(shield)config.set("heatMult", heatMult);
        if(blocksLOS)config.set("blocksLOS", true);
        config.set("functional", functional);
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(PlacementRule rule : rules){
                ruls.add(rule.save());
            }
            config.set("rules", ruls);
        }
        return config;
    }
}