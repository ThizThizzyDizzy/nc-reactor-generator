package net.ncplanner.plannerator.planner.ncpf.module.overhaulSFR;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulSFR.IrradiatorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
public class IrradiatorModule extends BlockFunctionModule implements RecipesBlockModule{
    public IrradiatorModule(){
        super("nuclearcraft:overhaul_sfr:irradiator");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Irradiator";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return IrradiatorRecipe::new;
    }
}