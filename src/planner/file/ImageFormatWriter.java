package planner.file;
import java.io.IOException;
import java.io.OutputStream;
import planner.ImageIO;
import simplelibrary.image.Image;
public abstract class ImageFormatWriter extends FormatWriter{
    @Override
    public void write(NCPFFile ncpf, OutputStream stream){
        try{
            ImageIO.write(write(ncpf), stream);
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract Image write(NCPFFile ncpf);
}