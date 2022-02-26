package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.GeneratorStage;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.StageTransition;
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
    private final T multiblock;
    private final Button done = add(new Button(0, 0, 0, 48, "Done", true, true)).setTooltip("Finish generation and return to the editor");
    private final Button prevStage = add(new Button(0, 0, 48, 48, "<", true, true)).setTooltip("Previous stage");
    private final Button nextStage = add(new Button(0, 0, 48, 48, ">", true, true)).setTooltip("Next stage");
    private final Button addStage = add(new Button(0, 0, 0, 48, "Add Stage", true, true)).setTooltip("Add a generation stage");
    private final Button delStage = add(new Button(0, 0, 0, 48, "Delete Stage (Hold Shift)", false, true)).setTooltip("Delete this generation stage");
    private final SingleColumnList stageSettings = add(new SingleColumnList(0, 48, 0, 0, 24));
    private LiteGenerator<T> generator;
    private float setScrollTo = -1;
    public MenuGenerator(GUI gui, MenuEdit editor, Multiblock<Block> multiblock){
        super(gui, editor);
        this.multiblock = multiblock.compile();
        generator = new LiteGenerator<>();
        generator.stages.add(new GeneratorStage<>());
        done.addAction(() -> {
            gui.open(new MenuTransition(gui, this, editor, MenuTransition.SlideTransition.slideTo(0, 1), 5));
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
        delStage.enabled = generator.stages.size()>1&&Core.isShiftPressed();
        stageSettings.width = delStage.width = done.width = gui.getWidth()/4;
        stageSettings.x = delStage.x = prevStage.x = gui.getWidth()*3/4;
        nextStage.x = gui.getWidth()-nextStage.width;
        addStage.x = prevStage.x+prevStage.width;
        addStage.width = nextStage.x-addStage.x;
        delStage.y = gui.getHeight()-delStage.height;
        stageSettings.height = delStage.y-stageSettings.y;
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
}