package multiblock.configuration.overhaul.fusion;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;
import multiblock.configuration.Configuration;
import multiblock.configuration.TextureManager;
import simplelibrary.config2.Config;
import simplelibrary.config2.ConfigList;
import simplelibrary.config2.ConfigNumberList;
public class Block extends RuleContainer{
    public String name;
    public String displayName;
    public ArrayList<String> legacyNames = new ArrayList<>();
    public int cooling = 0;
    public boolean cluster = false;
    public boolean createCluster = false;
    public boolean conductor = false;
    public boolean core = false;
    public boolean connector = false;
    public boolean electromagnet = false;
    public boolean heatingBlanket = false;
    public boolean reflector = false;
    public boolean breedingBlanket = false;
    public boolean augmentedBreedingBlanket = false;
    public boolean shielding = false;
    public float efficiency;
    public float shieldiness;
    public boolean functional;
    public BufferedImage texture;
    public BufferedImage displayTexture;
    public Block(String name){
        this.name = name;
    }
    public Config save(Configuration parent, FusionConfiguration configuration, boolean partial){
        Config config = Config.newConfig();
        config.set("name", name);
        if(displayName!=null)config.set("displayName", displayName);
        if(!legacyNames.isEmpty()){
            ConfigList lst = new ConfigList();
            for(String s : legacyNames)lst.add(s);
            config.set("legacyNames", lst);
        }
        if(cooling!=0)config.set("cooling", cooling);
        if(cluster)config.set("cluster", cluster);
        if(createCluster)config.set("createCluster", createCluster);
        if(conductor)config.set("conductor", conductor);
        if(core)config.set("core", core);
        if(connector)config.set("connector", connector);
        if(electromagnet)config.set("electromagnet", electromagnet);
        if(heatingBlanket)config.set("heatingBlanket", heatingBlanket);
        if(reflector)config.set("reflector", reflector);
        if(breedingBlanket)config.set("breedingBlanket", breedingBlanket);
        if(augmentedBreedingBlanket)config.set("augmentedBreedingBlanket", augmentedBreedingBlanket);
        if(shielding)config.set("shielding", shielding);
        if(reflector)config.set("efficiency", efficiency);
        if(shielding)config.set("shieldiness", shieldiness);
        config.set("functional", functional);
        if(!rules.isEmpty()){
            ConfigList ruls = new ConfigList();
            for(PlacementRule rule : rules){
                ruls.add(rule.save(parent, configuration));
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
    @Override
    public boolean stillEquals(RuleContainer rc){
        Block b = (Block)rc;
        return Objects.equals(b.name, name)
                &&b.cooling==cooling
                &&b.cluster==cluster
                &&b.createCluster==createCluster
                &&b.conductor==conductor
                &&b.connector==connector
                &&b.core==core
                &&b.electromagnet==electromagnet
                &&b.heatingBlanket==heatingBlanket
                &&b.reflector==reflector
                &&b.breedingBlanket==breedingBlanket
                &&b.augmentedBreedingBlanket==augmentedBreedingBlanket
                &&b.shielding==shielding
                &&b.efficiency==efficiency
                &&b.shieldiness==shieldiness
                &&b.functional==functional
                &&compareImages(b.texture, texture);
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