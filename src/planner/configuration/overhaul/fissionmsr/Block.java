package planner.configuration.overhaul.fissionmsr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import planner.Core;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class Block extends RuleContainer{
    public static Block heater(String name, int cooling, String input, String texture, PlacementRule... rules){
        return heater(name, cooling, input, "Hot "+input, texture, rules);
    }
    public static Block heater(String name, int cooling, String input, String output, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.cooling = cooling;
        block.input = input;
        block.output = output;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.setTexture(Core.getImage(texture));
        block.functional = true;
        block.cluster = true;
        block.moderator = true;
        return block;
    }
    public static Block moderator(String name, String texture, int flux, float efficiency){
        Block block = new Block(name);
        block.moderator = true;
        block.activeModerator = true;
        block.flux = flux;
        block.efficiency = efficiency;
        block.setTexture(Core.getImage(texture));
        block.functional = true;
        return block;
    }
    public static Block reflector(String name, String texture, float efficiency, float reflectivity){
        Block block = new Block(name);
        block.reflector = true;
        block.efficiency = efficiency;
        block.reflectivity = reflectivity;
        block.functional = true;
        block.blocksLOS = true;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block shield(String name, String texture, int heatPerFlux, float efficiency){
        Block block = new Block(name);
        block.shield = true;
        block.moderator = true;
        block.activeModerator = true;
        block.functional = true;
        block.cluster = true;
        block.createCluster = true;
        block.heatMult = heatPerFlux;
        block.efficiency = efficiency;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block vessel(String name, String texture){
        Block block = new Block(name);
        block.fuelVessel = true;
        block.cluster = true;
        block.createCluster = true;
        block.blocksLOS = true;
        block.functional = true;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block irradiator(String name, String texture){
        Block block = new Block(name);
        block.cluster = true;
        block.createCluster = true;
        block.irradiator = true;
        block.functional = true;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block conductor(String name, String texture){
        Block block = new Block(name);
        block.cluster = true;//because conductors connect clusters together
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public String name;
    public int cooling = 0;
    public String input;
    public String output;
    public boolean cluster = false;
    public boolean createCluster = false;
    public boolean conductor = false;
    public boolean fuelVessel = false;
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
    public BufferedImage texture;
    public BufferedImage displayTexture;
    public Block(String name){
        this.name = name;
    }
    public Config save(FissionMSRConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(cooling!=0)config.set("cooling", cooling);
        if(input!=null)config.set("input", input);
        if(output!=null)config.set("output", output);
        if(cluster)config.set("cluster", cluster);
        if(createCluster)config.set("createCluster", createCluster);
        if(conductor)config.set("conductor", conductor);
        if(fuelVessel)config.set("fuelVessel", fuelVessel);
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
                ruls.add(rule.save(configuration));
            }
            config.set("rules", ruls);
        }
        if(texture!=null&&!partial){
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
    public void setTexture(BufferedImage image){
        texture = image;
        BufferedImage displayImg = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y));
                displayImg.setRGB(x, y, new Color(Core.img_convert(col.getRed()), Core.img_convert(col.getGreen()), Core.img_convert(col.getBlue()), col.getAlpha()).getRGB());
            }
        }
        displayTexture = displayImg;
    }
}