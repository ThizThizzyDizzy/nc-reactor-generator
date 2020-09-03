package planner.tool;
import java.util.ArrayList;
import planner.menu.MenuEdit;
import simplelibrary.opengl.gui.components.MenuComponent;
public abstract class EditorTool{
    public final MenuEdit editor;
    public EditorTool(MenuEdit editor){
        this.editor = editor;
    }
    public abstract void render(double x, double y, double width, double height);
    public abstract void mouseReset(int button);
    public abstract void mousePressed(MenuComponent layer, int x, int y, int z, int button);
    public abstract void mouseReleased(MenuComponent layer, int x, int y, int z, int button);
    public abstract void mouseDragged(MenuComponent layer, int x, int y, int z, int button);
    public abstract void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture);
    public abstract void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture);
    public abstract void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture);
    public abstract boolean isEditTool();
    public abstract String getTooltip();
    public static interface TraceStep{
        public void step(int x, int z);
    }
    public void raytrace(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, TraceStep3 step, boolean includeFirst){
        int xDiff = toX-fromX;
        int yDiff = toY-fromY;
        int zDiff = toZ-fromZ;
        double dist = Math.sqrt(Math.pow(fromX-toX, 2)+Math.pow(fromY-toY, 2)+Math.pow(fromZ-toZ, 2));
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