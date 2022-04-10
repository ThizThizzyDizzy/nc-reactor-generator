package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.planner.Queue;
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
    }, new VariableLong("Last Update Nanos"){
        @Override
        public long getValue(){
            return (System.nanoTime()-lastUpdate)/1_000_000;
        }
    }};
    public ArrayList<GeneratorStage<T>> stages = new ArrayList<>();
    public int stage = 0;
    public long hits;
    public long lastUpdate = 0;
    public Queue<Long> timestamps = new Queue<>();
    public void run(T multiblock, Random rand, T original, T priorityMultiblock, Consumer<T> onUpgrade){
        hits++;
        timestamps.enqueue(System.nanoTime());
        GeneratorStage<T> currentStage = stages.get(stage);
        currentStage.run(multiblock, rand);
        multiblock.calculate();
        PRIORITY:for(Priority<T> priority : currentStage.priorities){
            for(Condition condition : priority.conditions){
                condition.hits++;
                if(!condition.check(rand))continue PRIORITY;
            }
            priority.hits++;
            float f;
            synchronized(priorityMultiblock){
                priorityMultiblock.copyVarsFrom(multiblock);
                f = ((Number)priority.operator.get().get()).floatValue();
            }
            if(f>0){
                onUpgrade.accept(multiblock);
                lastUpdate = System.nanoTime();
                break;
            }
            if(f<0)break;
        }
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
    public String getStatus(){
        while(!timestamps.isEmpty()&&timestamps.peek()<System.nanoTime()-10_000_000_000l){
            timestamps.dequeue();
        }
        return "Stage "+(stage+1)+" | "+hits+" Iterations | "+timestamps.size()/10+" per second";
    }
}