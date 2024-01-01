package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Supplier;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithVariables;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.ncpf.RegisteredNCPFObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public abstract class GeneratorMutator<T extends LiteMultiblock> extends RegisteredNCPFObject implements ThingWithSettings, ThingWithVariables{
    public static final HashMap<String, Supplier<GeneratorMutator>> registeredMutators = new HashMap<>();
    public static <T extends LiteMultiblock> HashMap<String, Supplier<GeneratorMutator<T>>> getRegisteredMutators(){
        HashMap<String, Supplier<GeneratorMutator<T>>> map = new HashMap<>();
        for(String key : registeredMutators.keySet()){
            map.put(key, () -> {
                return registeredMutators.get(key).get();
            });
        }
        for(Iterator<String> it = map.keySet().iterator(); it.hasNext();){
            String next = it.next();
            try{
                Supplier<GeneratorMutator<T>> get = map.get(next);
            }catch(ClassCastException ex){
                it.remove();//hacky workaround to remove invalid elements for this multiblock
            }
        }
        return map;
    }
    public Variable<?>[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public long hits = 0;
    public Mutator<T> mutator;
    public ArrayList<Condition> conditions = new ArrayList<>();
    private boolean expanded;
    public GeneratorMutator(String name){
        super(name);
    }
    public abstract String getTitle();
    public abstract String getTooltip();
    public abstract void run(T multiblock, Random rand);
    @Override
    public int getVariableCount(){
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        return vars[i];
    }
    public void reset(){
        hits = 0;
        for(Condition condition : conditions){
            condition.reset();
        }
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        mutator = ncpf.getRegisteredNCPFObject("mutator", Mutator.registeredMutators);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setRegisteredNCPFObject("mutator", mutator);
    }
    public void setIndicies(T multiblock){
        mutator.setIndicies(multiblock);
    }
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
        return "Mutator Quantity";
    }
}