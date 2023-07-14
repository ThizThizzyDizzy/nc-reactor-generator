package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CasingModule extends BlockFunctionModule{
    public boolean edge;
    public CasingModule(){
        super("nuclearcraft:overhaul_msr:casing");
    }
    public CasingModule(boolean edge){
        this();
        this.edge = edge;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        edge = ncpf.getBoolean("edge");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setBoolean("edge", edge);
    }
}