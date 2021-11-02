package net.ncplanner.plannerator.multiblock.generator;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingBoolean;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingInt;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingPercentage;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingPostProcessingEffects;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingPriorities;
import net.ncplanner.plannerator.multiblock.generator.setting.SettingSymmetries;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.ncplanner.plannerator.multiblock.Block;
import net.ncplanner.plannerator.multiblock.CuboidalMultiblock;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.Range;
import net.ncplanner.plannerator.multiblock.editor.action.PostProcessingAction;
import net.ncplanner.plannerator.multiblock.editor.action.SetblockAction;
import net.ncplanner.plannerator.multiblock.editor.action.SymmetryAction;
import net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.OverhaulMSR;
import net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.OverhaulSFR;
import net.ncplanner.plannerator.multiblock.overhaul.turbine.OverhaulTurbine;
import net.ncplanner.plannerator.multiblock.editor.ppe.PostProcessingEffect;
import net.ncplanner.plannerator.multiblock.editor.symmetry.Symmetry;
import net.ncplanner.plannerator.multiblock.underhaul.fissionsfr.UnderhaulSFR;
import net.ncplanner.plannerator.planner.exception.MissingConfigurationEntryException;
public class CoreBasedGenerator extends MultiblockGenerator{
    public SettingInt finalMultiblockCount, workingMultiblockCount, finalCoreCount, workingCoreCount, timeout;
    public SettingPriorities finalPriorities, corePriorities;
    public SettingPercentage changeChance, morphChance;
    public SettingBoolean variableRate, fillAir;
    public SettingSymmetries symmetries;
    public SettingPostProcessingEffects postProcessingEffects;
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
    protected void createSettings(){
        settings.add(finalMultiblockCount = new SettingInt("Final Multiblocks", 1, 64, 1, "This is the number of multiblocks that are kept as the best generated multiblocks\nWhen you close the generator, only the best of these will be kept"));
        settings.add(workingMultiblockCount = new SettingInt("Working Multiblocks", 1, 1024, 3, "This is the number of multiblocks that are actively being worked on\nEvery thread will work on all working multiblocks"));
        settings.add(finalCoreCount = new SettingInt("Final Cores", 1, 64, 1, "This is the number of multiblock cores that are kept as the best generated cores\nThese are the basis for generating multiblocks"));
        settings.add(workingCoreCount = new SettingInt("Working Cores", 1, 1024, 3, "This is the number of multiblock cores that are actively being worked on\nEvery thread will work on all working cores"));
        settings.add(timeout = new SettingInt("Timeout (sec)", 0, 86400, 10, "If a multiblock hasn't changed for this long, it will be reset\nThis is to avoid running into generation dead-ends"));
        ArrayList<Priority> finalList = new ArrayList<>(multiblock.getGenerationPriorities());
        for(Iterator<Priority> it = finalList.iterator(); it.hasNext();){
            Priority p = it.next();
            if(!p.isFinal())it.remove();
        }
        settings.add(finalPriorities = new SettingPriorities("Final Priorities", finalList));
        ArrayList<Priority> coreList = new ArrayList<>(multiblock.getGenerationPriorities());
        for(Iterator<Priority> it = coreList.iterator(); it.hasNext();){
            Priority p = it.next();
            if(!p.isCore())it.remove();
        }
        settings.add(corePriorities = new SettingPriorities("Core Priorities", finalList));
        settings.add(changeChance = new SettingPercentage("Change Chance", .01, "If variable rate is on: Each iteration, each block in the reactor has an x% chance of changing\nIf variable rate is off: Each iteration, exactly x% of the blocks in the reactor will change (minimum of 1)"));
        settings.add(morphChance = new SettingPercentage("Morph Chance", .0001, "If variable rate is on: Each iteration, each block in the reactor has an x% chance of changing\nIf variable rate is off: Each iteration, exactly x% of the blocks in the reactor will change (minimum of 1)\nThis applies only to Core blocks, such as cells and moderators"));
        settings.add(variableRate = new SettingBoolean("Variable Rate", true));
        settings.add(fillAir = new SettingBoolean("Fill Air", false));
        settings.add(symmetries = new SettingSymmetries("Symmetry Settings", multiblock.getSymmetries()));
        settings.add(postProcessingEffects = new SettingPostProcessingEffects("Post-Processing", multiblock.getPostProcessingEffects()));
    }
    @Override
    public MultiblockGenerator newInstance(Multiblock multi){
        return new CoreBasedGenerator(multi);
    }
    @Override
    public void tick(){
        //<editor-fold defaultstate="collapsed" desc="Calculate Core">
        //<editor-fold defaultstate="collapsed" desc="Adding/removing multiblock cores">
        int coreSize;
        synchronized(workingCores){
            coreSize = workingCores.size();
        }
        if(coreSize<workingCoreCount.getValue()){
            Multiblock inst = multiblock.blankCopy();
            inst.recalculate();
            synchronized(workingCores){
                workingCores.add(inst);
            }
        }else if(coreSize>workingCoreCount.getValue()){
            synchronized(workingCores){
                Multiblock worst = null;
                for(Multiblock mb : workingCores){
                    if(worst==null||worst.isBetterThan(mb, corePriorities.getValue())){
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
        if(variableRate.getValue()){
            final CuboidalMultiblock cmc = (CuboidalMultiblock)currentMultiblockCore;
            cmc.forEachInternalPosition((x, y, z) -> {
                Block b = cmc.getBlock(x, y, z);
                if(rand.nextDouble()<changeChance.getValue()||(fillAir.getValue()&&b==null)){
                    Block randBlock = randCore(cmc, getAllowedBlocks());
                    if(randBlock==null||!randBlock.isCore()||!cmc.canBePlacedWithinCasing(randBlock))return;//nope
                    cmc.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cmc, randBlock.newInstance(x, y, z))));
                }
            });
            cmc.buildDefaultCasing();
        }else{
            int changes = (int) Math.max(1, Math.round(changeChance.getValue()*currentMultiblockCore.getTotalVolume()));
            ArrayList<int[]> pool = new ArrayList<>();
            final CuboidalMultiblock cmc = (CuboidalMultiblock)currentMultiblockCore;
            cmc.forEachInternalPosition((x, y, z) -> {
                if(fillAir.getValue()&&cmc.getBlock(x, y, z)==null){
                    Block randBlock = randCore(cmc, getAllowedBlocks());
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
                Block randBlock = randCore(currentMultiblockCore, getAllowedBlocks());
                if(randBlock==null||!randBlock.isCore())continue;//nope
                currentMultiblockCore.queueAction(new SetblockAction(pos[0], pos[1], pos[2], applyMultiblockSpecificSettings(currentMultiblockCore, randBlock.newInstance(pos[0], pos[1], pos[2]))));
            }
        }
        currentMultiblockCore.performActions(false);
        for(PostProcessingEffect effect : postProcessingEffects.getValue()){
            if(effect.core&&effect.preSymmetry)currentMultiblockCore.action(new PostProcessingAction(effect, this), true, false);
        }
        for(Symmetry symmetry : symmetries.getValue()){
            currentMultiblockCore.queueAction(new SymmetryAction(symmetry));
        }
        currentMultiblockCore.performActions(false);
        currentMultiblockCore.recalculate();
        for(PostProcessingEffect effect : postProcessingEffects.getValue()){
            if(effect.core&&effect.postSymmetry)currentMultiblockCore.action(new PostProcessingAction(effect, this), true, false);
        }
//</editor-fold>
        synchronized(workingCores.get(coreIndex)){
            Multiblock mult = workingCores.get(coreIndex);
            finalizeCore(mult);
            if(currentMultiblockCore.isBetterThan(mult, corePriorities.getValue())){workingCores.set(coreIndex, currentMultiblockCore.copy());}
            else if(mult.millisSinceLastChange()>timeout.getValue()*1000){
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
            if(workingSize<workingMultiblockCount.getValue()){
                Multiblock inst;
                synchronized(finalCores){
                    inst = finalCores.get(rand.nextInt(finalCores.size())).copy();
                }
                synchronized(workingMultiblocks){
                    workingMultiblocks.add(inst);
                }
            }else if(workingSize>workingMultiblockCount.getValue()){
                synchronized(workingMultiblocks){
                    Multiblock worst = null;
                    for(Multiblock mb : workingMultiblocks){
                        if(worst==null||worst.isBetterThan(mb, finalPriorities.getValue())){
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
            if(variableRate.getValue()){
                final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
                cm.forEachInternalPosition((x, y, z) -> {
                    Block b = cm.getBlock(x, y, z);
                    boolean morph = rand.nextDouble()<morphChance.getValue();
                    if(b!=null&&(b.isCore()&&!morph))return;
                    if(rand.nextDouble()<changeChance.getValue()||(fillAir.getValue()&&b==null)){
                        Block randBlock = rand(cm, getAllowedBlocks());
                        if(randBlock==null||(randBlock.isCore()&&!morph)||!cm.canBePlacedWithinCasing(randBlock))return;//nope
                        cm.queueAction(new SetblockAction(x, y, z, applyMultiblockSpecificSettings(cm, randBlock.newInstance(x, y, z))));
                    }
                });
                cm.buildDefaultCasing();
            }else{
                int changes = (int) Math.max(1, Math.round(changeChance.getValue()*currentMultiblock.getTotalVolume()));
                ArrayList<int[]> pool = new ArrayList<>();
                final CuboidalMultiblock cm = (CuboidalMultiblock)currentMultiblock;
                cm.forEachInternalPosition((x, y, z) -> {
                    if(fillAir.getValue()&&cm.getBlock(x, y, z)==null){
                        Block randBlock = rand(cm, getAllowedBlocks());
                        boolean morph = rand.nextDouble()<morphChance.getValue();
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
                        boolean morph = rand.nextDouble()<morphChance.getValue();
                    if(b!=null&&(b.isCore()&&!morph))continue;
                    Block randBlock = rand(currentMultiblock, getAllowedBlocks());
                    if(randBlock==null||randBlock.isCore()&&!morph)continue;//nope
                    currentMultiblock.queueAction(new SetblockAction(pos[0], pos[1], pos[2], applyMultiblockSpecificSettings(currentMultiblock, randBlock.newInstance(pos[0], pos[1], pos[2]))));
                }
            }
            currentMultiblock.performActions(false);
            for(PostProcessingEffect effect : postProcessingEffects.getValue()){
                if(effect.preSymmetry)currentMultiblock.action(new PostProcessingAction(effect, this), true, false);
            }
            for(Symmetry symmetry : symmetries.getValue()){
                currentMultiblock.queueAction(new SymmetryAction(symmetry));
            }
            currentMultiblock.performActions(false);
            currentMultiblock.recalculate();
            for(PostProcessingEffect effect : postProcessingEffects.getValue()){
                if(effect.postSymmetry)currentMultiblock.action(new PostProcessingAction(effect, this), true, false);
            }
    //</editor-fold>
            synchronized(workingMultiblocks.get(idx)){
                Multiblock mult = workingMultiblocks.get(idx);
                finalize(mult);
                if(currentMultiblock.isBetterThan(mult, finalPriorities.getValue())){workingMultiblocks.set(idx, currentMultiblock.copy());}
                else if(mult.millisSinceLastChange()>timeout.getValue()*1000){
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
            net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block block = (net.ncplanner.plannerator.multiblock.overhaul.fissionsfr.Block)randBlock;
            if(!block.template.allRecipes.isEmpty()){
                ArrayList<Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe>> validRecipes = new ArrayList<>(((OverhaulSFR)multiblock).getValidRecipes());
                for(Iterator<Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe>> it = validRecipes.iterator(); it.hasNext();){
                    Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionsfr.BlockRecipe> next = it.next();
                    if(!block.template.allRecipes.contains(next.obj))it.remove();
                }
                if(!validRecipes.isEmpty())block.recipe = rand(currentMultiblock, validRecipes);
            }
            return randBlock;
        }
        if(multiblock instanceof OverhaulMSR){
            net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block block = (net.ncplanner.plannerator.multiblock.overhaul.fissionmsr.Block)randBlock;
            if(!block.template.allRecipes.isEmpty()){
                ArrayList<Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe>> validRecipes = new ArrayList<>(((OverhaulMSR)multiblock).getValidRecipes());
                for(Iterator<Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe>> it = validRecipes.iterator(); it.hasNext();){
                    Range<net.ncplanner.plannerator.multiblock.configuration.overhaul.fissionmsr.BlockRecipe> next = it.next();
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
            if(finalCores.size()<finalCoreCount.getValue()){
                finalCores.add(worst.copy());
                return;
            }else if(finalCores.size()>finalCoreCount.getValue()){
                Multiblock wrst = null;
                for(Multiblock mb : finalCores){
                    if(wrst==null||wrst.isBetterThan(mb, corePriorities.getValue())){
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
                if(worst.isBetterThan(multi, corePriorities.getValue())){
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
            if(finalMultiblocks.size()<finalMultiblockCount.getValue()){
                finalMultiblocks.add(worst.copy());
                return;
            }else if(finalMultiblocks.size()>finalMultiblockCount.getValue()){
                Multiblock wrst = null;
                for(Multiblock mb : finalMultiblocks){
                    if(wrst==null||wrst.isBetterThan(mb, finalPriorities.getValue())){
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
                if(worst.isBetterThan(multi, finalPriorities.getValue())){
                    finalMultiblocks.set(i, worst.copy());
                    return;
                }
            }
        }
    }
    @Override
    public void importMultiblock(Multiblock multiblock) throws MissingConfigurationEntryException{
        if(!multiblock.isShapeEqual(this.multiblock))return;
        multiblock.convertTo(this.multiblock.getConfiguration());
        if(multiblock instanceof UnderhaulSFR){
            multiblock = multiblock.copy();
            ((UnderhaulSFR)multiblock).fuel = ((UnderhaulSFR)this.multiblock).fuel;
            multiblock.recalculate();
        }
        for(Range<Block> range : getAllowedBlocks()){
            for(Block block : ((Multiblock<Block>)multiblock).getBlocks()){
                if(multiblock.count(block)>range.max)multiblock.action(new SetblockAction(block.x, block.y, block.z, null), true, false);
            }
        }
        ALLOWED:for(Block block : ((Multiblock<Block>)multiblock).getBlocks()){
            for(Range<Block> range : getAllowedBlocks()){
                if(range.obj.isEqual(block))continue ALLOWED;
            }
            multiblock.action(new SetblockAction(block.x, block.y, block.z, null), true, false);
        }
        finalize(multiblock);
    }
    private <T extends Object> T rand(Multiblock multiblock, List<Range<T>> ranges){
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
    private <T extends Block> T randCore(Multiblock multiblock, List<Range<T>> ranges){
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