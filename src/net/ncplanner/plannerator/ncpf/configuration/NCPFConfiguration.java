package net.ncplanner.plannerator.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
public abstract class NCPFConfiguration extends DefinedNCPFModularObject{
    public final String name;
    public NCPFConfiguration(String name){
        this.name = name;
    }
    public void setReferences(){
        List<NCPFElement>[] elements = getElements();
        for(List<NCPFElement> elems : elements){
            for(NCPFElement elem : elems){
                for(List<NCPFElement> lst : elements){
                    elem.setReferences(lst);
                }
            }
        }
    }
    public abstract List<NCPFElement>[] getElements();
    public <T extends NCPFElement> T getElement(NCPFElementDefinition definition){
        for(List<NCPFElement> elems : getElements()){
            for(NCPFElement elem : elems){
                if(elem.definition.matches(definition))return (T)elem;
            }
        }
        return null;
    }
}