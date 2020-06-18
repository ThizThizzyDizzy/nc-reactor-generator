package planner.file;
import java.io.File;
public interface FormatReader{
    public boolean formatMatches(File file);
    public NCPFFile read(File file);
}