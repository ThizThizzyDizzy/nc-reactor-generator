package multiblock.configuration.underhaul.fissionsfr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import planner.Core;
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
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block activeCooler(String name, int cooling, String liquid, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.cooling = cooling;
        block.active = liquid;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block fuelCell(String name, String texture){
        Block block = new Block(name);
        block.fuelCell = true;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public static Block moderator(String name, String texture){
        Block block = new Block(name);
        block.moderator = true;
        block.setTexture(Core.getImage(texture));
        return block;
    }
    public String name;
    public int cooling = 0;
    public boolean fuelCell = false;
    public boolean moderator = false;
    public String active;
    public BufferedImage texture;
    public BufferedImage displayTexture;
    public Block(String name){
        this.name = name;
    }
    public Config save(FissionSFRConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(cooling!=0)config.set("cooling", cooling);
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(PlacementRule rule : rules){
                ruls.add(rule.save(configuration));
            }
            config.set("rules", ruls);
        }
        if(active!=null)config.set("active", active);
        if(fuelCell)config.set("fuelCell", true);
        if(moderator)config.set("moderator", true);
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
        int left = Math.max(0,image.getWidth()/16-1);
        int right = Math.min(image.getWidth()*15/16, image.getWidth()-1);
        int up = Math.max(0,image.getHeight()/16-1);
        int down = Math.min(image.getHeight()*15/16, image.getHeight()-1);
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y));
                if(active!=null){
                    if(x<=left||y<=up||x>=right||y>=down){
                        col = new Color(144, 238, 144);
                    }
                }
                displayImg.setRGB(x, y, new Color(Core.img_convert(col.getRed()), Core.img_convert(col.getGreen()), Core.img_convert(col.getBlue()), col.getAlpha()).getRGB());
            }
        }
        displayTexture = displayImg;
    }
}