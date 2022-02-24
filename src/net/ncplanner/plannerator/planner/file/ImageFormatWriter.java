package net.ncplanner.plannerator.planner.file;
import java.io.IOException;
import java.io.OutputStream;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.planner.ImageIO;
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