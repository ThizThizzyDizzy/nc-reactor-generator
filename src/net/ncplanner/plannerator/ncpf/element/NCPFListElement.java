package net.ncplanner.plannerator.ncpf.element;
import java.util.ArrayList;
public class NCPFListElement extends NCPFSettingsElement{
    public ArrayList<NCPFElementDefinition> elements = new ArrayList<>();
    public NCPFListElement(){
        super("list");
        addElementsList("elements", ()->elements, (elems)->elements = elems, "Elements");
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
}