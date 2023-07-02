package net.ncplanner.plannerator.ncpf.configuration;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
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
}