package net.ncplanner.plannerator.planner.file;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
public class FileReader{
    public static final ArrayList<FormatReader> formats = new ArrayList<>();
    public static NCPFFile read(InputStreamProvider provider){
        for(FormatReader reader : formats){
            boolean matches = false;
            try{
                if(reader.formatMatches(provider.getInputStream()))matches = true;
            }catch(Throwable t){}
            if(matches)return reader.read(provider.getInputStream());
        }
        throw new IllegalArgumentException("Unknown file format!");
    }
    public static NCPFFile read(File file){
        return read(() -> {
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        });
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