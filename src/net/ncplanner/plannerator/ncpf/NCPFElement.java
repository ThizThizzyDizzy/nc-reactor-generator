package net.ncplanner.plannerator.ncpf;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.UnknownNCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class NCPFElement extends DefinedNCPFModularObject{
    public static HashMap<String, Supplier<NCPFElementDefinition>> recognizedElements = new HashMap<>();
    public NCPFElementDefinition definition;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        definition = recognizedElements.getOrDefault(ncpf.getString("type"), UnknownNCPFElement::new).get();
        definition.convertFromObject(ncpf);
        super.convertFromObject(ncpf);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definition.convertToObject(ncpf);
        super.convertToObject(ncpf);
    }
}