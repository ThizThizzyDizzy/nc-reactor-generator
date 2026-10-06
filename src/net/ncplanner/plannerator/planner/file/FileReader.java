package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.function.Supplier;
import net.ncplanner.ncpf.runtime.RuntimeNcpf;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.recovery.NonRecoveryHandler;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryModeHandler;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    public static final RecoveryHandler defaultRecoveryHandler = new NonRecoveryHandler();
    public static RuntimeNcpf read(Supplier<InputStream> provider){
        return read(provider, Core.recoveryMode?new RecoveryModeHandler():defaultRecoveryHandler, null);
    }
    public static RuntimeNcpf read(Supplier<InputStream> provider, RecoveryHandler handler, File fileContext){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider))matches = true;
            }catch(Throwable t){}
            if(matches){
                RuntimeNcpf project = reader.read(provider, handler, fileContext);
                if(project==null)continue; // format does not match, actually. false alarm.
                return project;
            }
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static RuntimeNcpf read(File file){
        return read(file, Core.recoveryMode?new RecoveryModeHandler():defaultRecoveryHandler);
    }
    public static RuntimeNcpf read(File file, RecoveryHandler handler){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        }, handler, file);
    }
}