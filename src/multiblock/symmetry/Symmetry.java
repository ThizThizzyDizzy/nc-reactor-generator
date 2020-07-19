package multiblock.symmetry;
import multiblock.Multiblock;
public abstract class Symmetry{
    public final String name;
    public Symmetry(String name){
        this.name = name;
    }
    public abstract void apply(Multiblock multiblock);
}