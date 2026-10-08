package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import net.ncplanner.ncpf.structure.NcpfRoot;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFFileWriter{
    public static final ArrayList<NCPFFormatWriter> formats = new ArrayList<>();
    public static boolean botRunning;
    static{formats.add(new JSONNCPFWriter());}
    public static void write(Project project, OutputStream stream, NCPFFormatWriter format){
        write(NcpfBridge.toNcpfRoot(project), stream, format);
    }
    /** The caller retains ownership of the stream. */
    public static void write(NcpfRoot root, OutputStream stream, NCPFFormatWriter format){
        try{format.write(root, stream);}
        catch(IOException ex){throw new java.io.UncheckedIOException("Failed to write NCPF document", ex);}
    }
    public static void write(Project project, File file, NCPFFormatWriter format){
        write(NcpfBridge.toNcpfRoot(project), file, format);
    }
    public static void write(NcpfRoot root, File file, NCPFFormatWriter format){
        Path destination = file.toPath().toAbsolutePath();
        Path temporary = null;
        try{
            temporary = Files.createTempFile(destination.getParent(), ".ncpf-", ".tmp");
            try(OutputStream stream = Files.newOutputStream(temporary)){format.write(root, stream);}
            try{Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);}
            catch(AtomicMoveNotSupportedException ex){Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);}
        }catch(IOException ex){throw new java.io.UncheckedIOException("Failed to save NCPF document", ex);}
        finally{
            if(temporary!=null)try{Files.deleteIfExists(temporary);}catch(IOException ignored){}
        }
    }
}
