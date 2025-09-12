package net.ncplanner.plannerator.planner.ncpf.module;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class AirModule extends BlockFunctionModule{
    public AirModule(){
        super("minecraft:air");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
    }
    @Override
    public String getFunctionName(){
        return "Air";
    }
}
