package net.ncplanner.plannerator.planner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.ncplanner.plannerator.graphics.image.Color;
import net.ncplanner.plannerator.graphics.image.Image;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;
public class ImageIO{
    public static Image read(InputStream input) throws IOException{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int b;
        while((b = input.read())!=-1){
            output.write(b);
        }
        input.close();
        output.close();
        byte[] data = output.toByteArray();
        ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        IntBuffer width = BufferUtils.createIntBuffer(1);;
        IntBuffer height = BufferUtils.createIntBuffer(1);;
        ByteBuffer imageData = STBImage.stbi_load_from_memory(buffer, width, height, BufferUtils.createIntBuffer(1), 4);
        if(imageData==null)throw new IOException("Failed to load image: "+STBImage.stbi_failure_reason());
        Image image = new Image(width.get(0), height.get(0));
        for(int y = 0; y<image.getHeight(); y++){
            for(int x = 0; x<image.getWidth(); x++){
                image.setRGB(x, y, Color.getRGB(imageData.get(), imageData.get(), imageData.get(), imageData.get()));
            }
        }
        return image;
    }
    public static Image read(File file) throws IOException{
        return read(new FileInputStream(file));
    }
    public static void write(Image image, OutputStream output) throws IOException{
        ByteBuffer imageData = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
        imageData.mark();
        for(int y = 0; y<image.getHeight(); y++){
            for(int x = 0; x<image.getWidth(); x++){
                imageData.put((byte)image.getRed(x, y));
                imageData.put((byte)image.getGreen(x, y));
                imageData.put((byte)image.getBlue(x, y));
                imageData.put((byte)image.getAlpha(x, y));
            }
        }
        imageData.reset();
//        File file = new File("temp.png");
//        if(!STBImageWrite.stbi_write_png(file.getAbsolutePath(), image.getWidth(), image.getHeight(), 4, imageData, 0))throw new IOException("Failed to write image!");
//        FileInputStream in = new FileInputStream(file);
//        int b;
//        while((b = in.read())!=-1){
//            output.write(b);;
//        }
//        in.close();
//        file.delete();
        
        if(!STBImageWrite.stbi_write_png_to_func((context, data, size) -> {
            try{
                for(int i = 0; i<size; i++)output.write(MemoryUtil.memGetByte(data+i));
            }catch(IOException ex){
                throw new RuntimeException(ex);
            }
        }, 45, image.getWidth(), image.getHeight(), 4, imageData, 0))throw new IOException("Failed to write image!");
    }
}