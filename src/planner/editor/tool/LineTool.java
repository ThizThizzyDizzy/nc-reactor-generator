package planner.editor.tool;
import multiblock.Axis;
import multiblock.EditorSpace;
import multiblock.action.SetblocksAction;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.editor.Editor;
import planner.vr.VRCore;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer2D;
public class LineTool extends EditorTool{
    public LineTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] leftDragStart;
    private int[] rightDragStart;
    private int[] leftDragEnd;
    private int[] rightDragEnd;
    @Override
    public void render(double x, double y, double width, double height, int themeIndex){
        Core.applyColor(Core.theme.getEditorToolTextColor(themeIndex));
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x+width*.125, y+height*.75);
        GL11.glVertex2d(x+width*.25, y+height*.875);
        GL11.glVertex2d(x+width*.875, y+height*.25);
        GL11.glVertex2d(x+width*.75, y+height*.125);
        GL11.glEnd();
    }
    @Override
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyWhite(.5f);
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (bx,by,bz) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), bx, by, bz))return;
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
            int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
            int sz = bx*axis.x+by*axis.y+bz*axis.z;
            if(sz!=layer)return;
            if(sx<x1||sx>x2)return;
            if(sy<y1||sy>y2)return;
            Renderer2D.drawRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, texture);
        });
        Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (bx,by,bz) -> {
            Axis xAxis = axis.get2DXAxis();
            Axis yAxis = axis.get2DYAxis();
            int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
            int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
            int sz = bx*axis.x+by*axis.y+bz*axis.z;
            if(sz!=layer)return;
            if(sx<x1||sx>x2)return;
            if(sy<y1||sy>y2)return;
            Renderer2D.drawRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
        double border = blockSize/64;
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(!editorSpace.isSpaceValid(editor.getSelectedBlock(id), X, Y, Z))return;
            VRCore.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, texture);
        });
        Core.applyWhite(.5f);
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(editor.getMultiblock().getBlock(X, Y, Z)==null)return;
            VRCore.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = null;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = leftDragEnd = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                if(editorSpace.isSpaceValid(set.block, X, Y, Z))set.add(X, Y, Z);
            });
            editor.setblocks(id, set);
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&rightDragStart!=null){
            SetblocksAction set = new SetblocksAction(null);
            raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
            });
            editor.setblocks(id, set);
        }
        mouseReset(editorSpace, button);
    }
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragEnd = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public String getTooltip(){
        return "Line tool (L)\nUse this tool to draw blocks in a line through the multiblock\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){}
}