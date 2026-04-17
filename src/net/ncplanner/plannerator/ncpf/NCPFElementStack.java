package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElementStack extends DefinedNCPFModularObject{
    public NCPFElementDefinition definition = new UnknownNCPFElement();
    public int amount = 1;
    public NCPFElementStack(){
    }
    public NCPFElementStack(NCPFElementDefinition definition){
        this.definition = definition;
    }
    public NCPFElementStack(NCPFElementDefinition definition, int amount){
        if(!definition.canHaveAmount())throw new IllegalArgumentException("Cannot create an element stack, with an amount, using a definition that cannot have an amount!");
        this.definition = definition;
        this.amount = amount;
    }
    public NCPFElementStack(NCPFElementStack stack){
        definition = stack.definition;
        amount = stack.amount;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = NCPFElement.recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get().getRecipeContainedAlternative();
        if(definition.canHaveAmount()){
            amount = ncpf.getInteger("amount");
        }
        definition.convertFromObject(ncpf);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("type", definition.type);
        if(definition.canHaveAmount()){
            ncpf.setInteger("amount", amount);
        }
        definition.convertToObject(ncpf);
        super.convertToObject(ncpf);
    }
    @Override
    public String toString(){
        return definition.canHaveAmount()?definition.toString()+"*"+amount:definition.toString();
    }
}
