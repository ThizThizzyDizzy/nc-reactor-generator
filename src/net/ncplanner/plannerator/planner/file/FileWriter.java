package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.file.writer.HellrageFormatWriter;
import net.ncplanner.plannerator.planner.file.writer.NCPFFormatWriter;
import net.ncplanner.plannerator.planner.file.writer.PNGFormatWriter;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    public static FormatWriter NCPF,HELLRAGE;
    public static ImageFormatWriter PNG;
    static{
        formats.add(HELLRAGE = new HellrageFormatWriter());
        formats.add(NCPF = new NCPFFormatWriter());
        formats.add(PNG = new PNGFormatWriter());
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