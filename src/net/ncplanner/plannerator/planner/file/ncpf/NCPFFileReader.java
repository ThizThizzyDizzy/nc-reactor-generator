package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.file.InputStreamProvider;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFFileReader{
    public static final ArrayList<NCPFFormatReader> formats = new ArrayList<>();
    private static JSONNCPFReader JSON;
    static{
        formats.add(JSON = new JSONNCPFReader());
    }
    public static Project read(InputStreamProvider provider){
        Project project = new Project();
        NCPFObject ncpf = null;
        for(NCPFFormatReader reader : formats){
            try{
                ncpf = reader.read(provider.getInputStream());
                break;
            }catch(Throwable t){}
        }
        if(ncpf==null)throw new IllegalArgumentException("Unknown file format!");
        project.convertFromObject(ncpf);
        return project;
    }
    public static Project read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}