package planner;
import java.io.File;
import planner.file.FileFormat;
public interface FileChooserResultListener{
    public void approved(File file, FileFormat format);
}