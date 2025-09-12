package net.ncplanner.plannerator.planner.ncpf.defined;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipePortsModule;
public class DefinedPlanneratorRecipe<DefinedBlock extends BlockRecipesElement, DefinedRecipe extends NCPFElement, IBlockRecipe>{
    public final Supplier<NCPFModule> recipeContainingFunctionModule;
    public final Function<DefinedBlock, List<DefinedRecipe>> get;
    public final Supplier<NCPFRecipePortsModule> getPorts;
    public final BiConsumer<DefinedBlock, List<DefinedRecipe>> set;
    public final Supplier<DefinedRecipe> recipeSupplier;
    public DefinedPlanneratorRecipe(Supplier<NCPFModule> recipeContainingFunctionModule, Function<DefinedBlock, List<DefinedRecipe>> get, Supplier<NCPFRecipePortsModule> getPorts, BiConsumer<DefinedBlock, List<DefinedRecipe>> set, Supplier<DefinedRecipe> recipeSupplier){
        this.recipeContainingFunctionModule = recipeContainingFunctionModule;
        this.get = get;
        this.getPorts = getPorts;
        this.set = set;
        this.recipeSupplier = recipeSupplier;
    }
    public void convertFromObject(DefinedBlock defined, NCPFObject ncpf){
        if(recipeContainingFunctionModule.get()!=null)set.accept(defined, defined.getRecipes(recipeSupplier));
    }
    public void conglomerate(DefinedBlock defined, DefinedNCPFModularObject addon){
        if(recipeContainingFunctionModule.get()!=null)set.accept(defined, defined.getRecipes(recipeSupplier));
    }
    public void setReferences(DefinedBlock defined, List<NCPFElement> lst, boolean soft){
        if(defined.getParent()!=null){
            set.accept(defined, get.apply((DefinedBlock)defined.getParent()));
        }
        get.apply(defined).forEach((recipe)->recipe.setReferences(lst, soft));
        NCPFRecipePortsModule<DefinedBlock> ports = getPorts.get();
        if(ports!=null){
            if(ports.input!=null)set.accept(ports.input.block, get.apply(defined));
        }
    }
    public void convertToObject(DefinedBlock defined, NCPFObject ncpf){
        defined.setRecipes(get.apply(defined));
    }
    public List<DefinedRecipe> getBlockRecipes(DefinedBlock defined){
        return get.apply(defined);
    }
    public void clearBlockRecipes(DefinedBlock defined){
        get.apply(defined).clear();
    }
    public void makePartial(DefinedBlock defined, List<Design> designs){
        defined.makePartial(get.apply(defined), designs);
    }
}
