package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
/**
 * A DefinedNCPFObject with modules
 * @author thiz
 */
public abstract class DefinedNCPFModularObject extends DefinedNCPFObject{
    public NCPFModuleContainer modules = new NCPFModuleContainer();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        if(ncpf.containsKey("modules"))modules = ncpf.getDefinedNCPFObject("modules", NCPFModuleContainer::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        if(!modules.modules.isEmpty())ncpf.setDefinedNCPFObject("modules", modules);
    }
    public <T extends NCPFModule> boolean hasModule(Supplier<T> module){
        return modules.hasModule(module);
    }
    public <T extends NCPFModule> T setModule(T module){
        modules.setModule(module);
        return module;
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
        for(NCPFModule module : modules.modules.values())module.setLocalReferences(this);
    }
    public <T extends DefinedNCPFObject> List<T> getRecipes(Supplier<T> newCopy){
        List<T> list = new ArrayList<>();
        withModule(NCPFBlockRecipesModule::new, (blockRecipes)->{
            copyList(blockRecipes.recipes, list, newCopy);
        });
        return list;
    }
    public void setRecipes(List<? extends DefinedNCPFObject>... recipes){
        boolean empty = true;
        for(List l : recipes)if(!l.isEmpty())empty = false;
        if(empty)return;//nothing to set
        withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes)->{
            blockRecipes.recipes.clear();
            for(List<? extends DefinedNCPFObject> list : recipes){
                copyRecipes(list, blockRecipes);
            }
        });
    }
    public <T extends DefinedNCPFObject> List<NCPFElement> copyRecipes(List<T> from, NCPFBlockRecipesModule to){
        return copyList(from, to.recipes, NCPFElement::new);
    }
}