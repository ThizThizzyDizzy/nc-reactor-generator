package net.ncplanner.plannerator.multiblock.symmetry;
import java.util.function.Consumer;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.planner.editor.Editor;
public class EditorSymmetry extends Symmetry{
    private final Editor editor;
    public EditorSymmetry(Editor editor){
        this.editor = editor;
    }
    public StandardSymmetry standard = new StandardSymmetry();
    @Override
    public void apply(BlockPos pos, int w, int h, int d, Consumer<BlockPos> consumer){
        editor.getMultiblock().applyMultiblockSymmetry(pos, consumer);
        standard.apply(pos, w, h, d, consumer);
    }
}
