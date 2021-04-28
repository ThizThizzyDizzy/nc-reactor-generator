package planner.core;
public class PlannerImage{
    public static PlannerImage fromAWT(java.awt.image.BufferedImage awt){
        if(awt==null)return null;
        PlannerImage image = new PlannerImage(awt.getWidth(), awt.getHeight());
        for(int x = 0; x<awt.getWidth(); x++){
            for(int y = 0; y<awt.getHeight(); y++){
                image.setRGB(x, y, awt.getRGB(x, y));
            }
        }
        return image;
    }
    private final int width;
    private final int height;
    private final int[][] data;
    public PlannerImage(int width, int height){
        this.width = width;
        this.height = height;
        data = new int[width][height];
    }
    public void setRGB(int x, int y, int width, int height, int[] rgbArray, int offset, int scansize){
        int yoff  = offset;
        int off;
        for(int Y = y; Y<y+height; Y++, yoff+=scansize){
            off = yoff;
            for(int X = x; X<x+width; X++){
                data[X][Y] = rgbArray[off++];
            }
        }
    }
    public void setRGB(int x, int y, int rgb){
        data[x][y] = rgb;
    }
    public void setColor(int x, int y, Color color){
        setRGB(x, y, color.getRGB());
    }
    public java.awt.image.BufferedImage toAWT(){
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_4BYTE_ABGR);
        for(int x = 0; x<width; x++){
            for(int y = 0; y<height; y++){
                image.setRGB(x, y, data[x][y]);
            }
        }
        return image;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public int getRGB(int x, int y){
        return data[x][y];
    }
    public Color getColor(int x, int y){
        return new Color(getRGB(x, y));
    }
    public int getRed(int x, int y){
        return Color.rgbToRed(getRGB(x, y));
    }
    public int getGreen(int x, int y){
        return Color.rgbToGreen(getRGB(x, y));
    }
    public int getBlue(int x, int y){
        return Color.rgbToBlue(getRGB(x, y));
    }
    public int getAlpha(int x, int y){
        return Color.rgbToAlpha(getRGB(x, y));
    }
}