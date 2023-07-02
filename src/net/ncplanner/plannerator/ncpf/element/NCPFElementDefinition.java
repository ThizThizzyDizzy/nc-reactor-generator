package net.ncplanner.plannerator.ncpf.element;
import java.util.List;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.NCPFElement;
/**
 * A utility class for the plannerator; in NCPF, this is still part of NCPFElement
 * @author thiz
 */
public abstract class NCPFElementDefinition extends DefinedNCPFObject{
    public abstract boolean matches(NCPFElementDefinition definition);
}