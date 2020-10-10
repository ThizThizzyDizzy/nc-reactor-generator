package multiblock;
public abstract class Action<T extends Multiblock>{
    public abstract void apply(T multiblock, boolean allowUndo);
    public abstract void undo(T multiblock);
}