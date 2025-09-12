package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFItemElement extends NCPFSettingsElement{
    public String name = "";
    public String nbt;
    public NCPFItemElement(){
        super("item");
        addString("name", () -> name, (v) -> name = v, "Name", Type.NAMESPACED_NAME);
        addString("nbt", () -> nbt, (v) -> nbt = v, "NBT Tag", Type.NBT);
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
    public String toString(){
        return name+(nbt!=null?nbt:"");
    }
    @Override
    public String getTypeName(){
        return "Item";
    }
}
