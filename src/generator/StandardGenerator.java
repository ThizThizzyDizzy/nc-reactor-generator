package generator;
import java.util.ArrayList;
import multiblock.Multiblock;
import multiblock.ppe.PostProcessingEffect;
import multiblock.symmetry.Symmetry;
import planner.Core;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentPostProcessingEffect;
import planner.menu.component.MenuComponentPriority;
import planner.menu.component.MenuComponentSymmetry;
import planner.menu.component.MenuComponentToggle;
import simplelibrary.opengl.gui.components.MenuComponent;
public class StandardGenerator extends MultiblockGenerator{
    private MenuComponentMinimalistTextBox finalReactorCount;
    private MenuComponentMinimalistTextBox workingReactorCount;
    private MenuComponentMinimalistTextBox timeout;
    private MenuComponentMinimaList prioritiesList;
    private MenuComponentMinimalistButton moveUp;
    private MenuComponentMinimalistButton moveDown;
    private MenuComponentMinimaList symmetriesList;
    private MenuComponentMinimaList postProcessingEffectsList;
    private MenuComponentMinimalistTextBox changeChance;
    private MenuComponentToggle variableRate;
    private MenuComponentToggle lockCore;
    private GeneratorSettings settings = new GeneratorSettings();
    private ArrayList<Multiblock> finalReactors = new ArrayList<>();
    private ArrayList<Multiblock> workingReactors = new ArrayList<>();
    public StandardGenerator(Multiblock multiblock){
        super(multiblock);
    }
    @Override
    public ArrayList<Multiblock>[] getMultiblockLists(){
        return new ArrayList[]{(ArrayList)finalReactors.clone(),(ArrayList)workingReactors.clone()};
    }
    @Override
    public Multiblock[] getValidMultiblocks(){
        return Core.multiblockTypes.toArray(new Multiblock[Core.multiblockTypes.size()]);
    }
    @Override
    public String getName(){
        return "Standard";
    }
    @Override
    public void addSettings(MenuComponentMinimaList generatorSettings, Multiblock multi){
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Reactors", true));
        finalReactorCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "2", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Working Reactors", true));
        workingReactorCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Timeout (sec)", true));
        timeout = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "10", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Priorities", true));
        prioritiesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, priorities.size()*32, 24){
            @Override
            public void render(int millisSinceLastTick){
                for(MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
            }
        });
        refreshPriorities();
        MenuComponent priorityButtonHolder = generatorSettings.add(new MenuComponent(0, 0, 0, 32){
            @Override
            public void renderBackground(){
                components.get(1).x = width/2;
                components.get(0).width = components.get(1).width = width/2;
                components.get(0).height = components.get(1).height = height;
            }
            @Override
            public void render(){}
        });
        moveUp = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Up", true, true));
        moveUp.addActionListener((e) -> {
            int index = prioritiesList.getSelectedIndex();
            if(index==-1||index==0)return;
            priorities.add(index-1, priorities.remove(index));
            refreshPriorities();
            prioritiesList.setSelectedIndex(index-1);
        });
        moveDown = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Down", true, true));
        moveDown.addActionListener((e) -> {
            int index = prioritiesList.getSelectedIndex();
            if(index==-1||index==priorities.size()-1)return;
            priorities.add(index+1, priorities.remove(index));
            refreshPriorities();
            prioritiesList.setSelectedIndex(index+1);
        });
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Symmetry Settings", true));
        ArrayList<Symmetry> symmetries = multi.getSymmetries();
        symmetriesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, symmetries.size()*32, 24));
        for(Symmetry symmetry : symmetries){
            symmetriesList.add(new MenuComponentSymmetry(symmetry));
        }
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Post-Processing", true));
        ArrayList<PostProcessingEffect> postProcessingEffects = multi.getPostProcessingEffects();
        postProcessingEffectsList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, postProcessingEffects.size()*32, 24));
        for(PostProcessingEffect postProcessingEffect : postProcessingEffects){
            postProcessingEffectsList.add(new MenuComponentPostProcessingEffect(postProcessingEffect));
        }
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Other Settings", true));
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Change Chance", true));
        changeChance = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "1", true).setFloatFilter(0f, 100f).setSuffix("%"));
        variableRate = generatorSettings.add(new MenuComponentToggle(0, 0, 0, 32, "Variable Rate", true));
        lockCore = generatorSettings.add(new MenuComponentToggle(0, 0, 0, 32, "Lock Core", false));
    }
    @Override
    public MultiblockGenerator newInstance(Multiblock multi){
        return new StandardGenerator(multi);
    }
    private void refreshPriorities(){
        prioritiesList.components.clear();
        for(Priority priority : priorities){
            prioritiesList.add(new MenuComponentPriority(priority));
        }
    }
    @Override
    public void refreshSettings(){
        settings.refresh();
    }
    @Override
    public void tick(){
        int size;
        synchronized(workingReactors){
            size = workingReactors.size();
        }
        if(size<settings.workingReactors){
            Multiblock inst = multiblock.newInstance();
            synchronized(workingReactors){
                workingReactors.add(inst);
            }
            //todo TICK THOSE REACTORS
        }else if(size>settings.workingReactors){
            //TODO MERGE WITH FINAL!!!
            synchronized(workingReactors){
                workingReactors.remove(0);
            }
        }
        //TODO final reactors?
    }
    private class GeneratorSettings{
        public int finalReactors, workingReactors, timeout;
        public ArrayList<Symmetry> symmetries = new ArrayList<>();
        public ArrayList<PostProcessingEffect> postProcessingEffects = new ArrayList<>();
        public float changeChance;
        public boolean variableRate, lockCore;
        public void refresh(){
            finalReactors = Integer.parseInt(StandardGenerator.this.finalReactorCount.text);
            workingReactors = Integer.parseInt(StandardGenerator.this.workingReactorCount.text);
            timeout = Integer.parseInt(StandardGenerator.this.timeout.text);
            symmetries.clear();
            for(MenuComponent comp : symmetriesList.components){
                symmetries.add(((MenuComponentSymmetry)comp).symmetry);
            }
            postProcessingEffects.clear();
            for(MenuComponent comp : postProcessingEffectsList.components){
                postProcessingEffects.add(((MenuComponentPostProcessingEffect)comp).postProcessingEffect);
            }
            changeChance = Float.parseFloat(StandardGenerator.this.changeChance.text);
            variableRate = StandardGenerator.this.variableRate.enabled;
            lockCore = StandardGenerator.this.lockCore.enabled;
        }
    }
}