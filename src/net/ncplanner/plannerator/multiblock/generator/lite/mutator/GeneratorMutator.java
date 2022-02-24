package net.ncplanner.plannerator.multiblock.generator.lite.mutator;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
public abstract class GeneratorMutator<T extends LiteMultiblock>{
    public static final ArrayList<Function<Mutator, GeneratorMutator>> mutators = new ArrayList<>();
    static{
        mutators.add(PlainMutator::new);
        mutators.add(RandomQuantityMutator::new);
    }
    public GeneratorMutator(Mutator<T> mutator){
        this.mutator = mutator;
    }
    public long hits = 0;
    public Mutator<T> mutator;
    public ArrayList<Condition> conditions = new ArrayList<>();
    public abstract void run(T multiblock, Random rand);
}