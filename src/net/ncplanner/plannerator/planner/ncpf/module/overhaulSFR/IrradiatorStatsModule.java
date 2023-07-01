package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class IrradiatorStatsModule extends BlockFunctionModule{
    public float efficiency;
    public float heat;
    public IrradiatorStatsModule(){
        super("nuclearcraft:overhaul_sfr:irradiator_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        heat = ncpf.getFloat("heat");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setFloat("heat", heat);
    }
}