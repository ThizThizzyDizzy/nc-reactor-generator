package net.ncplanner.plannerator.planner.ncpf.configuration;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
import net.ncplanner.plannerator.ncpf.element.NCPFLegacyRecipeElement;
public abstract class LegacyRecipeElement extends NamedTexturedNCPFElement{
    public LegacyRecipeElement(){
        definition = new NCPFLegacyRecipeElement();
    }
    public NCPFLegacyRecipeElement getRecipeDefinition(){
        return (NCPFLegacyRecipeElement)definition;
    }
    public NCPFElementStack getAnyOutput(){
        return getRecipeDefinition().outputs.iterator().next();
    }
}
