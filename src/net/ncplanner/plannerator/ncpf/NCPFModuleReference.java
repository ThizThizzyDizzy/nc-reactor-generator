package net.ncplanner.plannerator.ncpf;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class NCPFModuleReference extends NCPFElementReference{
    public Supplier<NCPFModule> module;
    public NCPFModuleReference(){}
    public NCPFModuleReference(Supplier<NCPFModule> module){
        super(new NCPFModuleElement(module));
        this.module = module;
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        super.convertFromObject(ncpf);
        setReferences(null);
    }
    @Override
    public void setReferences(List<NCPFElement> elements){
        module = NCPFModuleContainer.recognizedModules.get(((NCPFModuleElement)definition).name);
    }
    @Override
    public String getDisplayName(){
        NCPFModule mod = module.get();
        return mod.getFriendlyName();
    }
}