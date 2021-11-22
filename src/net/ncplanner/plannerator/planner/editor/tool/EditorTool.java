package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.planner.MathUtil;
import net.ncplanner.plannerator.planner.editor.Editor;
import net.ncplanner.plannerator.graphics.image.Image;
import org.joml.Matrix4f;
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
        renderer.model(new Matrix4f().translate(x, y+height, z+depth+.001f).scale(1, -1, 1));
        render(new Renderer(), 0, 0, width, height, themeIndex);//draw 2D
        renderer.resetModelMatrix();
    }//TODO VR: make this abstract fancy tool rendering
    public abstract void mouseReset(EditorSpace editorSpace, int button);
    public abstract void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button);
    public abstract void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button);
    public abstract void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button);
    public abstract void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z);
    public abstract void mouseMovedElsewhere(Object obj, EditorSpace editorSpace);
    public abstract void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture);
    public abstract void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture);
    public abstract boolean isEditTool();
    public abstract String getTooltip();
    public static interface TraceStep{
        public void step(int x, int z);
    }
    public void raytrace(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, TraceStep3 step, boolean includeFirst){
        int xDiff = toX-fromX;
        int yDiff = toY-fromY;
        int zDiff = toZ-fromZ;
        double dist = Math.sqrt(MathUtil.pow(fromX-toX, 2)+MathUtil.pow(fromY-toY, 2)+MathUtil.pow(fromZ-toZ, 2));
        ArrayList<int[]> steps = new ArrayList<>();
        if(!includeFirst)steps.add(new int[]{fromX,fromY,fromZ});
        FOR:for(float r = 0; r<1; r+=.25/dist){
            int x = Math.round(fromX+xDiff*r);
            int y = Math.round(fromY+yDiff*r);
            int z = Math.round(fromZ+zDiff*r);
            for(int[] stp : steps){
                if(x==stp[0]&&y==stp[1]&&z==stp[2])continue FOR;
            }
            steps.add(new int[]{x, y, z});
            step.step(x, y, z);
        }
    }
    public void raytrace(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, TraceStep3 step){
        raytrace(fromX, fromY, fromZ, toX, toY, toZ, step, true);
    }
    public static interface TraceStep3{
        public void step(int x, int y, int z);
    }
    public void foreach(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, TraceStep3 step){
        if(toX<fromX){
            int from = toX;
            toX = fromX;
            fromX = from;
        }
        if(toY<fromY){
            int from = toY;
            toY = fromY;
            fromY = from;
        }
        if(toZ<fromZ){
            int from = toZ;
            toZ = fromZ;
            fromZ = from;
        }
        for(int x = fromX; x<=toX; x++){
            for(int y = fromY; y<=toY; y++){
                for(int z = fromZ; z<=toZ; z++){
                    step.step(x, y, z);
                }
            }
        }
    }
}