package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.ncpf.defined.field.DefinedNCPFField;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.configuration.MultiblockRecipeElement;
import net.ncplanner.plannerator.planner.ncpf.defined.field.DefinedPlanneratorField;
import net.ncplanner.plannerator.planner.ncpf.module.configuration.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.GlobalElementsModule;
import net.ncplanner.plannerator.planner.ncpf.module.NCPFSettingsModule;
public abstract class NCPFConfiguration extends DefinedNCPFModularObject{
    public final String name;
    public NCPFConfiguration(String name){
        this.name = name;
    }
    public void setReferences(boolean soft){
        List<NCPFElement>[] elements = getElements();
        for(List<NCPFElement> elems : elements){
            for(NCPFElement elem : elems){
                for(List<NCPFElement> lst : elements){
                    elem.setReferences(lst, soft);
                }
            }
        }
        withModule(GlobalElementsModule::new, (m) -> {
            for(List<NCPFElement> elems : elements){
                for(NCPFElement elem : elems){
                    elem.setReferences(m.elements, soft);
                }
            }
        });
    }
    public List<NCPFElement>[] getElements(){
        if(!definedPlanneratorFields.isEmpty()){
            ArrayList<List<NCPFElement>> lists = new ArrayList<>();
            for(DefinedPlanneratorField field : definedPlanneratorFields){
                List<NCPFElement> list = field.getElements(this);
                if(list!=null)lists.add(list);
            }
            return lists.toArray(List[]::new);
        }
        ArrayList<List<NCPFElement>> lists = new ArrayList<>();
        for(DefinedNCPFField field : definedNCPFFields){
            List<NCPFElement> list = field.getElements(this);
            if(list!=null)lists.add(list);
        }
        return lists.toArray(List[]::new);
    }
    /**
     * @return a list of elements, plus any global elements
     */
    public List<NCPFElement>[] getAllElements(){
        ArrayList<List<NCPFElement>> elements = new ArrayList<>(Arrays.asList(getElements()));
        withModule(GlobalElementsModule::new, (m) -> {
            elements.add(m.elements);
        });
        return elements.toArray(List[]::new);
    }
    /**
     * @return a list of elements, plus any global elements, PLUS any block
     * recipes
     */
    public List<NCPFElement>[] getAllElementsISaidAllElements(){
        ArrayList<List<NCPFElement>> elements = new ArrayList<>(Arrays.asList(getElements()));
        for(int i = 0; i<elements.size(); i++){
            List<NCPFElement> lst = elements.get(i);
            for(NCPFElement elem : lst){
                if(elem instanceof BlockRecipesElement){
                    BlockRecipesElement brelem = (BlockRecipesElement)elem;
                    List<? extends NCPFElement> recipes = brelem.getBlockRecipes();
                    if(recipes==null)continue;
                    elements.add(new ArrayList<>(recipes));
                }
            }
        }
        withModule(GlobalElementsModule::new, (m) -> {
            elements.add(m.elements);
        });
        return elements.toArray(List[]::new);
    }
    public Supplier<NCPFElement>[] getElementSuppliers(){
        if(!definedPlanneratorFields.isEmpty()){
            ArrayList<Supplier<NCPFElement>> suppliers = new ArrayList<>();
            for(DefinedPlanneratorField field : definedPlanneratorFields){
                Supplier<NCPFElement> supplier = field.getElementSupplier();
                if(supplier!=null)suppliers.add(supplier);
            }
            return suppliers.toArray(Supplier[]::new);
        }
        ArrayList<Supplier<NCPFElement>> suppliers = new ArrayList<>();
        for(DefinedNCPFField field : definedNCPFFields){
            Supplier<NCPFElement> supplier = field.getElementSupplier();
            if(supplier!=null)suppliers.add(supplier);
        }
        return suppliers.toArray(Supplier[]::new);
    }
    public <T extends NCPFElement> T getElement(NCPFElementDefinition definition){
        for(List<NCPFElement> elems : getElements()){
            for(NCPFElement elem : elems){
                if(elem.definition.matches(definition))return (T)elem;
            }
        }
        return null;
    }
    public List<NCPFElement>[] getMultiblockRecipes(){
        ArrayList<List<NCPFElement>> lists = new ArrayList<>();
        for(DefinedPlanneratorField field : definedPlanneratorFields){
            List<NCPFElement> list = field.getElements(this);
            if(list!=null&&(list.isEmpty()||list.get(0) instanceof MultiblockRecipeElement))lists.add(list);
        }
        return lists.toArray(List[]::new);
    }
    public void makePartial(List<Design> designs){
        definedPlanneratorFields.forEach(field -> field.makePartial(this, designs));
    }
    public abstract String getName();
    public void init(boolean isAddon){
        definedPlanneratorModules.forEach(module -> module.init(this, isAddon));
    }
    public String getNameAndVersion(){
        ConfigurationMetadataModule module = getModule(ConfigurationMetadataModule::new);
        if(module!=null&&module.name!=null)return module.name+" "+module.version;
        return "Unknown Configuration";
    }
    public void removeSettings(){
        // Remove all configuration settings modules
        for(Iterator<String> it = modules.modules.keySet().iterator(); it.hasNext();){
            if(modules.modules.get(it.next()) instanceof NCPFSettingsModule)it.remove();
        }
        
        definedPlanneratorModules.forEach((module) -> module.removeSettings());
    }
}
