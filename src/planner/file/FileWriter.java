package planner.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    static{
        formats.add(new FormatWriter() {
            @Override
            public String getName(){
                return "NuclearCraft Planner Format";
            }
            @Override
            public String[] getExtensions(){
                return new String[]{"ncpf"};
            }
            @Override
            public void write(NCPFFile file, OutputStream stream){
                System.out.println("NCPF");
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        formats.add(new FormatWriter() {
            @Override
            public String getName(){
                return "Hellrage JSON format";
            }
            @Override
            public String[] getExtensions(){
                return new String[]{"json"};
            }
            @Override
            public void write(NCPFFile file, OutputStream stream){
                System.out.println("Hellrage");
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
    }
    public static void write(NCPFFile ncpf, OutputStream stream, FormatWriter format){
        format.write(ncpf, stream);
    }
    public static void write(NCPFFile ncpf, File file, FormatWriter format){
        if(file.exists())file.delete();
        try{
            file.createNewFile();
            write(ncpf, new FileOutputStream(file), format);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}