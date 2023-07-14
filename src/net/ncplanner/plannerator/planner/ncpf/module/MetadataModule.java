package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.HashMap;
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
}