package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.StageTransition;
public class LiteGenerator<T extends LiteMultiblock>{
    public ArrayList<GeneratorStage<T>> stages = new ArrayList<>();
    public int stage = 0;
    public long hits;
    public void run(T multiblock, Random rand){
        hits++;
        GeneratorStage<T> currentStage = stages.get(stage);
        currentStage.run(multiblock, rand);
        TRANSITION:for(StageTransition<T> transition : currentStage.stageTransitions){
            for(Condition condition : transition.conditions){
                condition.hits++;
                if(!condition.check(rand))continue TRANSITION;
            }
            transition.hits++;
            stage = transition.targetStage.get();
            break;
        }
    }
}