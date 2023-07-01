package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolantVentModule extends BlockFunctionModule{
    public BlockReference output;
    public CoolantVentModule(){
        super("nuclearcraft:overhaul_sfr:coolant_vent");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        output = ncpf.getDefinedNCPFObject("output", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("output", output);
    }
}