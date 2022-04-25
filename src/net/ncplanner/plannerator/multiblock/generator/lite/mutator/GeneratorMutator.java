package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithVariables;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
public abstract class GeneratorMutator<T extends LiteMultiblock> implements ThingWithSettings, ThingWithVariables{
    public static final ArrayList<Function<Mutator, GeneratorMutator>> mutators = new ArrayList<>();
    static{
        mutators.add(StandardMutator::new);
        mutators.add(RandomQuantityMutator::new);
    }
    public GeneratorMutator(Mutator<T> mutator){
        this.mutator = mutator;
    }
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public long hits = 0;
    public Mutator<T> mutator;
    public ArrayList<Condition> conditions = new ArrayList<>();
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
}