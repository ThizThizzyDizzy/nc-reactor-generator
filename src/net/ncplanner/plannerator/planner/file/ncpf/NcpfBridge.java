package net.ncplanner.plannerator.planner.file.ncpf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Map;
import net.ncplanner.ncpf.io.NcpfJsonConverter;
import net.ncplanner.ncpf.structure.NcpfRoot;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Project;
/** Preserves extension modules while adapting NCPF library data to the calculation model. */
public final class NcpfBridge{
    private NcpfBridge(){}
    public static Project toProject(NcpfRoot root){
        if(root==null)throw new IllegalArgumentException("Empty NCPF document");
        Project project=new Project();
        project.convertFromObject((NCPFObject)fromJson(NcpfJsonConverter.gson.toJsonTree(root)));
        return project;
    }
    public static Object fromJson(JsonElement json){
        if(json.isJsonNull())return null;
        if(json.isJsonObject()){
            NCPFObject object=new NCPFObject();
            for(Map.Entry<String,JsonElement> entry:json.getAsJsonObject().entrySet())
                object.put(entry.getKey(),fromJson(entry.getValue()));
            return object;
        }
        if(json.isJsonArray()){
            NCPFList list=new NCPFList();
            for(JsonElement element:json.getAsJsonArray())list.add(fromJson(element));
            return list;
        }
        JsonPrimitive value=json.getAsJsonPrimitive();
        if(value.isBoolean())return value.getAsBoolean();
        if(value.isString())return value.getAsString();
        String number=value.getAsString();
        if(!number.contains(".")&&!number.contains("e")&&!number.contains("E")){
            long integer=value.getAsLong();
            if(integer>=Integer.MIN_VALUE&&integer<=Integer.MAX_VALUE)return (int)integer;
            return integer;
        }
        return value.getAsDouble();
    }
}
