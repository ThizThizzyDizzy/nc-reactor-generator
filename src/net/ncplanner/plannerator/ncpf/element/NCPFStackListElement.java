package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.NCPFElementStack;
/**
 * This is an NCPFListElement, when it is inside a recipe. Children must be NCPF element stacks.
 * @author Thiz
 * @see NCPFListElement
 */
public class NCPFStackListElement extends NCPFSettingsElement{
    public ArrayList<NCPFElementStack> elements = new ArrayList<>();
    public NCPFStackListElement(){
        super("list");
        addElementStacksList("elements", () -> elements, (elems) -> elements = elems, "Elements");
    }
    public NCPFStackListElement(NCPFElementStack... stacks){
        this();
        for(NCPFElementStack definition : stacks)elements.add(definition);
    }
    @Override
    public String getName(){
        ArrayList<String> strs = new ArrayList<>();
        for(NCPFElementStack def : elements)strs.add(def.toString());
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
}
