package planner.configuration.underhaul.fissionsfr;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.imageio.ImageIO;
import planner.Core;
import planner.Main;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class Block extends RuleContainer{
    public static Block cooler(String name, int cooling, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.cooling = cooling;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.texture = Core.getImage(texture);
        return block;
    }
    public static Block activeCooler(String name, int cooling, String liquid, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.cooling = cooling;
        block.active = liquid;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.texture = Core.getImage(texture);
        return block;
    }
    public static Block fuelCell(String name, String texture){
        Block block = new Block(name);
        block.fuelCell = true;
        block.texture = Core.getImage(texture);
        return block;
    }
    public static Block moderator(String name, String texture){
        Block block = new Block(name);
        block.moderator = true;
        block.texture = Core.getImage(texture);
        return block;
    }
    public String name;
    public int cooling = 0;
    public boolean fuelCell = false;
    public boolean moderator = false;
    public String active;
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
        if(active!=null)config.set("active", active);
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