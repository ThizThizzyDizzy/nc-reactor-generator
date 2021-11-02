package net.ncplanner.plannerator.multiblock.editor.ppe;
import net.ncplanner.plannerator.multiblock.Multiblock;
import net.ncplanner.plannerator.multiblock.generator.MultiblockGenerator;
public abstract class PostProcessingEffect<T extends Multiblock>{
    public final String name;
    public final boolean core;
    public final boolean preSymmetry, postSymmetry;
    public PostProcessingEffect(String name, boolean core, boolean preSymmetry, boolean postSymmetry){
        this.name = name;
        this.core = core;
        this.preSymmetry = preSymmetry;
        this.postSymmetry = postSymmetry;
    }
    public abstract void apply(T multiblock, MultiblockGenerator generator);
    public boolean defaultEnabled(){
        return false;
    }
}