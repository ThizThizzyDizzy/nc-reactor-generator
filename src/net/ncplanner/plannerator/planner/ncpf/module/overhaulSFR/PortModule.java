package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class PortModule extends BlockFunctionModule{
    public boolean output;
    public PortModule(){
        super("nuclearcraft:overhaul_sfr:port");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        output = ncpf.getBoolean("output");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setBoolean("output", output);
    }
}