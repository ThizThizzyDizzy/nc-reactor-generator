package net.ncplanner.plannerator.ncpf.configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.element.NCPFElementDefinition;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
import net.ncplanner.plannerator.planner.ncpf.module.ConfigurationMetadataModule;
import net.ncplanner.plannerator.planner.ncpf.module.GlobalElementsModule;
public abstract class NCPFConfiguration extends DefinedNCPFModularObject{
    public final String name;
    public NCPFConfiguration(String name){
        this.name = name;
    }
    public void setReferences(){
        List<NCPFElement>[] elements = getElements();
        for(List<NCPFElement> elems : elements){
            for(NCPFElement elem : elems){
                for(List<NCPFElement> lst : elements){
                    elem.setReferences(lst);
                }
            }
        }
        withModule(GlobalElementsModule::new, (m) -> {
            for(List<NCPFElement> elems : elements){
                for(NCPFElement elem : elems){
                    elem.setReferences(m.elements);
                }
            }
        });
    }
    public abstract List<NCPFElement>[] getElements();
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
        Supplier<NCPFElement>[] supps = new Supplier[getElements().length];
        for(int i = 0; i<supps.length; i++)supps[i] = NCPFElement::new;
        return supps;
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
        return new List[0];
    }
    public abstract void makePartial(List<Design> designs);
    public abstract String getName();
    public void init(boolean isAddon){
    }
    public String getNameAndVersion(){
        ConfigurationMetadataModule module = getModule(ConfigurationMetadataModule::new);
        if(module!=null&&module.name!=null)return module.name+" "+module.version;
        return "Unknown Configuration";
    }
    public void removeSettings(){
    }
}
