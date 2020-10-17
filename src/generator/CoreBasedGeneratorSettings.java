package generator;
import generator.Priority;
import generator.Settings;
import java.util.ArrayList;
import multiblock.Block;
import multiblock.Range;
import multiblock.ppe.PostProcessingEffect;
import multiblock.symmetry.Symmetry;
import planner.menu.component.generator.MenuComponentPostProcessingEffect;
import planner.menu.component.generator.MenuComponentPriority;
import planner.menu.component.generator.MenuComponentSymmetry;
public class CoreBasedGeneratorSettings implements Settings{
    public int finalMultiblocks, workingMultiblocks, finalCores, workingCores, timeout;
    public ArrayList<Priority> finalPriorities = new ArrayList<>();
    public ArrayList<Priority> corePriorities = new ArrayList<>();
    public ArrayList<Symmetry> symmetries = new ArrayList<>();
    public ArrayList<PostProcessingEffect> postProcessingEffects = new ArrayList<>();
    public ArrayList<Range<Block>> allowedBlocks = new ArrayList<>();
    public float changeChancePercent;
    public float morphChancePercent;
    public boolean variableRate, fillAir;
    private final CoreBasedGenerator generator;
    public CoreBasedGeneratorSettings(CoreBasedGenerator generator){
        this.generator = generator;
    }
    public void refresh(CoreBasedGeneratorSettings settings){
        allowedBlocks = settings.allowedBlocks;
        finalMultiblocks = settings.finalMultiblocks;
        workingMultiblocks = settings.workingMultiblocks;
        finalCores = settings.finalCores;
        workingCores = settings.workingCores;
        timeout = settings.timeout;
        finalPriorities = settings.finalPriorities;
        corePriorities = settings.corePriorities;
        symmetries = settings.symmetries;
        postProcessingEffects = settings.postProcessingEffects;
        allowedBlocks = settings.allowedBlocks;
        changeChancePercent = settings.changeChancePercent;
        morphChancePercent = settings.morphChancePercent;
        variableRate = settings.variableRate;
        fillAir = settings.fillAir;
    }
    public void refresh(ArrayList<Range<Block>> allowedBlocks){
        this.allowedBlocks = allowedBlocks;
        finalMultiblocks = Integer.parseInt(generator.finalMultiblockCount.text);
        workingMultiblocks = Integer.parseInt(generator.workingMultiblockCount.text);
        finalCores = Integer.parseInt(generator.finalCoreCount.text);
        workingCores = Integer.parseInt(generator.workingCoreCount.text);
        timeout = Integer.parseInt(generator.timeout.text);
        ArrayList<Symmetry> newSymmetries = new ArrayList<>();
        for(simplelibrary.opengl.gui.components.MenuComponent comp : generator.symmetriesList.components){
            if(((MenuComponentSymmetry)comp).enabled)newSymmetries.add(((MenuComponentSymmetry)comp).symmetry);
        }
        symmetries = newSymmetries;
        ArrayList<Priority> newFinalPriorities = new ArrayList<>();
        for(simplelibrary.opengl.gui.components.MenuComponent comp : generator.finalPrioritiesList.components){
            Priority priority = ((MenuComponentPriority)comp).priority;
            if(priority.isFinal())newFinalPriorities.add(((MenuComponentPriority)comp).priority);
        }
        finalPriorities = newFinalPriorities;//to avoid concurrentModification
        ArrayList<Priority> newCorePriorities = new ArrayList<>();
        for(simplelibrary.opengl.gui.components.MenuComponent comp : generator.corePrioritiesList.components){
            Priority priority = ((MenuComponentPriority)comp).priority;
            if(priority.isCore())newCorePriorities.add(((MenuComponentPriority)comp).priority);
        }
        corePriorities = newCorePriorities;//to avoid concurrentModification
        ArrayList<PostProcessingEffect> newEffects = new ArrayList<>();
        for(simplelibrary.opengl.gui.components.MenuComponent comp : generator.postProcessingEffectsList.components){
            if(((MenuComponentPostProcessingEffect)comp).enabled)newEffects.add(((MenuComponentPostProcessingEffect)comp).postProcessingEffect);
        }
        postProcessingEffects = newEffects;
        changeChancePercent = Float.parseFloat(generator.changeChance.text);
        morphChancePercent = Float.parseFloat(generator.morphChance.text);
        variableRate = generator.variableRate.enabled;
        fillAir = generator.fillAir.enabled;
    }
    public float getChangeChance(){
        return changeChancePercent/100;
    }
    public float getMorphChance(){
        return morphChancePercent/100;
    }
    @Override
    public ArrayList<Range<Block>> getAllowedBlocks(){
        return allowedBlocks;
    }
}