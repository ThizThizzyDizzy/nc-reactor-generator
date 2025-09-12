package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFFluidElement extends NCPFSettingsElement{
    public String name = "";
    public NCPFFluidElement(){
        super("fluid");
        addString("name", () -> name, (v) -> name = v, "Name", Type.NAMESPACED_NAME);
    }
    public NCPFFluidElement(String name){
        this();
        if(!name.contains(":"))throw new IllegalArgumentException("NCPFFluidElement must be namespaced!");
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Fluid";
    }
}
