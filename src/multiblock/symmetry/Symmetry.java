package multiblock.symmetry;
import multiblock.Multiblock;
public abstract class Symmetry{
    public final String name;
    public Symmetry(String name){
        this.name = name;
    }
    public abstract void apply(Multiblock multiblock);
    public boolean defaultEnabled(){
        return false;
    }
    public boolean check(Multiblock m){
        Multiblock copy = m.copy();
        apply(copy);
        return m.areBlocksEqual(copy);
    }
}