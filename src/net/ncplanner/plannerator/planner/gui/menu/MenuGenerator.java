package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.Random;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.GenerationThread;
import net.ncplanner.plannerator.multiblock.generator.lite.GeneratorStage;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.StageTransition;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickCondition;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickGeneratorMutator;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickMutator;
public class MenuGenerator<T extends LiteMultiblock> extends Menu{
    public final T multiblock;
    private final Button done = add(new Button(0, 0, 0, 48, "Done", true, true)).setTooltip("Finish generation and return to the editor");
    private final Button prevStage = add(new Button(0, 0, 48, 48, "<", true, true)).setTooltip("Previous stage");
    private final Button nextStage = add(new Button(0, 0, 48, 48, ">", true, true)).setTooltip("Next stage");
    private final Button addStage = add(new Button(0, 0, 0, 48, "Add Stage", true, true)).setTooltip("Add a generation stage");
    private final Button addThread = add(new Button(0, 0, 64, 48, "+", true, true)).setTooltip("Add Thread");
    private final Button remThread = add(new Button(0, 0, 64, 48, "-", true, true)).setTooltip("Remove Thread");
    private final Button start = add(new Button(0, 0, 0, 48, "Start", true, true)).setTooltip("Start/Stop generation");
    private final Button delStage = add(new Button(0, 0, 0, 48, "Delete Stage (Hold Shift)", false, true)).setTooltip("Delete this generation stage");
    private final SingleColumnList stageSettings = add(new SingleColumnList(0, 48, 0, 0, 24));
    public final LiteGenerator<T> generator;
    private float setScrollTo = -1;
    private int threads = 1;
    private boolean running;
    public MenuGenerator(GUI gui, MenuEdit editor, Multiblock<Block> multiblock){
        super(gui, editor);
        this.multiblock = multiblock.compile();
        generator = new LiteGenerator<>();
        generator.stages.add(new GeneratorStage<>());
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, editor, MenuTransition.SlideTransition.slideTo(0, 1), 5));
        });
        addThread.addAction(() -> {
            threads++;
        });
        remThread.addAction(() -> {
            threads--;
        });
        start.addAction(() -> {
            running = !running;
            if(running)start();
        });
        prevStage.addAction(() -> {
            if(generator.stage>0)generator.stage--;
            rebuildGUI();
        });
        nextStage.addAction(() -> {
            if(generator.stage<generator.stages.size()-1)generator.stage++;
            rebuildGUI();
        });
        addStage.addAction(() -> {
            generator.stages.add(new GeneratorStage<>());
            generator.stage = generator.stages.size()-1;
            rebuildGUI();
        });
        delStage.addAction(() -> {
            for(GeneratorStage<T> stage : generator.stages){
                for(StageTransition<T> transition : stage.stageTransitions){
                    if(transition.targetStage.get()>generator.stage)transition.targetStage.set(transition.targetStage.get()-1);
                    if(transition.targetStage.get()==generator.stage)transition.targetStage.set(-1);
                }
            }
            generator.stages.remove(generator.stage);
            if(generator.stage>generator.stages.size()-1)generator.stage--;
            rebuildGUI();
        });
        rebuildGUI();
    }
    public void rebuildGUI(){
        prevStage.enabled = generator.stage>0;
        nextStage.enabled = generator.stage<generator.stages.size()-1;
        setScrollTo = stageSettings.scrollY;
        stageSettings.components.clear();
        stageSettings.add(new Label(0, 0, 0, 40, "Stage "+(generator.stage+1)));
        GeneratorStage<T> stage = generator.stages.get(generator.stage);
        stageSettings.add(new Label(0, 0, 0, 36, "Mutators", true));
        for(GeneratorMutator<T> mutator : stage.steps){
            stageSettings.add(new Label(0, 0, 0, 32, mutator.getTitle()){
                Button del = add(new Button(0, 0, height, height, "X", true, true).addAction(() -> {
                    stage.steps.remove(mutator);
                    rebuildGUI();
                }));
                @Override
                public void draw(double deltaTime){
                    del.x = width-del.width;
                    super.draw(deltaTime);
                }
            }.setTooltip(mutator.getTooltip()));
            addConditionSettings(mutator.conditions);
            addSettings(mutator);
            stageSettings.add(new Label(0, 0, 0, 30, mutator.mutator.getTitle()).setTooltip(mutator.mutator.getTooltip()));
            addSettings(mutator.mutator);
        }
        stageSettings.add(new Button(0, 0, 0, 32, "Add Mutator", true).addAction(() -> {
            new MenuPickMutator<>(gui, this, multiblock, (mutator)->{
                new MenuPickGeneratorMutator<>(gui, this, mutator, (genMutator)->{
                    stage.steps.add(genMutator);
                    rebuildGUI();
                }).open();
            }).open();
        }));
        stageSettings.add(new Label(0, 0, 0, 36, "Stage Transitions", true));
        for(int i = 0; i<stage.stageTransitions.size(); i++){
            StageTransition<T> transition = stage.stageTransitions.get(i);
            stageSettings.add(new Label(0, 0, 0, 32, "Transition "+(i+1)){
                Button del = add(new Button(0, 0, height, height, "X", true, true).addAction(() -> {
                    stage.stageTransitions.remove(transition);
                    rebuildGUI();
                }));
                @Override
                public void draw(double deltaTime){
                    del.x = width-del.width;
                    super.draw(deltaTime);
                }
            });
            transition.targetStage.addSettings(stageSettings, this);
            addConditionSettings(transition.conditions);
        }
        stageSettings.add(new Button(0, 0, 0, 32, "Add Transition", true).addAction(() -> {
            stage.stageTransitions.add(new StageTransition<>());
            rebuildGUI();
        }));
    }
    @Override
    public void render2d(double deltaTime){
        if(this.multiblock==null){
            gui.open(parent);
            return;
        }
        remThread.enabled = threads>1;
        start.text = (running?"Stop":"Start")+" "+threads+" Thread"+(threads==1?"":"s");
        delStage.enabled = generator.stages.size()>1&&Core.isShiftPressed();
        stageSettings.width = delStage.width = done.width = gui.getWidth()/4;
        stageSettings.x = delStage.x = prevStage.x = gui.getWidth()*3/4;
        nextStage.x = gui.getWidth()-nextStage.width;
        addStage.x = prevStage.x+prevStage.width;
        addStage.width = nextStage.x-addStage.x;
        delStage.y = gui.getHeight()-delStage.height;
        stageSettings.height = delStage.y-stageSettings.y;
        remThread.x = done.width;
        addThread.x = prevStage.x-addThread.width;
        start.x = remThread.x+remThread.width;
        start.width = addThread.x-start.x;
        super.render2d(deltaTime);
        if(setScrollTo>=0){
            stageSettings.scrollY = setScrollTo;
            setScrollTo = -1;
        }
    }
    public void addConditionSettings(ArrayList<Condition> conditions){
        stageSettings.add(new Label(0, 0, 0, 28, "Conditions", true));
        for(Condition condition : conditions){
            stageSettings.add(new Label(0, 0, 0, 24, condition.getTitle()){
                Button del = add(new Button(0, 0, height, height, "X", true, true).addAction(() -> {
                    conditions.remove(condition);
                    rebuildGUI();
                }));
                @Override
                public void draw(double deltaTime){
                    del.x = width-del.width;
                    super.draw(deltaTime);
                }
            }.setTooltip(condition.getTooltip()));
            addSettings(condition);
        }
        stageSettings.add(new Button(0, 0, 0, 28, "Add Condition", true).addAction(() -> {
            new MenuPickCondition(gui, this, (condition) -> {
                conditions.add(condition);
                rebuildGUI();
            }).open();
        }));
    }
    public void addSettings(ThingWithSettings thing){
        stageSettings.add(new Label(0, 0, 0, 28, "Settings", true));
        for(int i = 0; i<thing.getSettingCount(); i++){
            Setting setting = thing.getSetting(i);
            setting.addSettings(stageSettings, this);
        }
    }
    public void getAllVariables(ArrayList<Variable> vars, ArrayList<String> names){
        for(int i = 0; i<multiblock.getVariableCount(); i++){
            Variable v = multiblock.getVariable(i);
            vars.add(v);names.add("multiblock."+v.getName());
        }
        for(int i = 0; i<generator.getVariableCount(); i++){
            Variable v = generator.getVariable(i);
            vars.add(v);names.add("generator."+v.getName());
        }
        for(int stageIdx = 0; stageIdx<generator.stages.size(); stageIdx++){
            GeneratorStage<T> stage = generator.stages.get(stageIdx);
            for(int i = 0; i<stage.getVariableCount(); i++){
                Variable v = stage.getVariable(i);
                vars.add(v);names.add("generator.stages["+i+"]{Stage "+(stageIdx+1)+"}."+v.getName());
            }
            for(int stepIdx = 0; stepIdx<stage.steps.size(); stepIdx++){
                GeneratorMutator<T> step = stage.steps.get(stepIdx);
                for(int i = 0; i<step.getVariableCount(); i++){
                    Variable v = step.getVariable(i);
                    vars.add(v);names.add("generator.stages["+i+"]{Stage "+(stageIdx+1)+"}.steps["+stepIdx+"]{Step "+(stepIdx+1)+" ("+step.getTitle()+")}."+v.getName());
                }
                for(int i = 0; i<step.conditions.size(); i++){
                    Condition condition = step.conditions.get(i);
                    condition.getAllVariables(vars, names, "generator.stages["+i+"]{Stage "+(stageIdx+1)+"}.steps["+stepIdx+"]{Step "+(stepIdx+1)+" ("+step.getTitle()+")}.conditions["+i+"]{Condition "+(i+1)+" ("+step.conditions.get(i).getTitle()+")}");
                }
            }
            for(int transitionIdx = 0; transitionIdx<stage.stageTransitions.size(); transitionIdx++){
                StageTransition<T> transition = stage.stageTransitions.get(transitionIdx);
                for(int i = 0; i<transition.getVariableCount(); i++){
                    Variable v = transition.getVariable(i);
                    vars.add(v);names.add("generator.stages["+i+"]{Stage "+(stageIdx+1)+"}.transitions["+(transitionIdx+1)+"]{Transition "+(transitionIdx+1)+"}."+v.getName());
                }
                for(int i = 0; i<transition.conditions.size(); i++){
                    Condition condition = transition.conditions.get(i);
                    condition.getAllVariables(vars, names, "generator.stages["+i+"]{Stage "+(stageIdx+1)+"}.transitions["+(transitionIdx+1)+"]{Transition "+(transitionIdx+1)+"}.conditions["+i+"]{Condition "+(i+1)+" ("+transition.conditions.get(i).getTitle()+")}");
                }
            }
        }
    }
    private ArrayList<GenerationThread> generationThreads = new ArrayList<>();
    private void start(){
        Thread thread = new Thread(() -> {
            while(running){
                try{
                    if(generationThreads.size()>threads){
                        generationThreads.get(generationThreads.size()-1).running = false;
                        generationThreads.remove(generationThreads.size()-1);
                    }
                    if(generationThreads.size()<threads){
                        generationThreads.add(createGenerationThread());
                    }
                    Thread.sleep(1);
                }catch(Exception ex){
                    for(GenerationThread t : generationThreads)t.running = false;
                    throw new RuntimeException(ex);
                }
            }
            for(GenerationThread t : generationThreads)t.running = false;
        }, "Generator Thread Manager");
        thread.setDaemon(true);
        thread.start();
    }
    private GenerationThread createGenerationThread(){
        Random rand = new Random();
        return new GenerationThread(() -> {
            T mb = (T)multiblock.copy();
            GeneratorStage<T> stage = generator.stages.get(generator.stage);
            stage.run(mb, rand);
            mb.calculate();
            System.out.println(mb.getTooltip());
            STEP:for(StageTransition<T> transition : stage.stageTransitions){
                for(Condition c : transition.conditions){
                    c.hits++;
                    if(!c.check(rand))continue STEP;
                }
                transition.hits++;
                generator.stage = transition.targetStage.get();
            }
        });
    }
}