package net.ncplanner.plannerator.planner.file.ncpf;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfRoot;
public class JSONNCPFWriter implements NCPFFormatWriter{
    @Override
    public void write(NcpfRoot object,OutputStream stream) throws IOException{
        JsonWriter writer=new JsonWriter(new OutputStreamWriter(stream,StandardCharsets.UTF_8));
        NcpfJsonConverter.writeJson(object, writer);
        writer.flush();
    }
    @Override
    public String getExtension(){return "json";}
    public String getName(){return "JSON";}
}
