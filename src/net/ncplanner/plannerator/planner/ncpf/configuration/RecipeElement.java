package net.ncplanner.plannerator.planner.ncpf.configuration;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.ncpf.element.NCPFRecipeElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class RecipeElement extends NamedTexturedNCPFElement{
    public RecipeElement(){
        definition = new NCPFRecipeElement();
    }
    public NCPFRecipeElement getRecipeDefinition(){
        return (NCPFRecipeElement)definition;
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(!(definition instanceof NCPFRecipeElement))throw new RuntimeException("RecipeElement was intialized with a non-recipe definition! "+definition.toString());
        super.convertToObject(ncpf);
    }
    public NCPFElementStack getAnyOutput(){
        return getRecipeDefinition().outputs.iterator().next();
    }
}
