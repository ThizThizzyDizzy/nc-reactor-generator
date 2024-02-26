package net.ncplanner.plannerator.multiblock.generator.lite;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableInt;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.VariableLong;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.SettingString;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Parameter;
import net.ncplanner.plannerator.ncpf.DefinedNCPFObject;
import net.ncplanner.plannerator.ncpf.io.NCPFObject;
import net.ncplanner.plannerator.planner.Queue;
import net.ncplanner.plannerator.planner.gui.menu.MenuGenerator;
public class LiteGenerator<T extends LiteMultiblock> extends DefinedNCPFObject implements ThingWithSettings, ThingWithVariables{
    public SettingString name = new SettingString("Name", "Custom");
    public ArrayList<Parameter> parameters = new ArrayList<>();
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
    }, new VariableInt("Stored Multiblocks"){
        @Override
        public int getValue(){
            return storedMultiblocks.size();
        }
    }};
    public ArrayList<GeneratorStage<T>> stages = new ArrayList<>();
    public int stage = 0;
    public long hits;
    public long lastUpdate = 0;
    public Queue<T> storedMultiblocks = new Queue<>();
    public Queue<Long> timestamps = new Queue<>();
    private final Object stageTransitioner = new Object();
    private boolean expanded = true;
    public LiteGenerator(){}
    public LiteGenerator(String name){
        this.name.set(name);
    }
    public void run(T multiblock, Random rand, T original, T priorityMultiblock, Consumer<T> onUpgrade, Consumer<T> onStore, Runnable onExit, Runnable onConsolidate){
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
        int st = this.stage;
        synchronized(stageTransitioner){
            if(st!=stage)return;
            TRANSITION:for(StageTransition<T> transition : currentStage.stageTransitions){
                for(Condition condition : transition.conditions){
                    condition.hits++;
                    if(!condition.check(rand))continue TRANSITION;
                }
                transition.hits++;
                lastUpdate = System.nanoTime();
                if(transition.store.get()){
                    T stored = (T)original.copy();
                    stored.copyVarsFrom(original);
                    storedMultiblocks.enqueue(stored);
                    onStore.accept(stored);
                }
                if(transition.consolidate.get()){
                    onConsolidate.run();
                    while(!storedMultiblocks.isEmpty()){
                        T mb = storedMultiblocks.dequeue();
                        if(mb==null)continue;
                        PRIORITY:for(Priority<T> priority : currentStage.priorities){
                            for(Condition condition : priority.conditions){
                                condition.hits++;
                                if(!condition.check(rand))continue PRIORITY;
                            }
                            priority.hits++;
                            float f;
                            synchronized(priorityMultiblock){
                                priorityMultiblock.copyVarsFrom(mb);
                                f = ((Number)priority.operator.get().get()).floatValue();
                            }
                            if(f>0){
                                onUpgrade.accept(mb);
                                lastUpdate = System.nanoTime();
                                break;
                            }
                            if(f<0)break;
                        }
                    }
                }
                if(transition.stop.get()){
                    onExit.run();
                }else{
                    stage = transition.targetStage.get();
                    stages.get(stage).hits = 0;
                }
                break;
            }
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
        while(!timestamps.isEmpty()&&timestamps.peek()<System.nanoTime()-1_000_000_000l){
            timestamps.dequeue();
        }
        return "Stage "+(stage+1)+" | "+hits+" Iterations | "+timestamps.size()+" per second";
    }
    @Override
    public int getSettingCount(){
        return parameters.size()+1;
    }
    @Override
    public Setting getSetting(int i){
        if(i==0)return name;
        return parameters.get(i-1);
    }
    public void reset(){
        hits = 0;
        lastUpdate = 0;
        stage = 0;
        for(GeneratorStage<T> stag : stages){
            stag.reset();
        }
    }
    @Override
    public void convertFromObject(NCPFObject ncpf){
        if(MenuGenerator.current==null)return;//being loaded as part of a file, but not as a generator
        MenuGenerator.current.generator = this;
        name.set(ncpf.getString("name"));
        parameters = ncpf.getRegisteredNCPFList("parameters", Parameter.registeredParameters);
        stages = new ArrayList<>();
        ncpf.getDefinedNCPFList("stages", stages, GeneratorStage<T>::new);
    }
    @Override
    public void convertToObject(NCPFObject ncpf){
        ncpf.setString("name", name.get());
        ncpf.setRegisteredNCPFList("parameters", parameters);
        ncpf.setDefinedNCPFList("stages", stages);
    }
    public void setIndicies(T multiblock){
        for(GeneratorStage<T> stage : stages)stage.setIndicies(multiblock);
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
        return "Generator";
    }
}