package net.ncplanner.plannerator.ncpf;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
/**
 * A DefinedNCPFObject with modules
 * @author thiz
 */
public abstract class DefinedNCPFModularObject extends DefinedNCPFObject{
    public NCPFModuleContainer modules = new NCPFModuleContainer();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        modules = ncpf.getDefinedNCPFObject("modules", NCPFModuleContainer::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("modules", modules);
    }
    public boolean hasModule(Supplier<NCPFModule> module){
        return modules.hasModule(module);
    }
    public void setModule(NCPFModule module){
        modules.setModule(module);
    }
    public void setModules(NCPFModule... modules){
        for(NCPFModule module : modules)setModule(module);
    }
    public <T extends NCPFModule> T getModule(Supplier<T> module){
        return modules.getModule(module);
    }
    public <T extends NCPFModule> void withModule(Supplier<T> module, Consumer<T> doIfPresent){
        modules.withModule(module, doIfPresent);
    }
    public <T extends NCPFModule> void withModuleOrCreate(Supplier<T> module, Consumer<T> doIfPresent){
        modules.withModuleOrCreate(module, doIfPresent);
    }
    public <T extends NCPFModule> T getOrCreateModule(Supplier<T> module){
        return modules.getOrCreateModule(module);
    }
    public void conglomerate(DefinedNCPFModularObject addon){
        modules.conglomerate(addon.modules);
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        modules.setReferences(lst);
    }
}