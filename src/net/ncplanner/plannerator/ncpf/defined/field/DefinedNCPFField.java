package net.ncplanner.plannerator.ncpf.defined.field;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class DefinedNCPFField<DefinedObject extends DefinedNCPFModularObject>{
    public final String name;
    public DefinedNCPFField(String name){
        this.name = name;
    }
    public abstract void convertFromObject(DefinedObject defined, NCPFObject ncpf);
    public abstract void convertToObject(DefinedObject defined, NCPFObject ncpf);
    public abstract void conglomerate(DefinedObject defined, DefinedObject addon);
    public List<NCPFElement> getElements(DefinedObject defined){
        return null;
    }
    public Supplier<NCPFElement> getElementSupplier(){
        return null;
    }
}
