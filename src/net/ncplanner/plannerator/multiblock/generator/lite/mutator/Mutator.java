package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.ncpf.NCPFConfigurationContainer;
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
    private boolean expanded;
    public Mutator(String name){
        super(name);
    }
    public abstract String getTitle();
    public abstract String getTooltip();
    public abstract void run(T multiblock, Random rand);
    public void setIndicies(T multiblock){
    }
    /**
     * Initialize the mutator in the configuration menu. This method should do
     * nothing; It is here solely to ensure that it crashes when calling the
     * wrong mutator
     *
     * @param multiblock The multiblock to initialize with
     */
    public abstract void init(T multiblock);
    @Override
    public boolean isExpanded(){
        return expanded;
    }
    @Override
    public void setExpanded(boolean expanded){
        this.expanded = expanded;
    }
    @Override
    public String getSettingsPrefix(){
        return "Mutator";
    }
    public void importFrom(T multiblock, NCPFConfigurationContainer container){
    }
}
