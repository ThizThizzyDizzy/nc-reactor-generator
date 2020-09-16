package planner;
import java.io.File;
public interface FileChooserResultListener{
    public void approved(File file, FileFormat format);
}