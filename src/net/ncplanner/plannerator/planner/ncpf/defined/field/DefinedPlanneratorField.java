package net.ncplanner.plannerator.planner.ncpf.defined.field;
import java.util.List;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFModularObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
import net.ncplanner.plannerator.ncpf.defined.field.DefinedNCPFField;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.ncpf.Design;
public abstract class DefinedPlanneratorField<DefinedSuper extends DefinedNCPFModularObject, DefinedObject extends DefinedSuper, DefinedField extends DefinedNCPFField<DefinedSuper>>{
    public final DefinedField field;
    public DefinedPlanneratorField(DefinedField field){
        if(field==null)throw new IllegalArgumentException("field must not be null!");
        this.field = field;
    }
    public abstract void convertFromObject(DefinedObject defined, NCPFObject ncpf);
    public abstract void convertToObject(DefinedObject defined, NCPFObject ncpf);
    public abstract void conglomerate(DefinedObject defined, DefinedObject addon);
    public void makePartial(DefinedObject defined, List<Design> designs){
    }
    public List<? extends NCPFElement> getElements(DefinedObject defined){
        return null;
    }
    public Supplier<? extends NCPFElement> getElementSupplier(){
        return null;
    }
}
