package net.ncplanner.plannerator.ncpf;
import java.util.List;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
@Deprecated
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
    public void setReferences(List<NCPFElement> elements, boolean soft){
        if(target!=null&&soft)return;
        NCPFElement resolved = null;
        for(NCPFElement elem : elements){
            boolean isMatch = elem.definition.matches(definition);
            for(String legacy : elem.definition.getLegacyNames()){
                isMatch |= legacy.equals(definition.toString());
            }
            if(isMatch){
                if(resolved==elem)continue;
                if(resolved!=null){
                    if(soft)return;
                    throw new IllegalArgumentException("Element Reference "+definition+" matches more than one element: "+elem.definition+" and "+resolved.definition);
                }
                resolved = elem;
            }
        }
        if(resolved!=null)target = resolved;
    }

    public String getDisplayName(){
        return target==null?definition.toString():target.getDisplayName();
    }
}
