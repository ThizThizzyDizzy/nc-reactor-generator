package net.ncplanner.plannerator.ncpf.element;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
/**
 * A utility class for the plannerator; in NCPF, this is still part of NCPFElement
 * @author thiz
 */
public abstract class NCPFElementDefinition extends DefinedNCPFObject{
    public final String type;
    public NCPFElementDefinition(String type){
        this.type = type;
    }
    public abstract boolean matches(NCPFElementDefinition definition);
    public boolean typeMatches(Supplier<NCPFElementDefinition> match){
        return type.equals(match.get().type);
    }
}