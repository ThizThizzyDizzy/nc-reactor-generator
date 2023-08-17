package net.ncplanner.plannerator.ncpf.element;
public class NCPFItemTagElement extends NCPFSettingsElement{
    public String name = "";
    public String nbt;
    public NCPFItemTagElement(){
        super("item_tag");
        addString("name", ()->name, (v)->name = v, "Name", Type.TAG);
        addString("nbt", ()->nbt, (v)->nbt = v, "NBT Tag", Type.NBT);
    }
    public NCPFItemTagElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        return name+(nbt!=null?nbt:"");
    }
    @Override
    public String getTypeName(){
        return "Item Tag";
    }
}