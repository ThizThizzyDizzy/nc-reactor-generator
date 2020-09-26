package planner.file;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
public abstract class ImageFormatWriter extends FormatWriter{
    @Override
    public void write(NCPFFile ncpf, OutputStream stream){
        try{
            ImageIO.write(write(ncpf), "png", stream);
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract BufferedImage write(NCPFFile ncpf);
}