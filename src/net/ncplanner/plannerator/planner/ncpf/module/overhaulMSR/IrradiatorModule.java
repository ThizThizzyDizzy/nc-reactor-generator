package net.ncplanner.plannerator.planner.ncpf.module.overhaulMSR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.planner.module.OverhaulModule;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulMSR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = OverhaulModule.class)
public class IrradiatorModule extends BlockFunctionModule implements RecipesBlockModule{
    public IrradiatorModule(){
        super("nuclearcraft:overhaul_msr:irradiator");
    }
    @Override
    public String getFunctionName(){
        return "Irradiator";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return IrradiatorRecipe::new;
    }
}
