package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolerModule extends BlockFunctionModule{
    public int cooling;
    public CoolerModule(){
        super("nuclearcraft:underhaul_sfr:cooler");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        cooling = ncpf.getInteger("cooling");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("cooling", cooling);
    }
}