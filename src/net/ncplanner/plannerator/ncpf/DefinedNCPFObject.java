package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class DefinedNCPFObject{
    public abstract void convertFromObject(NCPFObject ncpf);
    public abstract void convertToObject(NCPFObject ncpf);
    public void conglomerateElementList(ArrayList<NCPFElement> elements, ArrayList<NCPFElement> addonElements){
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
}