package planner;
import planner.file.FileFormat;
import java.io.File;
public interface FileChooserResultListener{
    public void approved(File file, FileFormat format);
}