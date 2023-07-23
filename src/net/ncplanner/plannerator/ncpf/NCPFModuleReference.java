package net.ncplanner.plannerator.ncpf;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.element.NCPFModuleElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.BlockFunctionModule;
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
        if(mod instanceof BlockFunctionModule){
            BlockFunctionModule function = (BlockFunctionModule)mod;
            return function.getFunctionName();
        }
        return super.getDisplayName();
    }
}