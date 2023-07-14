package net.ncplanner.plannerator.ncpf;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
public class NCPFModuleReference extends NCPFElementReference{
    public Supplier<NCPFModule> module;
    @Override
    public void setReferences(List<NCPFElement> elements){
        module = NCPFModuleContainer.recognizedModules.get(((NCPFModuleElement)definition).name);
    }
}