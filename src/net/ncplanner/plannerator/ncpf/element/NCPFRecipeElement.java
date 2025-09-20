package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFRecipeElement extends NCPFSettingsElement{
    public String name = "";
    public NCPFRecipeElement(){
        super("recipe");
        addString("name", () -> name, (v) -> name = v, "Name", Type.NAME);
    }
    public NCPFRecipeElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Recipe";
    }
}
