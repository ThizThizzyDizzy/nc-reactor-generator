package multiblock.configuration.underhaul.fissionsfr;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Objects;
import multiblock.configuration.Configuration;
import multiblock.configuration.TextureManager;
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
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block activeCooler(String name, int cooling, String liquid, String texture, PlacementRule... rules){
        Block block = new Block(name);
        block.cooling = cooling;
        block.active = liquid;
        for(PlacementRule r : rules){
            block.rules.add(r);
        }
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block fuelCell(String name, String texture){
        Block block = new Block(name);
        block.fuelCell = true;
        block.setTexture(TextureManager.getImage(texture));
        return block;
    }
    public static Block moderator(String name, String texture){
        Block block = new Block(name);
        block.moderator = true;
        block.setTexture(TextureManager.getImage(texture));
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
    public Config save(Configuration parent, FissionSFRConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(cooling!=0)config.set("cooling", cooling);
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(PlacementRule rule : rules){
                ruls.add(rule.save(parent, configuration));
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
                displayImg.setRGB(x, y, new Color(TextureManager.convert(col.getRed()), TextureManager.convert(col.getGreen()), TextureManager.convert(col.getBlue()), col.getAlpha()).getRGB());
            }
        }
        displayTexture = displayImg;
    }
    @Override
    public boolean stillEquals(RuleContainer obj){
        if(obj!=null&&obj instanceof Block){
            Block b = (Block)obj;
            if(!compareImages(texture, b.texture))return false;
            return Objects.equals(name, b.name)
                    &&cooling==b.cooling
                    &&fuelCell==b.fuelCell
                    &&moderator==b.moderator
                    &&Objects.equals(active, b.active);
        }
        return false;
    }
    /**
    * Compares two images pixel by pixel.
    * 
    * from https://stackoverflow.com/questions/11006394/is-there-a-simple-way-to-compare-bufferedimage-instances/11006474#11006474
    *
    * @param imgA the first image.
    * @param imgB the second image.
    * @return whether the images are both the same or not.
    */
   public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
       if(imgA==null&&imgB==null)return true;
       if(imgA==null&&imgB!=null)return false;
       if(imgA!=null&&imgB==null)return false;
     // The images must be the same size.
     if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
       return false;
     }

     int width  = imgA.getWidth();
     int height = imgA.getHeight();

     // Loop over every pixel.
     for (int y = 0; y < height; y++) {
       for (int x = 0; x < width; x++) {
         // Compare the pixels for equality.
         if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
           return false;
         }
       }
     }

     return true;
   }
}