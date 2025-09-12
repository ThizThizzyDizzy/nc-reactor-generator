package net.ncplanner.plannerator.ncpf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.defined.field.DefinedNCPFField;
import net.ncplanner.plannerator.ncpf.defined.field.ElementListNCPFField;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.ncpf.module.NCPFBlockRecipesModule;
import net.ncplanner.plannerator.ncpf.module.NCPFModule;
import net.ncplanner.plannerator.planner.ncpf.defined.DefinedPlanneratorModule;
import net.ncplanner.plannerator.planner.ncpf.defined.field.DefinedPlanneratorField;
import net.ncplanner.plannerator.planner.ncpf.defined.field.ElementListPlanneratorField;
/**
 * A DefinedNCPFObject with modules
 *
 * @author thiz
 */
public abstract class DefinedNCPFModularObject extends DefinedNCPFObject{
    public NCPFModuleContainer modules = new NCPFModuleContainer();
    protected final ArrayList<DefinedNCPFField> definedNCPFFields = new ArrayList<>();
    protected final ArrayList<DefinedPlanneratorField> definedPlanneratorFields = new ArrayList<>();
    protected final ArrayList<DefinedPlanneratorModule> definedPlanneratorModules = new ArrayList<>();
    @Override
    public void convertFromObject(NCPFObject ncpf){
        if(ncpf.containsKey("modules"))modules = ncpf.getDefinedNCPFObject("modules", NCPFModuleContainer::new);
        definedNCPFFields.forEach(field -> field.convertFromObject(this, ncpf));
        definedPlanneratorModules.forEach((module) -> module.convertFromObject(this, ncpf));
        definedPlanneratorFields.forEach(field -> field.convertFromObject(this, ncpf));
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        definedPlanneratorModules.forEach((module) -> module.convertToObject(this, ncpf));
        definedPlanneratorFields.forEach(field -> field.convertToObject(this, ncpf));
        if(!modules.modules.isEmpty())ncpf.setDefinedNCPFObject("modules", modules);
        definedNCPFFields.forEach(field -> field.convertToObject(this, ncpf));
    }
    protected <DefinedObject extends DefinedNCPFModularObject> void defineNCPFField(Class<DefinedObject> clazz, String name, Function<DefinedObject, List<NCPFElement>> get, Consumer<List<NCPFElement>> set){
        definedNCPFFields.add(new ElementListNCPFField(name, get, set));
    }
    private <DefinedField extends DefinedNCPFField> DefinedField getDefinedNCPFField(String name){
        for(DefinedNCPFField field : definedNCPFFields){
            if(name.equals(field.name))return (DefinedField)field;
        }
        return null;
    }
    protected <DefinedSuper extends DefinedNCPFModularObject, DefinedObject extends DefinedNCPFModularObject, DefinedElement extends NCPFElement> void definePlanneratorField(Class<DefinedSuper> superClazz, Class<DefinedObject> clazz, String name, Function<DefinedObject, List<DefinedElement>> get, Consumer<List<DefinedElement>> set, Supplier<DefinedElement> elementSupplier){
        definedPlanneratorFields.add(new ElementListPlanneratorField(getDefinedNCPFField(name), get, set, elementSupplier));
    }
    protected <DefinedModule extends NCPFModule> void definePlanneratorModule(Supplier<DefinedModule> get, Consumer<DefinedModule> set, Supplier<DefinedModule> moduleSupplier){
        definePlanneratorModule(true, get, set, moduleSupplier);
    }
    protected <DefinedModule extends NCPFModule> void definePlanneratorModule(boolean existsForAddons, Supplier<DefinedModule> get, Consumer<DefinedModule> set, Supplier<DefinedModule> moduleSupplier){
        definedPlanneratorModules.add(new DefinedPlanneratorModule(existsForAddons, get, set, moduleSupplier));
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
        definedNCPFFields.forEach((field) -> field.conglomerate(this, addon));
        definedPlanneratorFields.forEach((field) -> field.conglomerate(this, addon));
    }
    @Override
    public void setReferences(List<NCPFElement> lst, boolean soft){
        modules.setReferences(lst, soft);
        for(NCPFModule module : modules.modules.values())module.setLocalReferences(this);
    }
    public <T extends DefinedNCPFObject> List<T> getRecipes(Supplier<T> newCopy){
        List<T> list = new ArrayList<>();
        withModule(NCPFBlockRecipesModule::new, (blockRecipes) -> {
            copyList(blockRecipes.recipes, list, newCopy);
        });
        return list;
    }
    public void setRecipes(List<? extends DefinedNCPFObject>... recipes){
        boolean empty = true;
        for(List l : recipes)if(!l.isEmpty())empty = false;
        if(empty)return;//nothing to set
        withModuleOrCreate(NCPFBlockRecipesModule::new, (blockRecipes) -> {
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
