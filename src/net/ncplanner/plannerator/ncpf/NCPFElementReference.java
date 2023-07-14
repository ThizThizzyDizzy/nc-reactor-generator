package net.ncplanner.plannerator.ncpf;
import java.util.List;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElementReference extends DefinedNCPFObject{
    public NCPFElementDefinition definition;
    public NCPFElement target;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = NCPFElement.recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> elements){
        for(NCPFElement elem : elements){
            if(elem.definition.matches(definition)){
                if(target!=null)throw new IllegalArgumentException("Element Reference matches more than one element!");
                target = elem;
            }
        }
    }
}