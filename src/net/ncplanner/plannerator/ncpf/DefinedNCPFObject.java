package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
public abstract class DefinedNCPFObject{
    public abstract void convertFromObject(NCPFObject ncpf);
    public abstract void convertToObject(NCPFObject ncpf);
    public void conglomerateElementList(List<NCPFElement> elements, List<NCPFElement> addonElements){
        for(NCPFElement addonElem : addonElements){
            NCPFElement match = null;
            for(NCPFElement elem : elements){
                if(elem.definition.matches(addonElem.definition)){
                    match = elem;
                    break;
                }
            }
            if(match==null)elements.add(addonElem);
            else match.conglomerate(addonElem);
        }
    }
    public <T extends DefinedNCPFObject> T copyTo(T blankCopy){
        NCPFObject obj = new NCPFObject();
        convertToObject(obj);
        blankCopy.convertFromObject(obj);
        return blankCopy;
    }
    public <T extends DefinedNCPFObject> List<T> copyList(NCPFBlockRecipesModule from, Supplier<T> newCopy){
        return copyList(from.recipes, newCopy);
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> List<T> copyList(List<V> from, Supplier<T> newCopy){
        return copyList(from, new ArrayList<>(), newCopy);
    }
    public <T extends DefinedNCPFObject> List<NCPFElement> copyList(List<T> from, NCPFBlockRecipesModule to){
        return copyList(from, to.recipes, NCPFElement::new);
    }
    public <T extends DefinedNCPFObject, V extends DefinedNCPFObject> List<T> copyList(List<V> from, List<T> to, Supplier<T> newCopy){
        for(V v : from)to.add(v.copyTo(newCopy.get()));
        return to;
    }
}