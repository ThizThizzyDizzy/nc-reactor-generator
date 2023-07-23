package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ActiveCoolerModule extends BlockFunctionModule{
    public ActiveCoolerModule(){
        super("nuclearcraft:underhaul_sfr:active_cooler");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Active Cooler";
    }
}