package net.ncplanner.plannerator.planner.file.ncpf;
import java.io.IOException;
import java.io.OutputStream;
import net.ncplanner.plannerator.ncpf.io.NCPFList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.file.JSON;
public class JSONNCPFWriter implements NCPFFormatWriter{
    @Override
    public void write(NCPFObject ncpf, OutputStream stream) throws IOException{
        toJSON(ncpf).write(stream);
    }
    private JSON.JSONObject toJSON(NCPFObject ncpf){
        JSON.JSONObject json = new JSON.JSONObject();
        for(String key : ncpf.keySet()){
            Object value = ncpf.get(key);
            if(value instanceof NCPFObject)value = toJSON((NCPFObject)value);
            if(value instanceof NCPFList)value = toJSON((NCPFList)value);
            json.put(key, value);
        }
        return json;
    }
    private JSON.JSONArray toJSON(NCPFList ncpf){
        JSON.JSONArray json = new JSON.JSONArray();
        for(Object value : ncpf){
            if(value instanceof NCPFObject)value = toJSON((NCPFObject)value);
            if(value instanceof NCPFList)value = toJSON((NCPFList)value);
            json.add(value);
        }
        return json;
    }
    @Override
    public String getExtension(){
        return "json";
    }
}