package multiblock.ppe;
import multiblock.Multiblock;
public abstract class PostProcessingEffect{
    public final String name;
    public boolean preSymmetry, postSymmetry;
    public PostProcessingEffect(String name, boolean preSymmetry, boolean postSymmetry){
        this.name = name;
        this.preSymmetry = preSymmetry;
        this.postSymmetry = postSymmetry;
    }
    public abstract void apply(Multiblock multiblock);
    public boolean defaultEnabled(){
        return false;
    }
}