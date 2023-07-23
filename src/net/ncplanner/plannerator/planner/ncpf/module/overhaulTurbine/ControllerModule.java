package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ControllerModule extends BlockFunctionModule{
    public ControllerModule(){
        super("nuclearcraft:overhaul_turbine:controller");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Controller";
    }
}