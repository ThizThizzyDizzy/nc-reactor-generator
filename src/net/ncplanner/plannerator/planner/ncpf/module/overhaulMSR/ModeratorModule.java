package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class ModeratorModule extends BlockFunctionModule{
    public int flux;
    public float efficiency;
    public ModeratorModule(){
        super("nuclearcraft:overhaul_msr:moderator");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        flux = ncpf.getInteger("flux");
        efficiency = ncpf.getFloat("efficiency");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("flux", flux);
        ncpf.setFloat("efficiency", efficiency);
    }
}