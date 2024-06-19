package net.ncplanner.plannerator.ncpf;
import java.util.List;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElementReference extends DefinedNCPFObject{
    public NCPFElementDefinition definition;
    public NCPFElement target;
    public NCPFElementReference(){
    }
    public NCPFElementReference(NCPFElementDefinition definition){
        this.definition = definition;
    }
    public NCPFElementReference(NCPFElement element){
        this(element.definition);
        target = element;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = NCPFElement.recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(target!=null)definition = target.definition;
        ncpf.setString("type", definition.type);
        definition.convertToObject(ncpf);
    }
    @Override
    public void setReferences(List<NCPFElement> elements){
        for(NCPFElement elem : elements){
            boolean isMatch = elem.definition.matches(definition);
            for(String legacy : elem.definition.getLegacyNames()){
                isMatch |= legacy.equals(definition.toString());
            }
            if(isMatch){
                if(target!=null)throw new IllegalArgumentException("Element Reference "+definition.toString()+" matches more than one element: "+elem.getDisplayName()+" and "+target.getDisplayName());
                target = elem;
            }
        }
    }
    public String getDisplayName(){
        return target==null?definition.toString():target.getDisplayName();
    }
}
