package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class RecipeStatsModule extends BlockFunctionModule{
    public float efficiency;
    public int heat;
    public int time;
    public float fluxiness;
    public RecipeStatsModule(){
        super("plannerator:fusion_test:recipe_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        efficiency = ncpf.getFloat("efficiency");
        heat = ncpf.getInteger("heat");
        time = ncpf.getInteger("time");
        fluxiness = ncpf.getFloat("fluxiness");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setFloat("efficiency", efficiency);
        ncpf.setInteger("heat", heat);
        ncpf.setInteger("time", time);
        ncpf.setFloat("fluxiness", fluxiness);
    }
}