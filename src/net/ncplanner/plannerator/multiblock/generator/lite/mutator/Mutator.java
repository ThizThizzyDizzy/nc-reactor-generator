package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.ncpf.RegisteredNCPFObject;
public abstract class Mutator<T extends LiteMultiblock> extends RegisteredNCPFObject implements ThingWithSettings{
    public static HashMap<String, Supplier<Mutator>> registeredMutators = new HashMap<>();
    public static <T extends LiteMultiblock> HashMap<String, Supplier<Mutator<T>>> getRegisteredMutators(){
        HashMap<String, Supplier<Mutator<T>>> map = new HashMap<>();
        for(String key : registeredMutators.keySet()){
            map.put(key, () -> {
                return registeredMutators.get(key).get();
            });
        }
        for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
            String next = it.next();
            try{
                Supplier<Mutator<T>> get = map.get(next);
            }catch(ClassCastException ex){
                it.remove();//hacky workaround to remove invalid elements for this multiblock
            }
        }
        return map;
    }
    public Mutator(String name){
        super(name);
    }
    public abstract String getTitle();
    public abstract String getTooltip();
    public abstract void run(T multiblock, Random rand);
    public void setIndicies(T multiblock){}
}