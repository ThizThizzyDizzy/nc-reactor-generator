package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFFluidTagElement extends NCPFSettingsElement{
    public String name = "";
    public NCPFFluidTagElement(){
        super("fluid_tag");
        addString("name", () -> name, (v) -> name = v, "Name", Type.TAG);
    }
    public NCPFFluidTagElement(String name){
        this();
        this.name = name;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String getTypeName(){
        return "Fluid Tag";
    }
}
