package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
public class NCPFLegacyItemElement extends NCPFSettingsElement{
    public String name = "";
    public Integer metadata;
    public String nbt;
    public NCPFLegacyItemElement(){
        super("legacy_item");
        addString("name", ()->name, (v)->name = v, "Name", Type.NAMESPACED_NAME);
        addMetadata(()->metadata, (v)->metadata = v);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
    }
    public NCPFLegacyItemElement(String name){
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
        return name+(metadata!=null?":"+metadata:"")+(nbt!=null?nbt:"");
    }
    @Override
    public String getTypeName(){
        return "Legacy Item";
    }
    @Override
    public ArrayList<String> getLegacyNames(){
        ArrayList<String> names = super.getLegacyNames();
        if(nbt!=null&&metadata!=null)names.add(name+":"+metadata);
        return names;
    }
}