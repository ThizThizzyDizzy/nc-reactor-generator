package net.ncplanner.plannerator.planner.file.reader;
import java.io.File;
import java.io.InputStream;
import java.util.function.Supplier;
import net.ncplanner.plannerator.planner.file.FormatReader;
import net.ncplanner.plannerator.planner.file.ncpf.NCPFFileReader;
import net.ncplanner.plannerator.planner.file.recovery.RecoveryHandler;
import net.ncplanner.plannerator.planner.ncpf.Project;
public class NCPFReader implements FormatReader{
    @Override
    public boolean formatMatches(Supplier<InputStream> provider){
        try(InputStream stream=provider.get();
            com.google.gson.stream.JsonReader reader=new com.google.gson.stream.JsonReader(
                new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8))){
            reader.beginObject();
            while(reader.hasNext()){
                String key=reader.nextName();
                if(key.equals("version")){
                    reader.nextInt();
                    return true;
                }
                reader.skipValue();
            }
        }catch(Exception ex){
            return false;
        }
        return false;
    }
    @Override
    public Project read(Supplier<InputStream> provider, RecoveryHandler recovery, File fileContext){
        return NCPFFileReader.read(provider, fileContext);
    }
}
