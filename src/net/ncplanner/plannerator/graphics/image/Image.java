package net.ncplanner.plannerator.graphics.image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import net.ncplanner.plannerator.planner.ImageIO;
import org.lwjgl.BufferUtils;
public class Image{
    private final int width;
    private final int height;
    private final int[][] data;
    public Image(int width, int height){
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
    public int[] getRGB(int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize){
        int yoff = offset;
        int off;
        if(rgbArray==null){
            rgbArray = new int[offset+h*scansize];
        }
        for(int y = startY; y<startY+h; y++, yoff+=scansize){
            off = yoff;
            for(int x = startX; x<startX+w; x++){
                rgbArray[off++] = getRGB(x, y);
            }
        }
        return rgbArray;
    }
    public void setRGB(int x, int y, int rgb){
        data[x][y] = rgb;
    }
    public void setColor(int x, int y, Color color){
        setRGB(x, y, color.getRGB());
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
        return Color.getRed(getRGB(x, y));
    }
    public int getGreen(int x, int y){
        return Color.getGreen(getRGB(x, y));
    }
    public int getBlue(int x, int y){
        return Color.getBlue(getRGB(x, y));
    }
    public int getAlpha(int x, int y){
        return Color.getAlpha(getRGB(x, y));
    }
    public Image getSubimage(int x, int y, int width, int height){
        Image sub = new Image(width, height);
        for(int X = 0; X<width; X++){
            for(int Y = 0; Y<height; Y++){
                sub.setRGB(X, Y, getRGB(x+X, y+Y));
            }
        }
        return sub;
    }
    public ByteBuffer getGLData(){
        ByteBuffer data = BufferUtils.createByteBuffer(width*height*4);
        for(int y = 0; y<height; y++){
            for(int x = 0; x<width; x++){
                data.put((byte)getRed(x, y));
                data.put((byte)getGreen(x, y));
                data.put((byte)getBlue(x, y));
                data.put((byte)getAlpha(x, y));
            }
        }
        data.rewind();
        return data;
    }
    public Image copy(){
        Image copy = new Image(getWidth(), getHeight());
        for(int x = 0; x<getWidth(); x++){
            for(int y = 0; y<getHeight(); y++){
                copy.setRGB(x, y, getRGB(x, y));
            }
        }
        return copy;
    }
    public Image flip(){
        Image copy = new Image(getWidth(), getHeight());
        for(int x = 0; x<getWidth(); x++){
            for(int y = 0; y<getHeight(); y++){
                copy.setRGB(x, getHeight()-y-1, getRGB(x, y));
            }
        }
        return copy;
    }
    //thanks ChatGPT! :3
    public String toBase64(){
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(this, stream);
            return Base64.getEncoder().encodeToString(stream.toByteArray());
        }catch(IOException ex){
            throw new RuntimeException("Failed to encode base 64!", ex);
        }
    }
    public static Image fromBase64(String string){
        if(string==null)return null;
        try(ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(string))){
            return ImageIO.read(stream);
        }catch(IOException ex){
            throw new RuntimeException("Failed to decode base 64!", ex);
        }
    }
}