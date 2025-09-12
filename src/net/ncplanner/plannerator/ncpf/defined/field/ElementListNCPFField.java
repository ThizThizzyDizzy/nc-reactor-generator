package net.ncplanner.plannerator.ncpf.defined.field;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class ElementListNCPFField<DefinedObject extends DefinedNCPFModularObject> extends DefinedNCPFField<DefinedObject>{
    public final Function<DefinedObject, List<NCPFElement>> get;
    public final Consumer<List<NCPFElement>> set;
    public ElementListNCPFField(String name, Function<DefinedObject, List<NCPFElement>> get, Consumer<List<NCPFElement>> set){
        super(name);
        this.get = get;
        this.set = set;
    }
    @Override
    public void convertFromObject(DefinedObject defined, NCPFObject ncpf){
        set.accept(ncpf.getDefinedNCPFList(name, NCPFElement::new));
    }
    @Override
    public void convertToObject(DefinedObject defined, NCPFObject ncpf){
        ncpf.setDefinedNCPFList(name, get.apply(defined));
    }
    @Override
    public void conglomerate(DefinedObject defined, DefinedObject addon){
        defined.conglomerateElementList(get.apply(defined), get.apply(addon));
    }
    @Override
    public List<NCPFElement> getElements(DefinedObject defined){
        return get.apply(defined);
    }
    @Override
    public Supplier<NCPFElement> getElementSupplier(){
        return NCPFElement::new;
    }
}
