package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.StageTransition;
public class GeneratorStage<T extends LiteMultiblock>{
    public long hits = 0;
    public ArrayList<GeneratorMutator<T>> steps = new ArrayList<>();
    public ArrayList<StageTransition<T>> stageMutators = new ArrayList<>();
    public void run(T multiblock, Random rand){
        hits++;
        STEP:for(GeneratorMutator<T> mutator : steps){
            for(Condition c : mutator.conditions){
                c.hits++;
                if(!c.check(rand))continue STEP;
            }
            mutator.hits++;
            mutator.run(multiblock, rand);
        }
    }
}