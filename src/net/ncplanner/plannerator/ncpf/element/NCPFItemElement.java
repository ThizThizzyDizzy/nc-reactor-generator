package net.ncplanner.plannerator.ncpf.element;
public class NCPFItemElement extends NCPFSettingsElement{
    public String name = "";
    public String nbt;
    public NCPFItemElement(){
        super("item");
        addString("name", ()->name, (v)->name = v, "Name", Type.NAMESPACED_NAME);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
    }
    public NCPFItemElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Item";
    }
}