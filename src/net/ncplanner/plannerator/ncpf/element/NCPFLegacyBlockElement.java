package net.ncplanner.plannerator.ncpf.element;
import java.util.HashMap;
import java.util.Objects;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFLegacyBlockElement extends NCPFElementDefinition{
    public String name;
    public Integer metadata;
    public HashMap<String, Object> blockstate = new HashMap<>();
    public String nbt;
    public NCPFLegacyBlockElement(){
        super("legacy_block");
    }
    public NCPFLegacyBlockElement(String name){
        this();
        if(name.matches(".*:\\d+")){
            String[] nameParts = name.split(":");
            metadata = Integer.valueOf(nameParts[nameParts.length-1]);
            name = name.substring(0, name.length()-(metadata+"").length()-1);
        }
        this.name = name;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        name = ncpf.getString("name");
        metadata = ncpf.getInteger("metadata");
        NCPFObject state = ncpf.getNCPFObject("blockstate");
        if(state!=null)blockstate.putAll(state);
        nbt = ncpf.getString("nbt");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name);
        ncpf.setInteger("metadata", metadata);
        if(!blockstate.isEmpty()){
            NCPFObject state = new NCPFObject();
            state.putAll(blockstate);
            ncpf.setNCPFObject("blockstate", state);
        }
        ncpf.setString("nbt", nbt);
    }
    @Override
    public boolean matches(NCPFElementDefinition definition){
        if(definition instanceof NCPFLegacyBlockElement){
            NCPFLegacyBlockElement other = (NCPFLegacyBlockElement) definition;
            return name.equals(other.name)&&Objects.equals(blockstate, other.blockstate)&&Objects.equals(metadata, other.metadata)&&Objects.equals(nbt, other.nbt);
        }
        return false;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return metadata!=null?name+":"+metadata:name;
    }
    public NCPFLegacyBlockElement with(String key, Object value){
        blockstate.put(key, value);
        return this;
    }
}