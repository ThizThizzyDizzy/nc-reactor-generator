package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.IOException;
import java.io.OutputStream;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public interface NCPFFormatWriter{
    public void write(NCPFObject ncpf, OutputStream stream) throws IOException;
    public String getExtension();
}