package planner.file;
import java.io.InputStream;
public interface FormatReader{
    public boolean formatMatches(InputStream stream);
    public NCPFFile read(InputStream stream);
}