package net.ncplanner.plannerator.multiblock.editor.symmetry;
import net.ncplanner.plannerator.multiblock.Multiblock;
public abstract class Symmetry<T extends Multiblock>{
    public final String name;
    public Symmetry(String name){
        this.name = name;
    }
    public abstract void apply(T multiblock);
    public boolean defaultEnabled(){
        return false;
    }
    public boolean check(T m){
        T copy = (T)m.copy();
        apply(copy);
        return m.areBlocksEqual(copy);
    }
}