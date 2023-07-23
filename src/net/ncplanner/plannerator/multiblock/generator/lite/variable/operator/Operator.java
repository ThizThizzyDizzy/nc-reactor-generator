package net.ncplanner.plannerator.multiblock.generator.lite.variable.operator;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public interface Operator extends ThingWithSettings{
    public static final HashMap<String, Supplier<Operator>> registeredOperators = new HashMap<>();
    public String getType();
    public void convertToObject(NCPFObject ncpf);
    public void convertFromObject(NCPFObject ncpf);
}