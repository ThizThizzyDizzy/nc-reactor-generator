package multiblock.configuration.overhaul.turbine;
import java.awt.Color;
import java.awt.image.BufferedImage;
import multiblock.configuration.TextureManager;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class Coil extends RuleContainer{
    public static Coil coil(String name, float efficiency, String texture){
        Coil coil = new Coil(name);
        coil.efficiency = efficiency;
        coil.setTexture(TextureManager.getImage(texture));
        return coil;
    }
    public static Coil bearing(String name, String texture){
        Coil coil = new Coil(name);
        coil.bearing = true;
        coil.setTexture(TextureManager.getImage(texture));
        return coil;
    }
    public static Coil connector(String name, String texture){
        Coil coil = new Coil(name);
        coil.connector = true;
        coil.setTexture(TextureManager.getImage(texture));
        return coil;
    }
    public String name;
    public float efficiency;
    public boolean bearing;
    public boolean connector;
    public BufferedImage texture;
    public BufferedImage displayTexture;
    public Coil(String name){
        this.name = name;
    }
    public Config save(TurbineConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        config.set("efficiency", efficiency);
        config.set("bearing", bearing);
        config.set("connector", connector);
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
                displayImg.setRGB(x, y, new Color(TextureManager.convert(col.getRed()), TextureManager.convert(col.getGreen()), TextureManager.convert(col.getBlue()), col.getAlpha()).getRGB());
            }
        }
        displayTexture = displayImg;
    }
    @Override
    public String toString(){
        return name;
    }
}