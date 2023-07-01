package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.BlockReference;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class NeutronShieldModule extends BlockFunctionModule{
    public int heatPerFlux;
    public float efficiency;
    public BlockReference closed;
    public NeutronShieldModule(){
        super("nuclearcraft:overhaul_sfr:neutron_shield");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        heatPerFlux = ncpf.getInteger("heat_per_flux");
        efficiency = ncpf.getFloat("efficiency");
        closed = ncpf.getDefinedNCPFObject("closed", BlockReference::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("heat_per_flux", heatPerFlux);
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setDefinedNCPFObject("closed", closed);
    }
}