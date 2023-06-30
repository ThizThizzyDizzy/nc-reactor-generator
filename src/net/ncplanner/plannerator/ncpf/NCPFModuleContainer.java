package net.ncplanner.plannerator.ncpf;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.ncpf.module.UnknownNCPFModule;
public class NCPFModuleContainer extends DefinedNCPFObject{
    public static HashMap<String, Supplier<NCPFModule>> recognizedModules = new HashMap<>();
    public static void initRecognizedElements(){
        recognizedModules.clear();
        recognizedModules.put(new NCPFBlockRecipesModule().name, NCPFBlockRecipesModule::new);
    }
    public HashMap<String, NCPFModule> modules;
    @Override
    public void convertFromObject(NCPFObject ncpf){
        for(String key : ncpf.keySet()){
            modules.put(key, ncpf.getDefinedNCPFObject(key, recognizedModules.getOrDefault(key, UnknownNCPFModule::new).get()));
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
    public void conglomerate(NCPFModuleContainer addon){
        for(String key : addon.modules.keySet()){
            NCPFModule addonModule = addon.modules.get(key);
            if(modules.containsKey(key)){
                modules.get(key).conglomerate(addonModule);
            }else modules.put(key, addonModule);
        }
    }
}