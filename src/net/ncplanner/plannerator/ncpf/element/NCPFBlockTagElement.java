package net.ncplanner.plannerator.ncpf.element;
import java.util.HashMap;
public class NCPFBlockTagElement extends NCPFSettingsElement{
    public String name = "";
    public HashMap<String, Object> blockstate = new HashMap<>();
    public String nbt;
    public NCPFBlockTagElement(){
        super("block_tag");
        addString("name", ()->name, (v)->name = v, "Name", Type.TAG);
        addBlockstate(()->blockstate, (v)->blockstate = v);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
    }
    public NCPFBlockTagElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Block Tag";
    }
}