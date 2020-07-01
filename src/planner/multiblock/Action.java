package planner.multiblock;
public interface Action<T extends Multiblock>{
    public void apply(T multiblock);
    public void undo(T multiblock);
}