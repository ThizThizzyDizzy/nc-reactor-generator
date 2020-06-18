package planner.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class Block extends RuleContainer{
    public String name;
    public int cooling = 0;
    public boolean fuelCell = false;
    public boolean moderator = false;
    public BufferedImage texture;
    public Block(String name){
        this.name = name;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        if(cooling!=0)config.set("cooling", cooling);
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(PlacementRule rule : rules){
                ruls.add(rule.save());
            }
            config.set("rules", ruls);
        }
        if(fuelCell)config.set("fuelCell", true);
        if(moderator)config.set("moderator", true);
        if(texture!=null){
            ConfigNumberList tex = new ConfigNumberList();
            tex.add(texture.getWidth());
            for(int x = 0; x<texture.getWidth(); x++){
                for(int y = 0; y<texture.getHeight(); y++){
                    tex.add(texture.getRGB(x, y));
                }
            }
            config.set("texture", tex);
        }
        return config;
    }
}