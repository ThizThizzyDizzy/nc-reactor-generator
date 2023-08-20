package net.ncplanner.plannerator.planner.ncpf.module.overhaulFusion;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.configuration.overhaulFusion.BreedingBlanketRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
import net.ncplanner.plannerator.planner.ncpf.module.RecipesBlockModule;
public class BreedingBlanketModule extends BlockFunctionModule implements RecipesBlockModule{
    public BreedingBlanketModule(){
        super("plannerator:fusion_test:breeding_blanket");
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){}
    @Override
    public void convertToObject(NCPFObject ncpf){}
    @Override
    public String getFunctionName(){
        return "Breeding Blanket";
    }
    @Override
    public Supplier<NCPFElement> getRecipeElement(){
        return BreedingBlanketRecipe::new;
    }
}