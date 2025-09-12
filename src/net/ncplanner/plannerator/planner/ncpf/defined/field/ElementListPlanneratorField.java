package net.ncplanner.plannerator.planner.ncpf.defined.field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.defined.field.ElementListNCPFField;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
import net.ncplanner.plannerator.planner.ncpf.configuration.BlockRecipesElement;
public class ElementListPlanneratorField<DefinedSuper extends DefinedNCPFModularObject, DefinedObject extends DefinedSuper, DefinedElement extends NCPFElement> extends DefinedPlanneratorField<DefinedSuper, DefinedObject, ElementListNCPFField<DefinedSuper>>{
    public final Function<DefinedObject, List<DefinedElement>> get;
    public final Consumer<List<DefinedElement>> set;
    public final Supplier<DefinedElement> elementSupplier;
    public ElementListPlanneratorField(ElementListNCPFField<DefinedSuper> field, Function<DefinedObject, List<DefinedElement>> get, Consumer<List<DefinedElement>> set, Supplier<DefinedElement> elementSupplier){
        super(field);
        this.get = get;
        this.set = set;
        this.elementSupplier = elementSupplier;
    }
    @Override
    public void convertFromObject(DefinedObject defined, NCPFObject ncpf){
        set.accept(defined.copyList(field.get.apply(defined), elementSupplier));
    }
    @Override
    public void convertToObject(DefinedObject defined, NCPFObject ncpf){
        field.set.accept(defined.copyList(get.apply(defined), NCPFElement::new));
    }
    @Override
    public void conglomerate(DefinedObject defined, DefinedObject addon){
        defined.conglomerateElementList(get.apply(defined), get.apply(addon));
    }
    @Override
    public void makePartial(DefinedObject defined, List<Design> designs){
        List<DefinedElement> lst = get.apply(defined);
        defined.makePartial(lst, designs);
        if(!lst.isEmpty()&&lst.get(0) instanceof BlockRecipesElement){
            lst.forEach((elem) -> ((BlockRecipesElement)elem).makePartial(designs));
        }
        get.apply(defined).forEach((t) -> {
            if(t instanceof BlockRecipesElement){
                ((BlockRecipesElement)t).makePartial(designs);
            }
        });
    }
    @Override
    public List<DefinedElement> getElements(DefinedObject defined){
        return get.apply(defined);
    }
    @Override
    public Supplier<DefinedElement> getElementSupplier(){
        return elementSupplier;
    }
}
