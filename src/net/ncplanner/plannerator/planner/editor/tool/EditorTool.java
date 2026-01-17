package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.symmetry.Symmetry;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.editor.Editor;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
public abstract class EditorTool{
    public final Editor editor;
    public final int id;
    public EditorTool(Editor editor, int id){
        this.editor = editor;
        this.id = id;
    }
    public abstract void render(Renderer renderer, float x, float y, float width, float height, int themeIndex);
    public void render(int x, int y, int z, float width, float height, float depth, int themeIndex){
        Renderer renderer = new Renderer();
        renderer.pushModel(new Matrix4f().translate(x, y+height, z+depth+.001f-1).scale(1, -1, 1));
        GL11.glDisable(GL11.GL_CULL_FACE);
        render(renderer, 0, 0, width, height, themeIndex);//draw 2D
        GL11.glEnable(GL11.GL_CULL_FACE);
        renderer.popModel();
    }//TODO VR: make this abstract fancy tool rendering
    public abstract void mouseReset(EditorSpace editorSpace, int button);
    public abstract void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button);
    public abstract void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button);
    public abstract void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button);
    public abstract void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos);
    public abstract void mouseMovedElsewhere(Object obj, EditorSpace editorSpace);
    public abstract void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture);
    public abstract void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture);
    public abstract boolean isEditTool();
    public abstract String getTooltip();
    public static interface TraceStep{
        public void step(int x, int z);
    }
    public void raytrace(BlockPos from, BlockPos to, Consumer<BlockPos> step, boolean includeFirst){
        int xDiff = to.x-from.x;
        int yDiff = to.y-from.y;
        int zDiff = to.z-from.z;
        double dist = Math.sqrt(MathUtil.pow(from.x-to.x, 2)+MathUtil.pow(from.y-to.y, 2)+MathUtil.pow(from.z-to.z, 2));
        ArrayList<BlockPos> steps = new ArrayList<>();
        if(!includeFirst)steps.add(from);
        FOR:for(float r = 0; r<1; r+=.25/dist){
            int x = Math.round(from.x+xDiff*r);
            int y = Math.round(from.y+yDiff*r);
            int z = Math.round(from.z+zDiff*r);
            for(BlockPos stp : steps){
                if(x==stp.x&&y==stp.y&&z==stp.z)continue FOR;
            }
            BlockPos pos = new BlockPos(x, y, z);
            steps.add(pos);
            step.accept(pos);
        }
    }
    public void raytrace(BlockPos from, BlockPos to, Consumer<BlockPos> step){
        raytrace(from, to, step, true);
    }
    public void raytrace(BlockPos from, BlockPos to, Consumer<BlockPos> step, boolean includeFirst, Symmetry symmetry, int width, int height, int depth){
        raytrace(from, to, (pos) -> {
            symmetry.apply(pos, width, height, depth, step::accept);
        }, includeFirst);
    }
    public void raytrace(BlockPos from, BlockPos to, Consumer<BlockPos> step, Symmetry symmetry, int width, int height, int depth){
        raytrace(from, to, step, true, symmetry, width, height, depth);
    }
    public void foreach(BlockPos from, BlockPos to, Consumer<BlockPos> step){
        BoundingBox.around(from, to).forEachPosition(step::accept);
    }
    public void foreach(BlockPos from, BlockPos to, Consumer<BlockPos> step, Symmetry symmetry, int width, int height, int depth){
        foreach(from, to, (pos) -> {
            symmetry.apply(pos, width, height, depth, step::accept);
        });
    }
}