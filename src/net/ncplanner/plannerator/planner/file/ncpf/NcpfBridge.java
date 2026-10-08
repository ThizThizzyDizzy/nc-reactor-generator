package net.ncplanner.plannerator.planner.file.ncpf;
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
        JsonObject source = NcpfJsonConverter.gson.toJsonTree(root, NcpfRoot.class).getAsJsonObject();
        NCPFObject internal = (NCPFObject)fromJson(source);
        if(internal.getNCPFObject("configuration")==null)internal.put("configuration", new NCPFObject());
        project.convertFromObject(internal);
        return project;
    }
    /** Converts the complete internal document before it reaches a format writer. */
    public static NcpfRoot toNcpfRoot(Project project){
        JsonObject current = internalDocument(project);
        return NcpfJsonConverter.gson.fromJson(current, NcpfRoot.class);
    }
    /** For prepared export documents, whose trimming is intentional. */
    public static NcpfRoot toNcpfRoot(NCPFObject document){
        return NcpfJsonConverter.gson.fromJson(toJson(document), NcpfRoot.class);
    }
    private static JsonObject internalDocument(Project project){
        NCPFObject document = new NCPFObject();
        project.convertToObject(document);
        return toJson(document).getAsJsonObject();
    }
    /** Gson's default map writer omits explicit nulls; opaque NCPF payloads must retain them. */
    private static JsonElement toJson(Object value){
        if(value==null)return com.google.gson.JsonNull.INSTANCE;
        if(value instanceof Map){
            Map<?, ?> map = (Map<?, ?>)value;
            JsonObject object = new JsonObject();
            for(Map.Entry<?, ?> entry : map.entrySet())object.add(entry.getKey().toString(), toJson(entry.getValue()));
            return object;
        }
        if(value instanceof Iterable){
            Iterable<?> list = (Iterable<?>)value;
            com.google.gson.JsonArray array = new com.google.gson.JsonArray();
            for(Object entry : list)array.add(toJson(entry));
            return array;
        }
        return NcpfJsonConverter.gson.toJsonTree(value);
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
            // Indexed insertion preserves opaque null entries; NCPFList.add(value) omits them.
            for(JsonElement element:json.getAsJsonArray())list.add(list.size(), fromJson(element));
            return list;
        }
        JsonPrimitive value=json.getAsJsonPrimitive();
        if(value.isBoolean())return value.getAsBoolean();
        if(value.isString())return value.getAsString();
        java.math.BigDecimal number = value.getAsBigDecimal();
        try{return number.intValueExact();}catch(ArithmeticException ignored){}
        try{return number.longValueExact();}catch(ArithmeticException ignored){}
        return number;
    }
}
