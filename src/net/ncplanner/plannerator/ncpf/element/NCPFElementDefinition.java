package net.ncplanner.plannerator.ncpf.element;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
/**
 * A utility class for the plannerator; in NCPF, this is still part of NCPFElement
 * @author thiz
 */
public abstract class NCPFElementDefinition extends DefinedNCPFObject{
    public abstract boolean matches(NCPFElementDefinition definition);
}