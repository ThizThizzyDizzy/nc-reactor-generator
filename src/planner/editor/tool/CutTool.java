package planner.editor.tool;
import multiblock.Axis;
import multiblock.EditorSpace;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.editor.Editor;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class CutTool extends EditorTool{
    public CutTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] dragStart;
    private int[] dragEnd;
    @Override
    public void render(double x, double y, double width, double height, int themeIndex){
        Core.applyColor(Core.theme.getEditorToolTextColor(themeIndex));
        ImageStash.instance.bindTexture(0);
        Core.drawCircle(x+width*.3, y+height*.3, width*.075, width*.125, Core.theme.getEditorToolTextColor(themeIndex));
        Core.drawCircle(x+width*.3, y+height*.7, width*.075, width*.125, Core.theme.getEditorToolTextColor(themeIndex));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x+width*.4, y+height*.35);
        GL11.glVertex2d(x+width*.35, y+height*.4);
        GL11.glVertex2d(x+width*.75, y+height*.8);
        GL11.glVertex2d(x+width*.85, y+height*.8);
        
        GL11.glVertex2d(x+width*.4, y+height*.65);
        GL11.glVertex2d(x+width*.35, y+height*.6);
        GL11.glVertex2d(x+width*.75, y+height*.2);
        GL11.glVertex2d(x+width*.85, y+height*.2);
    }
    @Override
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, double x, double y, double width, double height, int blockSize, int texture){
        if(dragEnd!=null&&dragStart!=null){
            float border = 1/8f;
            int minBX = Math.min(dragStart[0], dragEnd[0]);
            int minBY = Math.min(dragStart[1], dragEnd[1]);
            int minBZ = Math.min(dragStart[2], dragEnd[2]);
            int maxBX = Math.max(dragStart[0], dragEnd[0]);
            int maxBY = Math.max(dragStart[1], dragEnd[1]);
            int maxBZ = Math.max(dragStart[2], dragEnd[2]);
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int minSX = Math.max(x1,Math.min(x2,minBX*xAxis.x+minBY*xAxis.y+minBZ*xAxis.z-x1));
            int minSY = Math.max(y1,Math.min(y2,minBX*yAxis.x+minBY*yAxis.y+minBZ*yAxis.z-y1));
            int maxSX = Math.max(x1,Math.min(x2,maxBX*xAxis.x+maxBY*xAxis.y+maxBZ*xAxis.z-x1));
            int maxSY = Math.max(y1,Math.min(y2,maxBX*yAxis.x+maxBY*yAxis.y+maxBZ*yAxis.z-y1));
            int minSZ = minBX*axis.x+minBY*axis.y+minBZ*axis.z;
            int maxSZ = maxBX*axis.x+maxBY*axis.y+maxBZ*axis.z;
            if(layer>=minSZ&&layer<=maxSZ){
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(maxSY+1), 0);
                Core.applyColor(Core.theme.getSelectionColor());
                Renderer2D.drawRect(x+blockSize*minSX, y+blockSize*minSY, x+blockSize*(maxSX+1), y+blockSize*(border+minSY), 0);//top
                Renderer2D.drawRect(x+blockSize*minSX, y+blockSize*(maxSY+1-border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1), 0);//bottom
                Renderer2D.drawRect(x+blockSize*minSX, y+blockSize*(minSY+border), x+blockSize*(border+minSX), y+blockSize*(maxSY+1-border), 0);//left
                Renderer2D.drawRect(x+blockSize*(maxSX+1-border), y+blockSize*(minSY+border), x+blockSize*(maxSX+1), y+blockSize*(maxSY+1-border), 0);//right
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        //TODO VR: cut tool ghosts
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = dragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        editor.clearSelection(id);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&dragStart!=null){
            editor.select(id, dragStart[0], dragStart[1], dragStart[2], x, y, z);
            editor.cutSelection(id, (dragStart[0]+x)/2, (dragStart[1]+y)/2, (dragStart[2]+z)/2);
            editor.clearSelection(id);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Cut tool\nUse this to select an area to cut\nOnce an area is selected, click to paste that selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}