package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import net.ncplanner.plannerator.planner.module.CoreModule;
import net.ncplanner.plannerator.planner.ncpf.annotation.RegisterWith;
@RegisterWith(module = CoreModule.class)
public class NCPFListElement extends NCPFSettingsElement{
    public ArrayList<NCPFElementDefinition> elements = new ArrayList<>();
    public NCPFListElement(){
        super("list");
        addElementsList("elements", () -> elements, (elems) -> elements = elems, "Elements");
    }
    public NCPFListElement(NCPFElementDefinition... definitions){
        this();
        for(NCPFElementDefinition definition : definitions)elements.add(definition);
    }
    @Override
    public String getName(){
        ArrayList<String> strs = new ArrayList<>();
        for(NCPFElementDefinition def : elements)strs.add(def.toString());
        return String.join(", ", strs);
    }
    @Override
    public String getTypeName(){
        return "List";
    }
    @Override
    public boolean canHaveAmount(){
        return false;
    }
    @Override
    public NCPFElementDefinition getRecipeContainedAlternative(){
        return new NCPFStackListElement();
    }
}
