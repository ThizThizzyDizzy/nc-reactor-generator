package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.FusionTestModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = FusionTestModule.class)
public class PoloidalElectromagnetModule extends BlockFunctionModule{
    public PoloidalElectromagnetModule(){
        super("plannerator:fusion_test:poloidal_electromagnet");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
    }
    @Override
    public String getFunctionName(){
        return "Poloidal Electromagnet";
    }
}
