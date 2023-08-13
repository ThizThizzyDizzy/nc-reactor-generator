package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipeStatsModule;
public class RecipeStatsModule extends NCPFRecipeStatsModule{
    public double power;
    public double coefficient;
    public RecipeStatsModule(){
        super("nuclearcraft:overhaul_turbine:recipe_stats");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        power = ncpf.getDouble("power");
        coefficient = ncpf.getDouble("coefficient");
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDouble("power", power);
        ncpf.setDouble("coefficient", coefficient);
    }
    @Override
    public String getTooltip(){
        return "Expansion Coefficient: "+coefficient+"\n"
             + "Energy Density (RF/mb): "+power;
    }
    @Override
    public void conglomerate(NCPFModule addon){
        RecipeStatsModule stats = (RecipeStatsModule)addon;
        power = stats.power;
        coefficient = stats.coefficient;
    }
}