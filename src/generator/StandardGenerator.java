package generator;
import java.util.ArrayList;
import java.util.Iterator;
import multiblock.Block;
import multiblock.CuboidalMultiblock;
import multiblock.Multiblock;
import multiblock.Range;
import multiblock.action.PostProcessingAction;
import multiblock.action.SetblockAction;
import multiblock.action.SymmetryAction;
import multiblock.overhaul.fissionmsr.OverhaulMSR;
import multiblock.overhaul.fissionsfr.OverhaulSFR;
import multiblock.overhaul.turbine.OverhaulTurbine;
import multiblock.ppe.PostProcessingEffect;
import multiblock.symmetry.Symmetry;
import multiblock.underhaul.fissionsfr.UnderhaulSFR;
import planner.exception.MissingConfigurationEntryException;
import planner.menu.component.MenuComponentLabel;
import planner.menu.component.MenuComponentMinimaList;
import planner.menu.component.MenuComponentMinimalistButton;
import planner.menu.component.MenuComponentMinimalistTextBox;
import planner.menu.component.MenuComponentToggleBox;
import planner.menu.component.generator.MenuComponentPostProcessingEffect;
import planner.menu.component.generator.MenuComponentPriority;
import planner.menu.component.generator.MenuComponentSymmetry;
import simplelibrary.opengl.gui.components.MenuComponent;
public class StandardGenerator extends MultiblockGenerator{
//    private MenuComponentMinimalistTextBox finalMultiblockCount;
    MenuComponentMinimalistTextBox workingMultiblockCount;
    MenuComponentMinimalistTextBox timeout;
    MenuComponentMinimaList prioritiesList;
    MenuComponentMinimalistButton moveUp;
    MenuComponentMinimalistButton moveDown;
    MenuComponentMinimaList symmetriesList;
    MenuComponentMinimaList postProcessingEffectsList;
    MenuComponentMinimalistTextBox changeChance;
    MenuComponentToggleBox variableRate;
    MenuComponentToggleBox lockCore;
    MenuComponentToggleBox fillAir;
    private StandardGeneratorSettings settings = new StandardGeneratorSettings(this);
    private final ArrayList<Multiblock> finalMultiblocks = new ArrayList<>();
    private final ArrayList<Multiblock> workingMultiblocks = new ArrayList<>();
    private int index = 0;
    public StandardGenerator(Multiblock multiblock){
        super(multiblock);
    }
    @Override
    public ArrayList<Multiblock>[] getMultiblockLists(){
        return new ArrayList[]{(ArrayList)finalMultiblocks.clone(),(ArrayList)workingMultiblocks.clone()};
    }
    @Override
    public boolean canGenerateFor(Multiblock multiblock){
        return multiblock instanceof CuboidalMultiblock&&!(multiblock instanceof OverhaulTurbine);
    }
    @Override
    public String getName(){
        return "Standard";
    }
    @Override
    public void addSettings(MenuComponentMinimaList generatorSettings, Multiblock multi){
//        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Multiblocks", true));
//        finalMultiblockCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "2", true).setIntFilter());
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Working Multiblocks", true));
        workingMultiblockCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter()).setTooltip("This is the number of multiblocks that are actively being worked on\nEvery thread will work on all working multiblocks");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Timeout (sec)", true));
        timeout = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "10", true).setIntFilter()).setTooltip("If a multiblock hasn't changed for this long, it will be reset\nThis is to avoid running into generation dead-ends");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Priorities", true));
        prioritiesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, priorities.size()*32, 24){
            @Override
            public void render(int millisSinceLastTick){
                for(simplelibrary.opengl.gui.components.MenuComponent c : components){
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
        moveUp = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Up", true, true).setTooltip("Move the selected priority up so it is more important"));
        moveUp.addActionListener((e) -> {
            int index = prioritiesList.getSelectedIndex();
            if(index==-1||index==0)return;
            priorities.add(index-1, priorities.remove(index));
            refreshPriorities();
            prioritiesList.setSelectedIndex(index-1);
        });
        moveDown = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Down", true, true).setTooltip("Move the selected priority down so it is less important"));
        moveDown.addActionListener((e) -> {
            int index = prioritiesList.getSelectedIndex();
            if(index==-1||index==priorities.size()-1)return;
            priorities.add(index+1, priorities.remove(index));
            refreshPriorities();
            prioritiesList.setSelectedIndex(index+1);
        });
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Generator Settings", true));
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 24, "Change Chance", true));
        changeChance = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "1", true).setFloatFilter(0f, 100f).setSuffix("%")).setTooltip("If variable rate is on: Each iteration, each block in the reactor has an x% chance of changing\nIf variable rate is off: Each iteration, exactly x% of the blocks in the reactor will change (minimum of 1)");
        variableRate = generatorSettings.add(new MenuComponentToggleBox(0, 0, 0, 32, "Variable Rate", true));
        lockCore = generatorSettings.add(new MenuComponentToggleBox(0, 0, 0, 32, "Lock Core", false));
        fillAir = generatorSettings.add(new MenuComponentToggleBox(0, 0, 0, 32, "Fill Air", false));
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
    public void refreshSettingsFromGUI(ArrayList<Range<Block>> allowedBlocks){
        settings.refresh(allowedBlocks);
    }
    @Override
    public void refreshSettings(Settings settings){
        if(settings instanceof StandardGeneratorSettings){
            this.settings.refresh((StandardGeneratorSettings)settings);
        }else throw new IllegalArgumentException("Passed invalid settings to Standard generator!");
    }
    @Override
    public void tick(){
        int size;
        //<editor-fold defaultstate="collapsed" desc="Adding/removing working multiblocks">
        synchronized(workingMultiblocks){
            size = workingMultiblocks.size();
        }
        if(size<settings.workingMultiblocks){
            Multiblock inst = multiblock.blankCopy();
            inst.recalculate();
            synchronized(workingMultiblocks){
                workingMultiblocks.add(inst);
            }
        }else if(size>settings.workingMultiblocks){
            synchronized(workingMultiblocks){
                Multiblock worst = null;
                for(Multiblock mb : workingMultiblocks){
                    if(worst==null||worst.isBetterThan(mb, settings.priorities)){
                        worst = mb;
                    }
                }
                if(worst!=null){
                    finalize(worst);
                    workingMultiblocks.remove(worst);
                }
            }
        }
//</editor-fold>
        Multiblock currentMultiblock = null;
        int idx = index;
        //<editor-fold defaultstate="collapsed" desc="Fetch Current multiblock">
        synchronized(workingMultiblocks){
            if(idx>=workingMultiblocks.size())idx = 0;
            if(!workingMultiblocks.isEmpty()){
                currentMultiblock = workingMultiblocks.get(idx).copy();//TODO this is very laggy
            }
            index++;
            if(index>=workingMultiblocks.size())index = 0;
        }
//</editor-fold>
        if(currentMultiblock==null)return;//there's nothing to do!
        if(settings.variableRate){
            final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
            cm.forEachInternalPosition((x, y, z) -> {
                Block b = cm.getBlock(x, y, z);
                if(settings.lockCore&&b!=null&&b.isCore())return;
                if(rand.nextDouble()<settings.getChangeChance()||(settings.fillAir&&b==null)){
                    Block randBlock = rand(cm, settings.allowedBlocks);
                    if(randBlock==null||settings.lockCore&&randBlock.isCore()||!cm.canBePlacedWithinCasing(randBlock))return;//nope
                    cm.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cm, randBlock.newInstance(x, y, z))));
                }
            });
            cm.buildDefaultCasing();
        }else{
            int changes = (int) Math.max(1, Math.round(settings.changeChancePercent*currentMultiblock.getTotalVolume()));
            ArrayList<int[]> pool = new ArrayList<>();
            final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
            cm.forEachInternalPosition((x, y, z) -> {
                if(settings.fillAir&&cm.getBlock(x, y, z)==null){
                    Block randBlock = rand(cm, settings.allowedBlocks);
                    if(randBlock==null||settings.lockCore&&randBlock.isCore()||!cm.canBePlacedWithinCasing(randBlock))return;//nope
                    cm.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cm, randBlock.newInstance(x, y, z))));
                    return;
                }
                pool.add(new int[]{x,y,z});
            });
            cm.buildDefaultCasing();
            for(int i = 0; i<changes; i++){//so it can't change the same cell twice
                if(pool.isEmpty())break;
                int[] pos = pool.remove(rand.nextInt(pool.size()));
                Block b = currentMultiblock.getBlock(pos[0], pos[1], pos[2]);
                if(settings.lockCore&&b!=null&&b.isCore())continue;
                Block randBlock = rand(currentMultiblock, settings.allowedBlocks);
                if(randBlock==null||settings.lockCore&&randBlock.isCore())continue;//nope
                currentMultiblock.queueAction(new SetblockAction(pos[0], pos[1], pos[2], applyMultiblockSpecificSettings(currentMultiblock, randBlock.newInstance(pos[0], pos[1], pos[2]))));
            }
        }
        currentMultiblock.performActions(false);
        for(PostProcessingEffect effect : settings.postProcessingEffects){
            if(effect.preSymmetry)currentMultiblock.action(new PostProcessingAction(effect, settings), true, false);
        }
        for(Symmetry symmetry : settings.symmetries){
            currentMultiblock.queueAction(new SymmetryAction(symmetry));
        }
        currentMultiblock.performActions(false);
        currentMultiblock.recalculate();
        for(PostProcessingEffect effect : settings.postProcessingEffects){
            if(effect.postSymmetry)currentMultiblock.action(new PostProcessingAction(effect, settings), true, false);
        }
        synchronized(workingMultiblocks.get(idx)){
            Multiblock mult = workingMultiblocks.get(idx);
            finalize(mult);
            if(currentMultiblock.isBetterThan(mult, settings.priorities)){workingMultiblocks.set(idx, currentMultiblock.copy());}
            else if(mult.millisSinceLastChange()>settings.timeout*1000){
                Multiblock m = multiblock.blankCopy();
                m.recalculate();
                workingMultiblocks.set(idx, m);
            }
        }
        countIteration();
    }
    private Block applyMultiblockSpecificSettings(Multiblock currentMultiblock, Block randBlock){
        if(multiblock instanceof OverhaulSFR){
            multiblock.overhaul.fissionsfr.Block block = (multiblock.overhaul.fissionsfr.Block)randBlock;
            if(!block.template.allRecipes.isEmpty()){
                ArrayList<Range<multiblock.configuration.overhaul.fissionsfr.BlockRecipe>> validRecipes = new ArrayList<>(((OverhaulSFR)multiblock).getValidRecipes());
                for(Iterator<Range<multiblock.configuration.overhaul.fissionsfr.BlockRecipe>> it = validRecipes.iterator(); it.hasNext();){
                    Range<multiblock.configuration.overhaul.fissionsfr.BlockRecipe> next = it.next();
                    if(!block.template.allRecipes.contains(next.obj))it.remove();
                }
                if(!validRecipes.isEmpty())block.recipe = rand(currentMultiblock, validRecipes);
            }
            return randBlock;
        }
        if(multiblock instanceof OverhaulMSR){
            multiblock.overhaul.fissionmsr.Block block = (multiblock.overhaul.fissionmsr.Block)randBlock;
            if(!block.template.allRecipes.isEmpty()){
                ArrayList<Range<multiblock.configuration.overhaul.fissionmsr.BlockRecipe>> validRecipes = new ArrayList<>(((OverhaulMSR)multiblock).getValidRecipes());
                for(Iterator<Range<multiblock.configuration.overhaul.fissionmsr.BlockRecipe>> it = validRecipes.iterator(); it.hasNext();){
                    Range<multiblock.configuration.overhaul.fissionmsr.BlockRecipe> next = it.next();
                    if(!block.template.allRecipes.contains(next.obj))it.remove();
                }
                if(!validRecipes.isEmpty())block.recipe = rand(currentMultiblock, validRecipes);
            }
            return randBlock;
        }
        return randBlock;
    }
    private void finalize(Multiblock worst){
        if(worst==null)return;
        synchronized(finalMultiblocks){
        //<editor-fold defaultstate="collapsed" desc="Adding/removing final multiblocks">
            if(finalMultiblocks.size()<settings.finalMultiblocks){
                finalMultiblocks.add(worst.copy());
                return;
            }else if(finalMultiblocks.size()>settings.finalMultiblocks){
                Multiblock wrst = null;
                for(Multiblock mb : finalMultiblocks){
                    if(wrst==null||wrst.isBetterThan(mb, settings.priorities)){
                        wrst = mb;
                    }
                }
                if(wrst!=null){
                    finalMultiblocks.remove(wrst);
                }
            }
//</editor-fold>
            for(int i = 0; i<finalMultiblocks.size(); i++){
                Multiblock multi = finalMultiblocks.get(i);
                if(worst.isBetterThan(multi, settings.priorities)){
                    finalMultiblocks.set(i, worst.copy());
                    return;
                }
            }
        }
    }
    @Override
    public void importMultiblock(Multiblock multiblock) throws MissingConfigurationEntryException{
        multiblock.convertTo(this.multiblock.getConfiguration());
        if(multiblock instanceof UnderhaulSFR){
            multiblock = multiblock.copy();
            ((UnderhaulSFR)multiblock).fuel = ((UnderhaulSFR)this.multiblock).fuel;
            multiblock.recalculate();
        }
        if(!multiblock.isShapeEqual(this.multiblock))return;
        for(Range<Block> range : settings.allowedBlocks){
            for(Block block : ((Multiblock<Block>)multiblock).getBlocks()){
                if(multiblock.count(block)>range.max)multiblock.action(new SetblockAction(block.x, block.y, block.z, null), true, false);
            }
        }
        ALLOWED:for(Block block : ((Multiblock<Block>)multiblock).getBlocks()){
            for(Range<Block> range : settings.allowedBlocks){
                if(range.obj.isEqual(block))continue ALLOWED;
            }
            multiblock.action(new SetblockAction(block.x, block.y, block.z, null), true, false);
        }
        finalize(multiblock);
        workingMultiblocks.add(multiblock.copy());
    }
    private <T extends Object> T rand(Multiblock multiblock, ArrayList<Range<T>> ranges){
        if(ranges.isEmpty())return null;
        for(Range<T> range : ranges){
            if(range.min==0&&range.max==Integer.MAX_VALUE)continue;
            if(multiblock.count(range.obj)<range.min)return range.obj;
        }
        Range<T> randRange = ranges.get(rand.nextInt(ranges.size()));
        if((randRange.min!=0||randRange.max!=Integer.MAX_VALUE)&&randRange.max!=0&&multiblock.count(randRange.obj)>=randRange.max){
            return null;
        }
        return randRange.obj;
    }
}