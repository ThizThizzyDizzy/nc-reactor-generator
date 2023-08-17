package net.ncplanner.plannerator.ncpf.element;
import java.util.HashMap;
public class NCPFBlockElement extends NCPFSettingsElement{
    public String name = "";
    public HashMap<String, Object> blockstate = new HashMap<>();
    public String nbt;
    public NCPFBlockElement(){
        super("block");
        addString("name", ()->name, (v)->name = v, "Name", Type.NAMESPACED_NAME);
        addBlockstate(()->blockstate, (v)->blockstate = v);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
    }
    public NCPFBlockElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    public NCPFBlockElement with(String key, Object value){
        blockstate.put(key, value);
        return this;
    }
    @Override
    public String getTypeName(){
        return "Block";
    }
}