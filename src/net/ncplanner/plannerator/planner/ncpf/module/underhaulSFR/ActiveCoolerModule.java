package net.ncplanner.plannerator.planner.ncpf.module.underhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.underhaulSFR.ActiveCoolerRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
public class ActiveCoolerModule extends BlockFunctionModule implements RecipesBlockModule{
    public ActiveCoolerModule(){
        super("nuclearcraft:underhaul_sfr:active_cooler");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Active Cooler";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return ActiveCoolerRecipe::new;
    }
}