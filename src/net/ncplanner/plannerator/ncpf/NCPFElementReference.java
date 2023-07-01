package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class NCPFElementReference extends DefinedNCPFObject{
    public NCPFElementDefinition definition;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = NCPFElement.recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.convertToObject(ncpf);
    }
}