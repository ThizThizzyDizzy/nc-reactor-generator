package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFItemTagElement extends NCPFSettingsElement{
    public String name = "";
    public String nbt;
    public NCPFItemTagElement(){
        super("item_tag");
        addString("name", () -> name, (v) -> name = v, "Name", Type.TAG);
        addString("nbt", () -> nbt, (v) -> nbt = v, "NBT Tag", Type.NBT);
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
