package net.ncplanner.plannerator.planner.ncpf.defined;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.configuration.NCPFConfiguration;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public class DefinedPlanneratorModule<DefinedModule extends NCPFModule>{
    public final boolean existsForAddons;
    public final Supplier<DefinedModule> get;
    public final Consumer<DefinedModule> set;
    public final Supplier<DefinedModule> moduleSupplier;
    public DefinedPlanneratorModule(boolean existsForAddons, Supplier<DefinedModule> get, Consumer<DefinedModule> set, Supplier<DefinedModule> moduleSupplier){
        this.existsForAddons = existsForAddons;
        this.get = get;
        this.set = set;
        this.moduleSupplier = moduleSupplier;
    }
    public void init(NCPFConfiguration config, boolean addon){
        if(existsForAddons){
            config.setModule(get.get());
        }else if(!addon)set.accept(moduleSupplier.get());
    }
    public void convertFromObject(DefinedNCPFModularObject defined, NCPFObject ncpf){
        set.accept(defined.getModule(moduleSupplier));
    }
    public void convertToObject(DefinedNCPFModularObject defined, NCPFObject ncpf){
        defined.setModule(get.get());
    }
    public void removeSettings(){
        DefinedModule module = get.get();
        if(module instanceof NCPFSettingsModule)set.accept(null);
    }
    public void removeModule(NCPFModule module){
        DefinedModule defined = get.get();
        if(module==defined)set.accept(null);
    }
}
