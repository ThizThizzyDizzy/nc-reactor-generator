package net.ncplanner.plannerator.planner.ncpf.module.overhaulTurbine;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
public class RecipeStatsModule extends BlockFunctionModule{
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
}