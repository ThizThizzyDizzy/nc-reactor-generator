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
public class CoreBasedGenerator extends MultiblockGenerator{
    MenuComponentMinimalistTextBox finalMultiblockCount;
    MenuComponentMinimalistTextBox workingMultiblockCount;
    MenuComponentMinimalistTextBox finalCoreCount;
    MenuComponentMinimalistTextBox workingCoreCount;
    MenuComponentMinimalistTextBox timeout;
    MenuComponentMinimaList finalPrioritiesList;
    MenuComponentMinimaList corePrioritiesList;
    MenuComponentMinimalistButton moveFinalUp;
    MenuComponentMinimalistButton moveFinalDown;
    MenuComponentMinimalistButton moveCoreUp;
    MenuComponentMinimalistButton moveCoreDown;
    MenuComponentMinimaList symmetriesList;
    MenuComponentMinimaList postProcessingEffectsList;
    MenuComponentMinimalistTextBox changeChance;
    MenuComponentMinimalistTextBox morphChance;
    MenuComponentToggleBox variableRate;
    MenuComponentToggleBox fillAir;
    private CoreBasedGeneratorSettings settings = new CoreBasedGeneratorSettings(this);
    private final ArrayList<Multiblock> finalMultiblocks = new ArrayList<>();
    private final ArrayList<Multiblock> workingMultiblocks = new ArrayList<>();
    private final ArrayList<Multiblock> finalCores = new ArrayList<>();
    private final ArrayList<Multiblock> workingCores = new ArrayList<>();
    private int index = 0;
    private int corIndex = 0;
    public CoreBasedGenerator(Multiblock multiblock){
        super(multiblock);
    }
    @Override
    public ArrayList<Multiblock>[] getMultiblockLists(){
        return new ArrayList[]{(ArrayList)finalMultiblocks.clone(),(ArrayList)workingMultiblocks.clone(),(ArrayList)finalCores.clone(),(ArrayList)workingCores.clone()};
    }
    @Override
    public boolean canGenerateFor(Multiblock multiblock){
        return multiblock instanceof CuboidalMultiblock&&!(multiblock instanceof OverhaulTurbine)&&!(multiblock instanceof UnderhaulSFR);
    }
    @Override
    public String getName(){
        return "Core-based";
    }
    @Override
    public void addSettings(MenuComponentMinimaList generatorSettings, Multiblock multi){
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Multiblocks", true));
        finalMultiblockCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter()).setTooltip("This is the number of multiblocks that are kept as the best generated multiblocks\nWhen you close the generator, only the best of these will be kept");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Working Multiblocks", true));
        workingMultiblockCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter()).setTooltip("This is the number of multiblocks that are actively being worked on\nEvery thread will work on all working multiblocks");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Cores", true));
        finalCoreCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter()).setTooltip("This is the number of multiblock cores that are kept as the best generated cores\nThese are the basis for generating multiblocks");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Working Cores", true));
        workingCoreCount = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "6", true).setIntFilter()).setTooltip("This is the number of multiblock cores that are actively being worked on\nEvery thread will work on all working cores");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Timeout (sec)", true));
        timeout = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "10", true).setIntFilter()).setTooltip("If a multiblock hasn't changed for this long, it will be reset\nThis is to avoid running into generation dead-ends");
        int numFinal = 0, numCore = 0;
        for(Priority p : priorities){
            if(p.isFinal())numFinal++;
            if(p.isCore())numCore++;
        }
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Final Priorities", true));
        finalPrioritiesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, numFinal*32, 24){
            @Override
            public void render(int millisSinceLastTick){
                for(simplelibrary.opengl.gui.components.MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
            }
        });
        finalPrioritiesList.components.clear();
        for(Priority priority : priorities){
            if(priority.isFinal())finalPrioritiesList.add(new MenuComponentPriority(priority));
        }
        MenuComponent finalPriorityButtonHolder = generatorSettings.add(new MenuComponent(0, 0, 0, 32){
            @Override
            public void renderBackground(){
                components.get(1).x = width/2;
                components.get(0).width = components.get(1).width = width/2;
                components.get(0).height = components.get(1).height = height;
            }
            @Override
            public void render(){}
        });
        moveFinalUp = finalPriorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Up", true, true).setTooltip("Move the selected priority up so it is more important"));
        moveFinalUp.addActionListener((e) -> {
            int index = finalPrioritiesList.getSelectedIndex();
            if(index==-1||index==0)return;
            finalPrioritiesList.components.add(index-1, finalPrioritiesList.components.remove(index));
//            refreshPriorities();
            finalPrioritiesList.setSelectedIndex(index-1);
        });
        moveFinalDown = finalPriorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Down", true, true).setTooltip("Move the selected priority down so it is less important"));
        moveFinalDown.addActionListener((e) -> {
            int index = finalPrioritiesList.getSelectedIndex();
            if(index==-1||index==finalPrioritiesList.components.size()-1)return;
            finalPrioritiesList.components.add(index+1, finalPrioritiesList.components.remove(index));
//            refreshPriorities();
            finalPrioritiesList.setSelectedIndex(index+1);
        });
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Core Priorities", true));
        corePrioritiesList = generatorSettings.add(new MenuComponentMinimaList(0, 0, 0, numCore*32, 24){
            @Override
            public void render(int millisSinceLastTick){
                for(simplelibrary.opengl.gui.components.MenuComponent c : components){
                    c.width = width-(hasVertScrollbar()?vertScrollbarWidth:0);
                }
                super.render(millisSinceLastTick);
            }
        });
        corePrioritiesList.components.clear();
        for(Priority priority : priorities){
            if(priority.isCore())corePrioritiesList.add(new MenuComponentPriority(priority));
        }
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
        moveCoreUp = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Up", true, true).setTooltip("Move the selected priority up so it is more important"));
        moveCoreUp.addActionListener((e) -> {
            int index = corePrioritiesList.getSelectedIndex();
            if(index==-1||index==0)return;
            corePrioritiesList.components.add(index-1, corePrioritiesList.components.remove(index));
//            refreshPriorities();
            corePrioritiesList.setSelectedIndex(index-1);
        });
        moveCoreDown = priorityButtonHolder.add(new MenuComponentMinimalistButton(0, 0, 0, 0, "Move Down", true, true).setTooltip("Move the selected priority down so it is less important"));
        moveCoreDown.addActionListener((e) -> {
            int index = corePrioritiesList.getSelectedIndex();
            if(index==-1||index==corePrioritiesList.components.size()-1)return;
            corePrioritiesList.components.add(index+1, corePrioritiesList.components.remove(index));
//            refreshPriorities();
            corePrioritiesList.setSelectedIndex(index+1);
        });
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 32, "Generator Settings", true));
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 24, "Change Chance", true));
        changeChance = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, "1", true).setFloatFilter(0f, 100f).setSuffix("%")).setTooltip("If variable rate is on: Each iteration, each block in the reactor has an x% chance of changing\nIf variable rate is off: Each iteration, exactly x% of the blocks in the reactor will change (minimum of 1)");
        generatorSettings.add(new MenuComponentLabel(0, 0, 0, 24, "Morph Chance", true));
        morphChance = generatorSettings.add(new MenuComponentMinimalistTextBox(0, 0, 0, 32, ".01", true).setFloatFilter(0f, 100f).setSuffix("%")).setTooltip("If variable rate is on: Each iteration, each block in the reactor has an x% chance of changing\nIf variable rate is off: Each iteration, exactly x% of the blocks in the reactor will change (minimum of 1)\nThis applies only to Core blocks, such as cells and moderators");
        variableRate = generatorSettings.add(new MenuComponentToggleBox(0, 0, 0, 32, "Variable Rate", true));
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
        return new CoreBasedGenerator(multi);
    }
    @Override
    public void refreshSettingsFromGUI(ArrayList<Range<Block>> allowedBlocks){
        settings.refresh(allowedBlocks);
    }
    @Override
    public void refreshSettings(Settings settings){
        if(settings instanceof CoreBasedGeneratorSettings){
            this.settings.refresh((CoreBasedGeneratorSettings)settings);
        }else throw new IllegalArgumentException("Passed invalid settings to Core-based generator!");
    }
    @Override
    public void tick(){
        //<editor-fold defaultstate="collapsed" desc="Calculate Core">
        //<editor-fold defaultstate="collapsed" desc="Adding/removing multiblock cores">
        int coreSize;
        synchronized(workingCores){
            coreSize = workingCores.size();
        }
        if(coreSize<settings.workingCores){
            Multiblock inst = multiblock.blankCopy();
            inst.recalculate();
            synchronized(workingCores){
                workingCores.add(inst);
            }
        }else if(coreSize>settings.workingCores){
            synchronized(workingCores){
                Multiblock worst = null;
                for(Multiblock mb : workingCores){
                    if(worst==null||worst.isBetterThan(mb, settings.corePriorities)){
                        worst = mb;
                    }
                }
                if(worst!=null){
                    workingCores.remove(worst);
                }
            }
        }
//</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Fetch Current Core">
        Multiblock currentMultiblockCore = null;
        int coreIndex = corIndex;
        synchronized(workingCores){
            if(coreIndex>=workingCores.size())coreIndex = 0;
            if(!workingCores.isEmpty()){
                currentMultiblockCore = workingCores.get(coreIndex).copy();
            }
            corIndex++;
            if(corIndex>=workingCores.size())corIndex = 0;
        }
        //</editor-fold>
        if(currentMultiblockCore==null)return;//there's nothing to do!
        //<editor-fold defaultstate="collapsed" desc="Process Core">
        if(settings.variableRate){
            final CuboidalMultiblock cmc = (CuboidalMultiblock)currentMultiblockCore;
            cmc.forEachInternalPosition((x, y, z) -> {
                Block b = cmc.getBlock(x, y, z);
                if(rand.nextDouble()<settings.getChangeChance()||(settings.fillAir&&b==null)){
                    Block randBlock = randCore(cmc, settings.allowedBlocks);
                    if(randBlock==null||!randBlock.isCore()||!cmc.canBePlacedWithinCasing(randBlock))return;//nope
                    cmc.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cmc, randBlock.newInstance(x, y, z))));
                }
            });
            cmc.buildDefaultCasing();
        }else{
            int changes = (int) Math.max(1, Math.round(settings.getChangeChance()*currentMultiblockCore.getTotalVolume()));
            ArrayList<int[]> pool = new ArrayList<>();
            final CuboidalMultiblock cmc = (CuboidalMultiblock)currentMultiblockCore;
            cmc.forEachInternalPosition((x, y, z) -> {
                if(settings.fillAir&&cmc.getBlock(x, y, z)==null){
                    Block randBlock = randCore(cmc, settings.allowedBlocks);
                    if(randBlock==null||!randBlock.isCore()||!cmc.canBePlacedWithinCasing(randBlock))return;//nope
                    cmc.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cmc, randBlock.newInstance(x, y, z))));
                    return;
                }
                pool.add(new int[]{x,y,z});
            });
            cmc.buildDefaultCasing();
            for(int i = 0; i<changes; i++){//so it can't change the same cell twice
                if(pool.isEmpty())break;
                int[] pos = pool.remove(rand.nextInt(pool.size()));
                Block b = currentMultiblockCore.getBlock(pos[0], pos[1], pos[2]);
                Block randBlock = randCore(currentMultiblockCore, settings.allowedBlocks);
                if(randBlock==null||!randBlock.isCore())continue;//nope
                currentMultiblockCore.queueAction(new SetblockAction(pos[0], pos[1], pos[2], applyMultiblockSpecificSettings(currentMultiblockCore, randBlock.newInstance(pos[0], pos[1], pos[2]))));
            }
        }
        currentMultiblockCore.performActions(false);
        for(PostProcessingEffect effect : settings.postProcessingEffects){
            if(effect.core&&effect.preSymmetry)currentMultiblockCore.action(new PostProcessingAction(effect, settings), true, false);
        }
        for(Symmetry symmetry : settings.symmetries){
            currentMultiblockCore.queueAction(new SymmetryAction(symmetry));
        }
        currentMultiblockCore.performActions(false);
        currentMultiblockCore.recalculate();
        for(PostProcessingEffect effect : settings.postProcessingEffects){
            if(effect.core&&effect.postSymmetry)currentMultiblockCore.action(new PostProcessingAction(effect, settings), true, false);
        }
//</editor-fold>
        synchronized(workingCores.get(coreIndex)){
            Multiblock mult = workingCores.get(coreIndex);
            finalizeCore(mult);
            if(currentMultiblockCore.isBetterThan(mult, settings.corePriorities)){workingCores.set(coreIndex, currentMultiblockCore.copy());}
            else if(mult.millisSinceLastChange()>settings.timeout*1000){
                Multiblock m = multiblock.blankCopy();
                m.recalculate();
                workingCores.set(coreIndex, m);
            }
        }
    //</editor-fold>
        countIteration();
        int numFinalCores;
        synchronized(finalCores){
            numFinalCores = finalCores.size();
        }
        if(numFinalCores>0){
            //<editor-fold defaultstate="collapsed" desc="Calculate Working Multiblock">
            //<editor-fold defaultstate="collapsed" desc="Adding/removing working multiblocks">
            int workingSize;
            synchronized(workingMultiblocks){
                workingSize = workingMultiblocks.size();
            }
            if(workingSize<settings.workingMultiblocks){
                Multiblock inst;
                synchronized(finalCores){
                    inst = finalCores.get(rand.nextInt(finalCores.size())).copy();
                }
                synchronized(workingMultiblocks){
                    workingMultiblocks.add(inst);
                }
            }else if(workingSize>settings.workingMultiblocks){
                synchronized(workingMultiblocks){
                    Multiblock worst = null;
                    for(Multiblock mb : workingMultiblocks){
                        if(worst==null||worst.isBetterThan(mb, settings.finalPriorities)){
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
            //<editor-fold defaultstate="collapsed" desc="Fetch Current multiblock">
            Multiblock currentMultiblock = null;
            int idx = index;
            synchronized(workingMultiblocks){
                if(idx>=workingMultiblocks.size())idx = 0;
                if(!workingMultiblocks.isEmpty()){
                    currentMultiblock = workingMultiblocks.get(idx).copy();
                }
                index++;
                if(index>=workingMultiblocks.size())index = 0;
            }
            //</editor-fold>
            if(currentMultiblock==null)return;//there's nothing to do!
            //<editor-fold defaultstate="collapsed" desc="Process Multiblock">
            if(settings.variableRate){
                final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
                cm.forEachInternalPosition((x, y, z) -> {
                    Block b = cm.getBlock(x, y, z);
                    boolean morph = rand.nextDouble()<settings.getMorphChance();
                    if(b!=null&&(b.isCore()&&!morph))return;
                    if(rand.nextDouble()<settings.getChangeChance()||(settings.fillAir&&b==null)){
                        Block randBlock = rand(cm, settings.allowedBlocks);
                        if(randBlock==null||(randBlock.isCore()&&!morph)||!cm.canBePlacedWithinCasing(randBlock))return;//nope
                        cm.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cm, randBlock.newInstance(x, y, z))));
                    }
                });
                cm.buildDefaultCasing();
            }else{
                int changes = (int) Math.max(1, Math.round(settings.getChangeChance()*currentMultiblock.getTotalVolume()));
                ArrayList<int[]> pool = new ArrayList<>();
                final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
                cm.forEachInternalPosition((x, y, z) -> {
                    if(settings.fillAir&&cm.getBlock(x, y, z)==null){
                        Block randBlock = rand(cm, settings.allowedBlocks);
                        boolean morph = rand.nextDouble()<settings.getMorphChance();
                        if(randBlock==null||(randBlock.isCore()&&!morph)||!cm.canBePlacedWithinCasing(randBlock))return;//nope
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
                        boolean morph = rand.nextDouble()<settings.getMorphChance();
                    if(b!=null&&(b.isCore()&&!morph))continue;
                    Block randBlock = rand(currentMultiblock, settings.allowedBlocks);
                    if(randBlock==null||randBlock.isCore()&&!morph)continue;//nope
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
    //</editor-fold>
            synchronized(workingMultiblocks.get(idx)){
                Multiblock mult = workingMultiblocks.get(idx);
                finalize(mult);
                if(currentMultiblock.isBetterThan(mult, settings.finalPriorities)){workingMultiblocks.set(idx, currentMultiblock.copy());}
                else if(mult.millisSinceLastChange()>settings.timeout*1000){
                    synchronized(finalCores){
                        workingMultiblocks.set(idx, finalCores.get(rand.nextInt(finalCores.size())).copy());
                    }
                }
            }
        //</editor-fold>
            countIteration();
        }
    }
    private Block applyMultiblockSpecificSettings(Multiblock currentMultiblock, Block randBlock){
        if(multiblock instanceof UnderhaulSFR)return randBlock;//no block-specifics here!
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
        if(multiblock instanceof OverhaulTurbine)return randBlock;//also no block-specifics!
        throw new IllegalArgumentException("Unknown multiblock: "+multiblock.getDefinitionName());
    }
    private void finalizeCore(Multiblock worst){
        if(worst==null)return;
        synchronized(finalCores){
        //<editor-fold defaultstate="collapsed" desc="Adding/removing final multiblocks">
            if(finalCores.size()<settings.finalCores){
                finalCores.add(worst.copy());
                return;
            }else if(finalCores.size()>settings.finalCores){
                Multiblock wrst = null;
                for(Multiblock mb : finalCores){
                    if(wrst==null||wrst.isBetterThan(mb, settings.corePriorities)){
                        wrst = mb;
                    }
                }
                if(wrst!=null){
                    finalCores.remove(wrst);
                }
            }
//</editor-fold>
            for(int i = 0; i<finalCores.size(); i++){
                Multiblock multi = finalCores.get(i);
                if(worst.isBetterThan(multi, settings.corePriorities)){
                    finalCores.set(i, worst.copy());
                    return;
                }
            }
        }
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
                    if(wrst==null||wrst.isBetterThan(mb, settings.finalPriorities)){
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
                if(worst.isBetterThan(multi, settings.finalPriorities)){
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
    private <T extends Block> T randCore(Multiblock multiblock, ArrayList<Range<T>> ranges){
        ArrayList<Range<T>> coreRanges = new ArrayList<>(ranges);
        for(Iterator<Range<T>> it = coreRanges.iterator(); it.hasNext();){
            Range<T> next = it.next();
            if(!next.obj.isCore())it.remove();
        }
        if(coreRanges.isEmpty())return null;
        for(Range<T> range : coreRanges){
            if(!range.obj.isCore())continue;
            if(range.min==0&&range.max==Integer.MAX_VALUE)continue;
            if(multiblock.count(range.obj)<range.min)return range.obj;
        }
        Range<T> randRange = coreRanges.get(rand.nextInt(coreRanges.size()));
        if((randRange.min!=0||randRange.max!=Integer.MAX_VALUE)&&randRange.max!=0&&multiblock.count(randRange.obj)>=randRange.max){
            return null;
        }
        return randRange.obj;
    }
}