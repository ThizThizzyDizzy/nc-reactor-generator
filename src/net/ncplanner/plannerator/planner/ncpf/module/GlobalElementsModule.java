package net.ncplanner.plannerator.planner.ncpf.module;
import java.util.ArrayList;
import java.util.List;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class GlobalElementsModule extends NCPFModule{
    public List<NCPFElement> elements = new ArrayList<>();
    public GlobalElementsModule(){
        super("plannerator:global_elements");
    }
    @Override
    public void conglomerate(NCPFModule addon){
        conglomerateElementList(elements, ((GlobalElementsModule)addon).elements);
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        elements = ncpf.getDefinedNCPFList("elements", NCPFElement::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFList("elements", elements);
    }
    @Override
    public boolean exists(){
        return !elements.isEmpty();
    }
}
