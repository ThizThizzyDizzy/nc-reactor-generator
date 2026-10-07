package net.ncplanner.plannerator.planner.file.ncpf;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class JSONNCPFReader implements NCPFFormatReader{
    @Override
    public NCPFObject read(InputStream stream){
        JsonElement tree=NcpfJsonConverter.gson.fromJson(new JsonReader(new InputStreamReader(stream,StandardCharsets.UTF_8)),JsonElement.class);
        if(tree==null||!tree.isJsonObject())return null;
        NCPFObject object=(NCPFObject)NcpfBridge.fromJson(tree);
        if(!(object.get("version") instanceof Integer))return null;
        return object;
    }
}
