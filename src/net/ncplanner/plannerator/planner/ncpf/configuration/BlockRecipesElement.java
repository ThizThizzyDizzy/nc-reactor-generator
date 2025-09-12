package net.ncplanner.plannerator.planner.ncpf.configuration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.defined.DefinedPlanneratorRecipe;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFRecipePortsModule;
public abstract class BlockRecipesElement extends NamedTexturedNCPFElement{
    protected final ArrayList<DefinedPlanneratorRecipe> definedPlanneratorRecipes = new ArrayList<>();
    protected <DefinedBlock extends BlockRecipesElement, DefinedRecipe extends NCPFElement, IBlockRecipe> void definePlanneratorRecipes(Class<DefinedBlock> clazz, Supplier<NCPFModule> recipeContainingFunctionModule, Function<DefinedBlock, List<DefinedRecipe>> get, Supplier<NCPFRecipePortsModule> getPorts, BiConsumer<DefinedBlock, List<DefinedRecipe>> set, Supplier<DefinedRecipe> recipeSupplier){
        definedPlanneratorRecipes.add(new DefinedPlanneratorRecipe(recipeContainingFunctionModule, get, getPorts, set, recipeSupplier));
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        definedPlanneratorRecipes.forEach(recipe -> recipe.convertFromObject(this, ncpf));
    }
    @Override
    public void conglomerate(DefinedNCPFModularObject addon){
        super.conglomerate(addon);
        definedPlanneratorRecipes.forEach(recipe -> recipe.conglomerate(this, addon));
    }
    @Override
    public void setReferences(List<NCPFElement> lst, boolean soft){
        super.setReferences(lst, soft);
        definedPlanneratorRecipes.forEach(recipe->recipe.setReferences(this, lst, soft));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definedPlanneratorRecipes.forEach(recipe->recipe.convertToObject(this, ncpf));
        super.convertToObject(ncpf);
    }
    public List<? extends NCPFElement> getBlockRecipes(){
        for(DefinedPlanneratorRecipe recipe : definedPlanneratorRecipes){
            List recipes = recipe.getBlockRecipes(this);
            if(recipes!=null)return recipes;
        }
        if(getParent()!=null)return getParent().getBlockRecipes();
        return null;
    }
    public void clearBlockRecipes(){
        definedPlanneratorRecipes.forEach(recipe -> recipe.clearBlockRecipes(this));
    }
    public void makePartial(List<Design> designs){
        definedPlanneratorRecipes.forEach(recipe -> recipe.makePartial(this, designs));
    }
    @Override
    public String getTitle(){
        return "Block";
    }
    public abstract BlockRecipesElement getParent();
}