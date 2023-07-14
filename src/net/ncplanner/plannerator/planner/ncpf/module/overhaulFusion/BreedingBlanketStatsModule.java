package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class BreedingBlanketStatsModule extends BlockFunctionModule{
    public boolean augmented;
    public float efficiency;
    public float heat;
    public BreedingBlanketStatsModule(){
        super("nuclearcraft:overhaul_sfr:irradiator_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        augmented = ncpf.getBoolean("augmented;");
        efficiency = ncpf.getFloat("efficiency");
        heat = ncpf.getFloat("heat");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setBoolean("augmented", augmented);
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setFloat("heat", heat);
    }
}