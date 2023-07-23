package net.ncplanner.plannerator.multiblock.generator.lite.variable.setting;
import java.util.HashMap;
import java.util.function.Supplier;
import net.ncplanner.plannerator.ncpf.RegisteredNCPFObject;
//settings that you can add as generator parameters
public abstract class Parameter<T> extends RegisteredNCPFObject implements Setting<T>{
    public static HashMap<String, Supplier<Parameter>> registeredParameters = new HashMap<>();
    public String name;
    public Parameter(String type){
        super(type);
    }
}