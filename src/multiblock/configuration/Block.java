package multiblock.configuration;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Locale;
import multiblock.Multiblock;
public abstract class Block{
    private BufferedImage grayscaleTexture = null;
    public abstract BufferedImage getBaseTexture();
    public abstract BufferedImage getTexture();
    private BufferedImage getGrayscaleTexture(){
        if(grayscaleTexture!=null)return grayscaleTexture;
        BufferedImage img = getTexture();
        if(img==null)return null;
        BufferedImage grayscale = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        for(int x = 0; x<img.getWidth(); x++){
            for(int y = 0; y<img.getHeight(); y++){
                Color c = new Color(img.getRGB(x, y));
                float[] hsb = new float[3];
                Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                hsb[1]*=.25f;
                c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
                grayscale.setRGB(x, y, c.getRGB());
            }
        }
        return grayscaleTexture = grayscale;
    }
    public abstract String getName();
    public abstract String getTooltip();
    public abstract boolean isCore();
    public abstract boolean isCasing();
    public abstract boolean hasRules();
    public abstract boolean calculateRules(int x, int y, int z, Multiblock multiblock);
    public abstract boolean canBeQuickReplaced();
    public boolean defaultEnabled(){
        return true;
    }
    public boolean roughMatch(String blockNam){
        blockNam = blockNam.toLowerCase(Locale.ENGLISH);
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        blockNam = blockNam.replace("_", " ").replace("liquid ", "").replace(" cooler", "").replace(" heat sink", "").replace(" heatsink", "").replace(" sink", "").replace(" neutron shield", "").replace(" shield", "").replace(" moderator", "").replace(" coolant", "").replace(" heater", "").replace("fuel ", "").replace(" reflector", "");
        if(blockNam.endsWith("s"))blockNam = blockNam.substring(0, blockNam.length()-1);
        String blockName = getName();
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        blockName = blockName.toLowerCase(Locale.ENGLISH).replace("_", " ").replace("liquid ", "").replace(" cooler", "").replace(" heat sink", "").replace(" heatsink", "").replace(" sink", "").replace(" neutron shield", "").replace(" shield", "").replace(" moderator", "").replace(" coolant", "").replace(" heater", "").replace("fuel ", "").replace(" reflector", "");
        if(blockName.endsWith("s"))blockName = blockName.substring(0, blockName.length()-1);
        return blockNam.equalsIgnoreCase(blockName);
    }
    public boolean isFullBlock(){
        return true;
    }
}