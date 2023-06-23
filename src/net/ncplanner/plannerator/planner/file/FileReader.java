package net.ncplanner.plannerator.planner.file;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.file.recovery.NonRecoveryHandler;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryModeHandler;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    public static final RecoveryHandler defaultRecoveryHandler = new NonRecoveryHandler();
    public static NCPFFile read(InputStreamProvider provider){
        return read(provider, Core.recoveryMode?new RecoveryModeHandler():defaultRecoveryHandler);
    }
    public static NCPFFile read(InputStreamProvider provider, RecoveryHandler handler){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream(), handler);
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFFile read(File file){
        return read(file, Core.recoveryMode?new RecoveryModeHandler():defaultRecoveryHandler);
    }
    public static NCPFFile read(File file, RecoveryHandler handler){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        }, handler);
    }
    public static NCPFHeader readHeader(InputStreamProvider provider){
        for(FormatReader reader : formats){
            if(reader instanceof HeaderFormatReader){
                boolean matches = false;
                try{
                    if(reader.formatMatches(provider.getInputStream()))matches = true;
                }catch(Throwable t){}
                if(matches)return ((HeaderFormatReader)reader).readHeader(provider.getInputStream());
            }
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFHeader readHeader(File file){
        return readHeader(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
    }
}