package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFFileWriter{
    public static final ArrayList<NCPFFormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    static{
        formats.add(new JSONNCPFWriter());
    }
    public static void write(Project project, OutputStream stream, NCPFFormatWriter format){
        NCPFObject ncpf = new NCPFObject();
        project.convertToObject(ncpf);
        try{
            format.write(ncpf, stream);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    public static void write(Project project, File file, NCPFFormatWriter format){
        if(file.exists())file.delete();
        try{
            file.createNewFile();
            write(project, new FileOutputStream(file), format);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}