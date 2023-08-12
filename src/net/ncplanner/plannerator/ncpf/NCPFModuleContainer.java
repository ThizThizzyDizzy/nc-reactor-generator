package net.ncplanner.plannerator.ncpf;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.ncpf.module.UnknownNCPFModule;
public class NCPFModuleContainer extends DefinedNCPFObject{
    public static HashMap<String, Supplier<NCPFModule>> recognizedModules = new HashMap<>();
    public HashMap<String, NCPFModule> modules = new HashMap<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String key : ncpf.keySet()){
            modules.put(key, ncpf.getDefinedNCPFObject(key, recognizedModules.getOrDefault(key, UnknownNCPFModule::new)));
        }
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        for(String key : modules.keySet()){
            ncpf.setDefinedNCPFObject(key, modules.get(key));
        }
    }
    public boolean hasModule(Supplier<NCPFModule> module){
        return modules.containsKey(module.get().name);
    }
    public <T extends NCPFModule> T getModule(Supplier<T> module){
        return (T) modules.get(module.get().name);
    }
    public void setModule(NCPFModule module){
        if(module==null)return;
        modules.put(module.name, module);
    }
    public <T extends NCPFModule> void withModule(Supplier<T> module, Consumer<T> doIfPresent){
        T t = getModule(module);
        if(t!=null)doIfPresent.accept(t);
    }
    public <T extends NCPFModule> void withModuleOrCreate(Supplier<T> module, Consumer<T> doIfPresent){
        T t = getOrCreateModule(module);
        if(t!=null)doIfPresent.accept(t);
    }
    public <T extends NCPFModule> T getOrCreateModule(Supplier<T> module){
        T mod = getModule(module);
        if(mod==null){
            mod = module.get();
            modules.put(mod.name, mod);
        }
        return mod;
    }
    public void conglomerate(NCPFModuleContainer addon){
        for(String key : addon.modules.keySet()){
            NCPFModule addonModule = addon.modules.get(key);
            if(modules.containsKey(key)){
                modules.get(key).conglomerate(addonModule);
            }else modules.put(key, addonModule);
        }
    }
    @Override
    public void setReferences(List<NCPFElement> lst){
        for(NCPFModule module : modules.values())module.setReferences(lst);
    }
    void clearPlanneratorModules(){
        for(Iterator<String> it = modules.keySet().iterator(); it.hasNext();){
            String key = it.next();
            if(!key.startsWith("ncpf:"))it.remove();
        }
    }
}