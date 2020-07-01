package planner.multiblock;
public interface Action{
    public void apply(Multiblock multiblock);
    public void undo(Multiblock multiblock);
}