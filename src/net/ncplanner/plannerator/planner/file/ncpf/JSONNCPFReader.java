package net.ncplanner.plannerator.planner.file.ncpf;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfRoot;
public class JSONNCPFReader implements NCPFFormatReader{
    @Override
    public NcpfRoot read(InputStream stream){
        return NcpfJsonConverter.parseJson(new JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
    }
}
