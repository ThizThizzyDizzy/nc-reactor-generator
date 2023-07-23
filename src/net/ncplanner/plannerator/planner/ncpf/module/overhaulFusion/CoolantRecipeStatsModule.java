package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipeStatsModule;
public class CoolantRecipeStatsModule extends NCPFRecipeStatsModule{
    public int heat;
    public float outputRatio;
    public CoolantRecipeStatsModule(){
        super("plannerator:fusion_test:coolant_recipe_stats");
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
    @Override
    public String getTooltip(){
        return "Heat: "+heat+"\n"
             + "Output Ratio: "+outputRatio;
    }
}