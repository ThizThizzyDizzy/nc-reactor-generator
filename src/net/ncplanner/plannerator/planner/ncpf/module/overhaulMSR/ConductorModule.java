package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ConductorModule extends BlockFunctionModule{
    public ConductorModule(){
        super("nuclearcraft:overhaul_msr:conductor");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Conductor";
    }
}