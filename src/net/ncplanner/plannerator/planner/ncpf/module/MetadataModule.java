package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.HashMap;
import java.util.Set;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class MetadataModule extends NCPFModule{
    public HashMap<String, String> metadata = new HashMap<>();
    public MetadataModule(){
        super("plannerator:metadata");
    }
    @Override
    public void conglomerate(NCPFModule addon){}
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String key : ncpf.keySet())metadata.put(key, ncpf.getString(key));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.putAll(metadata);
    }
    public void put(String key, String value){
        metadata.put(key, value);
    }
    public String get(String key){
        return metadata.get(key);
    }
    public void clear(){
        metadata.clear();
    }
    public void putAll(HashMap<String, String> metadata){
        this.metadata.putAll(metadata);
    }
    public Set<String> keys(){
        return metadata.keySet();
    }
    public boolean contains(String name){
        return metadata.containsKey(name);
    }
}