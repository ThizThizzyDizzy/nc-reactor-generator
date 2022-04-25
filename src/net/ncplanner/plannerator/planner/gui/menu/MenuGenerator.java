package net.ncplanner.plannerator.planner.gui.menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.GenerationThread;
import net.ncplanner.plannerator.multiblock.generator.lite.GeneratorStage;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteGenerator;
import net.ncplanner.plannerator.multiblock.generator.lite.LiteMultiblock;
import net.ncplanner.plannerator.multiblock.generator.lite.Priority;
import net.ncplanner.plannerator.multiblock.generator.lite.StageTransition;
import net.ncplanner.plannerator.multiblock.generator.lite.ThingWithSettings;
import net.ncplanner.plannerator.multiblock.generator.lite.anim.Animation;
import net.ncplanner.plannerator.multiblock.generator.lite.anim.BlankAnimation;
import net.ncplanner.plannerator.multiblock.generator.lite.anim.LayerSplitAnimation;
import net.ncplanner.plannerator.multiblock.generator.lite.anim.SpinAnimation;
import net.ncplanner.plannerator.multiblock.generator.lite.condition.Condition;
import net.ncplanner.plannerator.multiblock.generator.lite.mutator.GeneratorMutator;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.Variable;
import net.ncplanner.plannerator.multiblock.generator.lite.variable.setting.Setting;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.gui.GUI;
import net.ncplanner.plannerator.planner.gui.Menu;
import net.ncplanner.plannerator.planner.gui.menu.component.Button;
import net.ncplanner.plannerator.planner.gui.menu.component.Label;
import net.ncplanner.plannerator.planner.gui.menu.component.SingleColumnList;
import net.ncplanner.plannerator.planner.gui.menu.component.TextView;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickCondition;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickGeneratorMutator;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickMutator;
import net.ncplanner.plannerator.planner.gui.menu.dialog.MenuPickVariable;
import org.joml.Matrix4f;
public class MenuGenerator<T extends LiteMultiblock> extends Menu{
    public final T multiblock;
    public final T priorityMultiblock;
    private final Button done = add(new Button(0, 0, 0, 48, "Done", true, true)).setTooltip("Finish generation and return to the editor");
    private final Button prevStage = add(new Button(0, 0, 48, 48, "<", true, true)).setTooltip("Previous stage");
    private final Button nextStage = add(new Button(0, 0, 48, 48, ">", true, true)).setTooltip("Next stage");
    private final Button addStage = add(new Button(0, 0, 0, 48, "Add Stage", true, true)).setTooltip("Add a generation stage");
    private final Button addThread = add(new Button(0, 0, 64, 48, "+", true, true)).setTooltip("Add Thread");
    private final Button remThread = add(new Button(0, 0, 64, 48, "-", true, true)).setTooltip("Remove Thread");
    private final Button start = add(new Button(0, 0, 0, 48, "Start", true, true)).setTooltip("Start/Stop generation");
    private final Button delStage = add(new Button(0, 0, 0, 48, "Delete Stage (Hold Shift)", false, true)).setTooltip("Delete this generation stage");
    private final TextView textView = add(new TextView(0, 48, 0, 0, 24, 24));
    private final SingleColumnList stageSettings = add(new SingleColumnList(0, 48, 0, 0, 24));
    public LiteGenerator<T>[] gens;
    public LiteGenerator<T> generator;
    private boolean customizing = false;
    private float setScrollTo = -1;
    private int threads = 1;
    private int currentStage;
    private boolean running;
    private final Animation blank = new BlankAnimation();
    private Animation anim = blank;
    private boolean wasRunning;
    private HashMap<T, Animation> storeAnims = new HashMap<>();
    public MenuGenerator(GUI gui, MenuEdit editor, Multiblock<Block> multiblock){
        super(gui, editor);
        this.multiblock = multiblock.compile();
        priorityMultiblock = (T)this.multiblock.copy();
        gens = this.multiblock.createGenerators(priorityMultiblock);
        generator = gens[0];
        if(generator.stages.isEmpty())generator.stages.add(new GeneratorStage<>());
        done.addAction(() -> {
            running = false;
            gui.open(new MenuTransition(gui, this, new MenuEdit(gui, editor.parent, this.multiblock.export(editor.multiblock.configuration)), MenuTransition.SlideTransition.slideTo(0, 1), 5));
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
            if(customizing){
                if(currentStage>0)currentStage--;
                rebuildGUI();
            }else{
                if(generator.stage>0)generator.stage--;
            }
        });
        nextStage.addAction(() -> {
            if(customizing){
                if(currentStage<generator.stages.size()-1)currentStage++;
                rebuildGUI();
            }else{
                if(generator.stage<generator.stages.size()-1)generator.stage++;
            }
        });
        addStage.addAction(() -> {
            if(customizing){
                generator.stages.add(new GeneratorStage<>());
                currentStage = generator.stages.size()-1;
                rebuildGUI();
            }else generator.stage = 0;
        });
        delStage.addAction(() -> {
            for(GeneratorStage<T> stage : generator.stages){
                for(StageTransition<T> transition : stage.stageTransitions){
                    if(transition.targetStage.get()>currentStage)transition.targetStage.set(transition.targetStage.get()-1);
                    if(transition.targetStage.get()==currentStage)transition.targetStage.set(-1);
                }
            }
            generator.stages.remove(currentStage);
            if(currentStage>generator.stages.size()-1)currentStage--;
            rebuildGUI();
        });
        rebuildGUI();
    }
    public void rebuildGUI(){
        setScrollTo = stageSettings.scrollY;
        stageSettings.components.clear();
        if(!customizing){
            stageSettings.add(new Label(0, 0, 0, 40, "Generator Presets", true));
            for(int i = 0; i<gens.length; i++){
                LiteGenerator<T> gen = gens[i];
                final int ii = i;
                stageSettings.add(new Label(0, 0, 0, 36, ""){
                    Button reset = add(new Button(0, 0, height*2.5f, height, "Reset", true, true).addAction(() -> {
                        boolean flag = generator==gens[ii];
                        gens[ii] = MenuGenerator.this.multiblock.createGenerators(priorityMultiblock)[ii];
                        if(flag)generator = gens[ii];
                        rebuildGUI();
                    }));
                    Button select = add(new Button(0, 0, height*2.5f, height, gen.name.get(), true, false).addAction(() -> {
                        generator = gens[ii];
                        rebuildGUI();
                    }));
                    @Override
                    public void draw(double deltaTime){
                        select.width = reset.x = width-reset.width;
                        super.draw(deltaTime);
                    }
                });
            }
            stageSettings.add(new Label(0, 0, 0, 36, "Generator", true));
            addSettings(generator, 1);
            stageSettings.add(new Button(0, 0, 0, 32, "Customize", true).addAction(() -> {
                customizing = true;
                rebuildGUI();
            }));
        }else{
            stageSettings.add(new Button(0, 0, 0, 32, "Finish Customizing", true).addAction(() -> {
                customizing = false;
                rebuildGUI();
            }));
            generator.name.addSettings(stageSettings, this);
            stageSettings.add(new Label(0, 0, 0, 40, "Generator Variables", true));
            for(Setting setting : generator.settings){
                stageSettings.add(new Label(0, 0, 0, 36, setting.getName()){
                    Button del = add(new Button(0, 0, height, height, "X", true, true).addAction(() -> {
                        generator.settings.remove(setting);
                        rebuildGUI();
                    }));
                    @Override
                    public void draw(double deltaTime){
                        del.x = width-del.width;
                        super.draw(deltaTime);
                    }
                });
                setting.addSettings(stageSettings, this);
            }
            stageSettings.add(new Button(0, 0, 0, 36, "Add Variable", true).addAction(() -> {
                new MenuPickVariable<>(gui, this, multiblock, (var)->{
                    generator.settings.add(var);
                    rebuildGUI();
                }).open();
            }));
            stageSettings.add(new Label(0, 0, 0, 10, "", true));
            stageSettings.add(new Label(0, 0, 0, 40, "Stage "+(currentStage+1)));
            GeneratorStage<T> stage = generator.stages.get(currentStage);
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
            stageSettings.add(new Label(0, 0, 0, 36, "Priorities", true));
            for(int i = 0; i<stage.priorities.size(); i++){
                Priority<T> priority = stage.priorities.get(i);
                stageSettings.add(new Label(0, 0, 0, 32, "Priority "+(i+1)){
                    Button del = add(new Button(0, 0, height, height, "X", true, true).addAction(() -> {
                        stage.priorities.remove(priority);
                        rebuildGUI();
                    }));
                    @Override
                    public void draw(double deltaTime){
                        del.x = width-del.width;
                        super.draw(deltaTime);
                    }
                });
                addConditionSettings(priority.conditions);
                addSettings(priority);
            }
            stageSettings.add(new Button(0, 0, 0, 32, "Add Priority", true).addAction(() -> {
                stage.priorities.add(new Priority<>());
                rebuildGUI();
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
    }
    float rot = 0;
    private Animation nextAnim(float pos){
        Animation anim = blank;
        ArrayList<Animation> animations = new ArrayList<>();
        Random rand = new Random();
        if(wasRunning&&!running){
            wasRunning = running;
            return new SpinAnimation(4);
        }
        wasRunning = running;
        float duration = 0;
        int[] axes = new int[rand.nextInt(4)+3];
        for(int i = 0; i<axes.length; i++){
            axes[i] = rand.nextInt(6);
            int axis3 = axes[i]>2?axes[i]-3:axes[i];
            duration+=multiblock.getDimension(axis3)/(2f*axes.length);
        }
        animations.add(new LayerSplitAnimation(axes, duration, 1.3f));
        if(running)anim = animations.get(rand.nextInt(animations.size()));
        if(pos>anim.length)return nextAnim(pos-anim.length);
        anim.pos = pos;
        return anim;
    }
    @Override
    public void render2d(double deltaTime){
        if(this.multiblock==null){
            gui.open(parent);
            return;
        }
        prevStage.enabled = (customizing?currentStage:generator.stage)>0;
        nextStage.enabled = (customizing?currentStage:generator.stage)<generator.stages.size()-1;
        addStage.text = customizing?"Add Stage":"Reset";
        addStage.tooltip = customizing?"Add a generation stage":"Reset to stage 0";
        remThread.enabled = threads>1;
        start.text = (running?"Stop":"Start")+" "+threads+" Thread"+(threads==1?"":"s");
        delStage.enabled = generator.stages.size()>1&&Core.isShiftPressed();
        textView.width = stageSettings.width = delStage.width = done.width = gui.getWidth()/4;
        textView.height = gui.getHeight()-textView.y;
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
        Renderer renderer = new Renderer();
        renderer.setColor(Core.theme.getComponentTextColor(0));//TODO make this a status bar label instead
        renderer.drawText(textView.width, gui.getHeight()-20, stageSettings.x, gui.getHeight(), (running?"ACTIVE":"IDLE")+" | "+generator.getStatus()+" | "+generator.storedMultiblocks.size());
        if(setScrollTo>=0){
            stageSettings.scrollY = setScrollTo;
            setScrollTo = -1;
        }
    }
    @Override
    public void render3d(double deltaTime){
        anim.pos+=deltaTime;
        if(anim.pos>anim.length)anim = nextAnim(anim.pos-anim.length);
        rot+=deltaTime*20;
        Renderer renderer = new Renderer();
        int w = multiblock.getDimension(0);
        int h = multiblock.getDimension(1);
        int d = multiblock.getDimension(2);
        float size = Math.max(w, Math.max(h, d));
        renderer.pushModel(new Matrix4f().rotate((float)MathUtil.toRadians(rot+anim.getYRotOffset()), 0, 1, 0).scale(2f/size, 2f/size, 2f/size).translate(-multiblock.getDimension(0)/2f, -multiblock.getDimension(1)/2f, -multiblock.getDimension(2)/2f));
        for(int x = 0; x<w; x++){
            for(int y = 0; y<h; y++){
                for(int z = 0; z<d; z++){
                    Image tex = multiblock.getBlockTexture(x, y, z);
                    if(tex!=null)renderer.drawCube(
                            x+multiblock.getCubeBounds(x,y,z,0)+(float)anim.getCubeOffset(x,y,z,w,h,d,0), 
                            y+multiblock.getCubeBounds(x,y,z,1)+(float)anim.getCubeOffset(x,y,z,w,h,d,1), 
                            z+multiblock.getCubeBounds(x,y,z,2)+(float)anim.getCubeOffset(x,y,z,w,h,d,2), 
                            x+multiblock.getCubeBounds(x,y,z,3)+(float)anim.getCubeOffset(x,y,z,w,h,d,3), 
                            y+multiblock.getCubeBounds(x,y,z,4)+(float)anim.getCubeOffset(x,y,z,w,h,d,4), 
                            z+multiblock.getCubeBounds(x,y,z,5)+(float)anim.getCubeOffset(x,y,z,w,h,d,5), tex);
                }
            }
        }
        renderer.popModel();
        synchronized(storeAnims){
            for(Iterator<T> it = storeAnims.keySet().iterator(); it.hasNext();){
                T multiblock = it.next();
                Animation anim = storeAnims.get(multiblock);
                anim.pos+=deltaTime;
                if(running&&anim.pos>anim.length)anim.pos = anim.length;
                if(anim.pos>anim.length*2)it.remove();
                w = multiblock.getDimension(0);
                h = multiblock.getDimension(1);
                d = multiblock.getDimension(2);
                size = Math.max(w, Math.max(h, d));
                float scale = 1;
                if(anim.getPercent()>1)scale = 1-anim.getPercent()/4;
                renderer.pushModel(new Matrix4f().rotate((float)MathUtil.toRadians(rot+anim.getYRotOffset()), 0, 1, 0).scale(2f/size*scale, 2f/size*scale, 2f/size*scale).translate(-multiblock.getDimension(0)/2f, -multiblock.getDimension(1)/2f, -multiblock.getDimension(2)/2f));
                for(int x = 0; x<w; x++){
                    for(int y = 0; y<h; y++){
                        for(int z = 0; z<d; z++){
                            Image tex = multiblock.getBlockTexture(x, y, z);
                            if(tex!=null)renderer.drawCube(
                                    x+multiblock.getCubeBounds(x,y,z,0)+(float)anim.getCubeOffset(x,y,z,w,h,d,0),
                                    y+multiblock.getCubeBounds(x,y,z,1)+(float)anim.getCubeOffset(x,y,z,w,h,d,1),
                                    z+multiblock.getCubeBounds(x,y,z,2)+(float)anim.getCubeOffset(x,y,z,w,h,d,2),
                                    x+multiblock.getCubeBounds(x,y,z,3)+(float)anim.getCubeOffset(x,y,z,w,h,d,3),
                                    y+multiblock.getCubeBounds(x,y,z,4)+(float)anim.getCubeOffset(x,y,z,w,h,d,4), 
                                    z+multiblock.getCubeBounds(x,y,z,5)+(float)anim.getCubeOffset(x,y,z,w,h,d,5), tex);
                        }
                    }
                }
                renderer.popModel();
            }
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
        addSettings(thing, 0);
    }
    public void addSettings(ThingWithSettings thing, int trim){
        if(thing.getSettingCount()>trim)stageSettings.add(new Label(0, 0, 0, 28, "Settings", true));
        for(int i = trim; i<thing.getSettingCount(); i++){
            Setting setting = thing.getSetting(i);
            setting.addSettings(stageSettings, this);
        }
    }
    public void getAllVariables(ArrayList<Variable> vars, ArrayList<String> names){
        for(int i = 0; i<generator.getSettingCount(); i++){
            Setting s = generator.getSetting(i);
            vars.add(s);names.add("generator.settings."+s.getName());
        }
        for(int i = 0; i<multiblock.getVariableCount(); i++){
            Variable v = multiblock.getVariable(i);
            vars.add(v);names.add("multiblock."+v.getName());
        }
        for(int i = 0; i<priorityMultiblock.getVariableCount(); i++){
            Variable v = priorityMultiblock.getVariable(i);
            vars.add(v);names.add("multiblock2."+v.getName());
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
        generator.reset();
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
            generationThreads.clear();
        }, "Generator Thread Manager");
        thread.setDaemon(true);
        thread.start();
    }
    private GenerationThread createGenerationThread(){
        Random rand = new Random();
        return new GenerationThread(() -> {
            T mb = (T)multiblock.copy();
            generator.run(mb, rand, multiblock, priorityMultiblock, (t) -> {
                multiblock.copyFrom(t);
                multiblock.copyVarsFrom(t);
                textView.setText(multiblock.getTooltip());
            }, (t)->{
                synchronized(storeAnims){
                    storeAnims.put(t, new Animation(.75f){
                        float xOff = rand.nextFloat()*4-2;
                        float yOff = (rand.nextFloat()+1.5f)*(rand.nextBoolean()?1:-1);
                        float zOff = rand.nextFloat()*4-2;
                        @Override
                        public double getCubeOffset(int x, int y, int z, int w, int h, int d, int axis){
                            float percent = getPercent();
                            if(percent>1){
                                if(axis==0||axis==3)return (2-Math.min(2, percent))*mb.getDimension(0)*xOff;
                                if(axis==1||axis==4)return (2-Math.min(2, percent))*mb.getDimension(1)*yOff;
                                if(axis==2||axis==5)return (2-Math.min(2, percent))*mb.getDimension(2)*zOff;
                            }
                            if(axis==0||axis==3)return Math.min(1, percent)*mb.getDimension(0)*xOff;
                            if(axis==1||axis==4)return Math.min(1, percent)*mb.getDimension(1)*yOff;
                            if(axis==2||axis==5)return Math.min(1, percent)*mb.getDimension(2)*zOff;
                            return 0;
                        }
                        @Override
                        public double getYRotOffset(){
                            return 0;
                        }
                    });
                }
                multiblock.clear();
            }, ()->{
                running = false;
            });
        });
    }
}