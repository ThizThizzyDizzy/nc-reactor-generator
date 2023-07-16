package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class CoolantRecipeStatsModule extends BlockFunctionModule{
    public int heat;
    public float outputRatio;
    public CoolantRecipeStatsModule(){
        super("nuclearcraft:overhaul_sfr:coolant_recipe_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        heat = ncpf.getInteger("heat");
        outputRatio = ncpf.getFloat("output_ratio");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setInteger("heat", heat);
        ncpf.setFloat("output_ratio", outputRatio);
    }
}