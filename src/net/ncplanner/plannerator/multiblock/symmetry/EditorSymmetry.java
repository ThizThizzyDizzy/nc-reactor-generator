package net.ncplanner.plannerator.multiblock.symmetry;
import net.ncplanner.plannerator.multiblock.BlockPosConsumer;
import net.ncplanner.plannerator.planner.editor.Editor;
public class EditorSymmetry extends Symmetry{
    private final Editor editor;
    public EditorSymmetry(Editor editor){
        this.editor = editor;
    }
    public StandardSymmetry standard = new StandardSymmetry();
    @Override
    public void apply(int x, int y, int z, int w, int h, int d, BlockPosConsumer consumer){
        editor.getMultiblock().applyMultiblockSymmetry(x, y, z, w, h, d, consumer);
        standard.apply(x, y, z, w, h, d, consumer);
    }
}
