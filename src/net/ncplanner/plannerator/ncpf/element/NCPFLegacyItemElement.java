package net.ncplanner.plannerator.ncpf.element;
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
        return metadata!=null?name+":"+metadata:name;
    }
    @Override
    public String getTypeName(){
        return "Legacy Item";
    }
}