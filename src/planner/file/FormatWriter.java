package planner.file;
import java.io.OutputStream;
public interface FormatWriter{
    public String getName();
    public String[] getExtensions();
    public void write(NCPFFile ncpf, OutputStream stream);
}