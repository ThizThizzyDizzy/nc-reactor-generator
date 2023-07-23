package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
public class GeneratorStage<T extends LiteMultiblock> extends DefinedNCPFObject implements ThingWithVariables{
    public Variable[] vars = new Variable[]{new VariableLong("Hits"){
        @Override
        public long getValue(){
            return hits;
        }
    }};
    public long hits = 0;
    public ArrayList<GeneratorMutator<T>> steps = new ArrayList<>();
    public ArrayList<StageTransition<T>> stageTransitions = new ArrayList<>();
    public ArrayList<Priority<T>> priorities = new ArrayList<>();
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
        for(GeneratorMutator<T> step : steps){
            step.reset();
        }
        for(StageTransition<T> transition : stageTransitions){
            transition.reset();
        }
        for(Priority<T> priority : priorities){
            priority.reset();
        }
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        steps = ncpf.getRegisteredNCPFList("steps", GeneratorMutator.getRegisteredMutators());
        stageTransitions = ncpf.getDefinedNCPFList("stage_transitions", StageTransition<T>::new);
        priorities = ncpf.getDefinedNCPFList("priorities", Priority<T>::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setRegisteredNCPFList("steps", steps);
        ncpf.setDefinedNCPFList("stage_transitions", stageTransitions);
        ncpf.setDefinedNCPFList("priorities", priorities);
    }
    public void setIndicies(T multiblock){
        for(GeneratorMutator<T> step : steps)step.setIndicies(multiblock);
    }
}