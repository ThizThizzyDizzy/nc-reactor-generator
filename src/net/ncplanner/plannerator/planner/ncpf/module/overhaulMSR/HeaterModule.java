package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.HeaterRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockRulesModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
public class HeaterModule extends BlockRulesModule implements RecipesBlockModule{
    public HeaterModule(){
        super("nuclearcraft:overhaul_msr:heater");
    }
    @Override
    public String getFunctionName(){
        return "Heater";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return HeaterRecipe::new;
    }
}