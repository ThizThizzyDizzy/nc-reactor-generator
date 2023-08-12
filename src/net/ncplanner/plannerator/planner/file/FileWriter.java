package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.file.writer.BGStringWriter;
import net.ncplanner.plannerator.planner.file.writer.HellrageWriter;
import net.ncplanner.plannerator.planner.file.writer.LegacyNCPFWriter;
import net.ncplanner.plannerator.planner.file.writer.NCPFWriter;
import net.ncplanner.plannerator.planner.file.writer.PNGWriter;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class FileWriter{
    public static final ArrayList<FormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    public static LegacyNCPFWriter LEGACY_NCPF;
    public static HellrageWriter HELLRAGE;
    public static ImageFormatWriter PNG;
    public static NCPFWriter NCPF = new NCPFWriter();
    static{
        formats.add(HELLRAGE = new HellrageWriter());
        formats.add(LEGACY_NCPF = new LegacyNCPFWriter());
        formats.add(PNG = new PNGWriter());
        formats.add(new BGStringWriter());
    }
    public static void write(Project ncpf, OutputStream stream, FormatWriter format){
        format.write(ncpf, stream);
    }
    public static void write(Project ncpf, File file, FormatWriter format){
        if(file.exists())file.delete();
        try{
            file.createNewFile();
            write(ncpf, new FileOutputStream(file), format);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}