package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.InputStream;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.file.JSON;
public class JSONNCPFReader implements NCPFFormatReader{
    @Override
    public NCPFObject read(InputStream stream){
        return toNCPF(JSON.parse(stream));
    }
    private NCPFObject toNCPF(JSON.JSONObject json){
        NCPFObject ncpf = new NCPFObject();
        for(String key : json.keySet()){
            Object value = json.get(key);
            if(value instanceof JSON.JSONObject)value = toNCPF((JSON.JSONObject)value);
            if(value instanceof JSON.JSONArray)value = toNCPF((JSON.JSONArray)value);
            ncpf.put(key, value);
        }
        return ncpf;
    }
    private NCPFList toNCPF(JSON.JSONArray json){
        NCPFList ncpf = new NCPFList();
        for(Object value : json){//TODO reject multiple types in a list! (and maybe return a typed list?)
            if(value instanceof JSON.JSONObject)value = toNCPF((JSON.JSONObject)value);
            if(value instanceof JSON.JSONArray)value = toNCPF((JSON.JSONObject)value);
            ncpf.add(value);
        }
        return ncpf;
    }
}