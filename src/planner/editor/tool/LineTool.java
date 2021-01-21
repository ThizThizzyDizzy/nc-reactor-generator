package planner.editor.tool;
import org.lwjgl.opengl.GL11;
import planner.Core;
import multiblock.action.SetblocksAction;
import org.lwjgl.glfw.GLFW;
import planner.editor.Editor;
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
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x+width*.125, y+height*.75);
        GL11.glVertex2d(x+width*.25, y+height*.875);
        GL11.glVertex2d(x+width*.875, y+height*.25);
        GL11.glVertex2d(x+width*.75, y+height*.125);
        GL11.glEnd();
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(Z==layer)Renderer2D.drawRect(x+X*blockSize, y+Y*blockSize, x+(X+1)*blockSize, y+(Y+1)*blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(Z==layer)Renderer2D.drawRect(x+X*blockSize, y+Y*blockSize, x+(X+1)*blockSize, y+(Y+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(X==0&&Y==0)Renderer2D.drawRect(x+(Z-1)*blockSize, y, x+Z*blockSize, y+blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(X==0&&Y==0)Renderer2D.drawRect(x+(Z-1)*blockSize, y, x+Z*blockSize, y+blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void mouseReset(int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = null;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = leftDragEnd = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
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
        mouseReset(button);
    }
    @Override
    public void mouseDragged(Object obj, int x, int y, int z, int button){
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
    public void mouseMoved(Object obj, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj){}
}