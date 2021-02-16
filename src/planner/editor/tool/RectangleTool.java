package planner.editor.tool;
import planner.Core;
import multiblock.action.SetblocksAction;
import org.lwjgl.glfw.GLFW;
import planner.editor.Editor;
import planner.vr.VRCore;
import simplelibrary.opengl.Renderer2D;
public class RectangleTool extends EditorTool{
    public RectangleTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] leftDragStart;
    private int[] rightDragStart;
    private int[] leftDragEnd;
    private int[] rightDragEnd;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        int n = 3;
        double border = width/24;
        x+=border;
        y+=border;
        width-=border*2;
        height-=border*2;
        double w = width/n;
        double h = height/n;
        for(int X = 0; X<n; X++){
            for(int Y = 0; Y<n; Y++){
                Renderer2D.drawRect(x+X*w+border, y+Y*h+border, x+(X+1)*w-border, y+(Y+1)*h-border, 0);
            }
        }
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(Y==layer)Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(Z==layer)Renderer2D.drawRect(x+X*blockSize, y+Y*blockSize, x+(X+1)*blockSize, y+(Y+1)*blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(Z==layer)Renderer2D.drawRect(x+X*blockSize, y+Y*blockSize, x+(X+1)*blockSize, y+(Y+1)*blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            if(X==editor.getMultiblock().getX()/2&&Y==0)Renderer2D.drawRect(x+(Z-1)*blockSize, y, x+Z*blockSize, y+blockSize, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(X==editor.getMultiblock().getX()/2&&Y==0)Renderer2D.drawRect(x+(Z-1)*blockSize, y, x+Z*blockSize, y+blockSize, 0);
        });
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        double border = blockSize/64;
        if(leftDragEnd!=null&&leftDragStart!=null)foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], leftDragEnd[0], leftDragEnd[1], leftDragEnd[2], (X,Y,Z) -> {
            VRCore.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, texture);
        });
        if(rightDragEnd!=null&&rightDragStart!=null)foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], rightDragEnd[0], rightDragEnd[1], rightDragEnd[2], (X,Y,Z) -> {
            if(editor.getMultiblock().getBlock(X, Y, Z)==null)return;
            VRCore.drawCube(x+X*blockSize-border, y+Y*blockSize-border, z+Z*blockSize-border, x+(X+1)*blockSize+border, y+(Y+1)*blockSize+border, z+(Z+1)*blockSize+border, 0);
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
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object obj, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null){
            SetblocksAction set = new SetblocksAction(editor.getSelectedBlock(id));
            foreach(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                set.add(X, Y, Z);
            });
            editor.setblocks(id, set);
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&rightDragStart!=null){
            SetblocksAction set = new SetblocksAction(null);
            foreach(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
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
        return "Box tool (B)\nUse this tool to draw Rectangles or Cuboids of the same block\nHold CTRL to only place blocks where they are valid";
    }
    @Override
    public void mouseMoved(Object obj, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj){}
}