package net.ncplanner.plannerator.multiblock.configuration;
import java.io.IOException;
import java.util.HashMap;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.ImageIO;
import net.ncplanner.plannerator.planner.MathUtil;
public class TextureManager{
    private static final HashMap<String, Image> imageMap = new HashMap<>();
    public static Image getImageRaw(String texture){
        if(imageMap.containsKey(texture))return imageMap.get(texture);
        try{
            imageMap.put(texture, ImageIO.read(Core.getInputStream(texture)));
            return imageMap.get(texture);
        }catch(IOException ex){
            System.err.println("Couldn't read file: "+texture);
            imageMap.put(texture, new Image(1, 1));
            return imageMap.get(texture);
        }
    }
    public static Image getImage(String texture){
        return getImageRaw("textures/"+texture+".png");
    }
    public static boolean SEPARATE_BRIGHT_TEXTURES = true;
    public static final float IMG_FAC = .003925f;
    public static final float IMG_POW = 2f;
    public static final float IMG_STRAIGHT_FAC = 1.5f;
    public static int convert(int c){
        if(SEPARATE_BRIGHT_TEXTURES){
            double f = IMG_FAC*MathUtil.pow(c, IMG_POW);
            float g = c/255f;
            double h = f*MathUtil.pow(g, IMG_STRAIGHT_FAC)+c*(1-MathUtil.pow(g, IMG_STRAIGHT_FAC));
            c = (int)h;
        }
        return c;
    }
    public static Color convert(Color color){
        return new Color(convert(color.getRed()), convert(color.getGreen()), convert(color.getBlue()), color.getAlpha());
    }
    public static Image convert(Image image){
        Image converted = new Image(image.getWidth(), image.getHeight());
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                Color col = new Color(image.getRGB(x, y));
                converted.setRGB(x, y, convert(col).getRGB());
            }
        }
        return converted;
    }
    public static final String LIQUID = "fluids/templates/liquid";
    public static final String MOLTEN = "fluids/templates/molten";
    public static final String STEAM = "fluids/templates/steam";
    public static final String GAS = "fluids/templates/gas";
    public static final String[] textureTemplates = new String[]{LIQUID, MOLTEN, STEAM, GAS};
    public static Color getNaKColor(Color color){
        return blend(color, 0xFFe5BC, .375f);
    }
    public static Color getHotNaKColor(Color color){
        return blend(color, 0xFFe5BC, 0.2f);
    }
    public static Color getFLiBeColor(Color color){
        return blend(color, 0xC1C8B0, 0.4f);
    }
    public static Color blend(Color color1, int color2, float blendRatio){
        blendRatio = Math.max(0,Math.min(1, blendRatio));

        int alpha1 = color1.getAlpha();
        int red1 = color1.getRed();
        int green1 = color1.getGreen();
        int blue1 = color1.getBlue();

        int alpha2 = color2 >> 24 & 0xFF;
        int red2 = (color2 & 0xFF0000) >> 16;
        int green2 = (color2 & 0xFF00) >> 8;
        int blue2 = color2 & 0xFF;

        int alpha = Math.max(alpha1, alpha2);
        int red = (int) (red1 + (red2 - red1) * blendRatio);
        int green = (int) (green1 + (green2 - green1) * blendRatio);
        int blue = (int) (blue1 + (blue2 - blue1) * blendRatio);
        return new Color(red, green, blue, alpha);
    }
    public static Image generateTexture(String template, Color color){
        Image image = TextureManager.getImage(template).copy();
        for(int x = 0; x<image.getWidth(); x++){
            for(int y = 0; y<image.getHeight(); y++){
                int rgb = image.getRGB(x, y);
                Color grayColor = new Color(rgb);
                int r = (int)(color.getRed()*(grayColor.getRed()/255f));
                int g = (int)(color.getGreen()*(grayColor.getGreen()/255f));
                int b = (int)(color.getBlue()*(grayColor.getBlue()/255f));
                image.setRGB(x, y, new Color(r, g, b, grayColor.getAlpha()).getRGB());
            }
        }
        return image;
    }
}