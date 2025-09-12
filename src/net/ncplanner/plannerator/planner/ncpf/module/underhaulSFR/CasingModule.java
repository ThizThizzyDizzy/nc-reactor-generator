package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.module.UnderhaulModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = UnderhaulModule.class)
public class CasingModule extends BlockFunctionModule{
    public CasingModule(){
        super("nuclearcraft:underhaul_sfr:casing");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
    }
    @Override
    public String getFunctionName(){
        return "Casing";
    }
}
