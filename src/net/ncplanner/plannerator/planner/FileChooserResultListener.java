package net.ncplanner.plannerator.planner;
import java.io.File;
import net.ncplanner.plannerator.planner.file.FileFormat;
public interface FileChooserResultListener{
    public void approved(File file, FileFormat format);
}