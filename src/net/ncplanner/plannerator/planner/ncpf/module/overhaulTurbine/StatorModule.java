package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class StatorModule extends BlockFunctionModule{
    public float expansion;
    public StatorModule(){
        super("nuclearcraft:overhaul_turbine:stator");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        expansion = ncpf.getFloat("expansion");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("expansion", expansion);
    }
}