package net.ncplanner.plannerator.ncpf.configuration;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.ncpf.Design;
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
    public Supplier<NCPFElement>[] getElementSuppliers(){
        Supplier<NCPFElement>[] supps = new Supplier[getElements().length];
        for(int i = 0; i<supps.length; i++)supps[i] = NCPFElement::new;
        return supps;
    }
    public <T extends NCPFElement> T getElement(NCPFElementDefinition definition){
        for(List<NCPFElement> elems : getElements()){
            for(NCPFElement elem : elems){
                if(elem.definition.matches(definition))return (T)elem;
            }
        }
        return null;
    }
    public List<NCPFElement>[] getMultiblockRecipes(){
        return new List[0];
    }
    public abstract void makePartial(List<Design> designs);
    public abstract String getName();
    public void init(){}
}