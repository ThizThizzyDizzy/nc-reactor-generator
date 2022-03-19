package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.StageTransition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
public class LiteGenerator<T extends LiteMultiblock> implements ThingWithVariables{
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }, new VariableInt("Stage"){
        @Override
        public int getValue(){
            return stage;
        }
    }};
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
    @Override
    public int getVariableCount(){
        return vars.length;
    }
    @Override
    public Variable getVariable(int i){
        return vars[i];
    }
}