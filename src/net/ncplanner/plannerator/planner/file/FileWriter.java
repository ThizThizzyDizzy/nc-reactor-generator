package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.file.writer.HellrageWriter;
import net.ncplanner.plannerator.planner.file.writer.NCPFWriter;
import net.ncplanner.plannerator.planner.file.writer.PNGWriter;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    public static FormatWriter NCPF,HELLRAGE;
    public static ImageFormatWriter PNG;
    static{
        formats.add(HELLRAGE = new HellrageWriter());
        formats.add(NCPF = new NCPFWriter());
        formats.add(PNG = new PNGWriter());
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