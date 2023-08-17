package net.ncplanner.plannerator.ncpf.element;
import java.util.HashMap;
public class NCPFLegacyBlockElement extends NCPFSettingsElement{
    public String name = "";
    public Integer metadata;
    public HashMap<String, Object> blockstate = new HashMap<>();
    public String nbt;
    public NCPFLegacyBlockElement(){
        super("legacy_block");
        addString("name", ()->name, (v)->name = v, "Name", Type.NAMESPACED_NAME);
        addMetadata(()->metadata, (v)->metadata = v);
        addBlockstate(()->blockstate, (v)->blockstate = v);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
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
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return name+(metadata!=null?":"+metadata:"")+stringifyBlockstate(blockstate)+(nbt!=null?nbt:"");
    }
    public NCPFLegacyBlockElement with(String key, Object value){
        blockstate.put(key, value);
        return this;
    }
    @Override
    public String getTypeName(){
        return "Legacy Block";
    }
}