package planner.file;
import planner.core.PlannerImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
public abstract class ImageFormatWriter extends FormatWriter{
    @Override
    public void write(NCPFFile ncpf, OutputStream stream){
        try{
            ImageIO.write(write(ncpf).toAWT(), "png", stream);
            stream.close();
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public abstract PlannerImage write(NCPFFile ncpf);
}