package planner.multiblock;
import java.util.ArrayList;
public abstract class Action<T extends Multiblock>{
    public ActionResult<T> apply(T multiblock){
        doApply(multiblock);
        ArrayList blocks = new ArrayList<>();
        getAffectedBlocks(blocks);
        return new ActionResult<>(multiblock, blocks);
    }
    protected abstract void doApply(T multiblock);
    public ActionResult<T> undo(T multiblock){
        doUndo(multiblock);
        ArrayList blocks = new ArrayList<>();
        getAffectedBlocks(blocks);
        return new ActionResult<>(multiblock, blocks);
    }
    protected abstract void doUndo(T multiblock);
    protected abstract void getAffectedBlocks(ArrayList<Block> blocks);
}