package net.ncplanner.plannerator.ncpf;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
/**
 * A DefinedNCPFObject with modules
 * @author thiz
 */
public abstract class DefinedNCPFModularObject extends DefinedNCPFObject{
    public NCPFModuleContainer modules;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("modules", new NCPFModuleContainer());
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setDefinedNCPFObject("modules", modules);
    }
    public boolean hasModule(Supplier<NCPFModule> module){
        return modules.hasModule(module);
    }
    public <T extends NCPFModule> T getModule(Supplier<T> module){
        return modules.getModule(module);
    }
    public void conglomerate(DefinedNCPFModularObject addon){
        modules.conglomerate(addon.modules);
    }
}