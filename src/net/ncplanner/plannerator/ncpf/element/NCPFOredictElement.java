package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFOredictElement extends NCPFSettingsElement{
    public String oredict = "";
    public NCPFOredictElement(){
        super("oredict");
        addString("oredict", () -> oredict, (v) -> oredict = v, "Oredict", Type.OREDICT);
    }
    public NCPFOredictElement(String oredict){
        this();
        this.oredict = oredict;
    }
    @Override
    public String getName(){
        return oredict;
    }
    @Override
    public String getTypeName(){
        return "Oredict";
    }
}
