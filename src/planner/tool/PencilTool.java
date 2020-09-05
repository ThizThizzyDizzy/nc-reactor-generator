package planner.tool;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import org.lwjgl.glfw.GLFW;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.MenuComponent;
public class PencilTool extends EditorTool{
    public PencilTool(MenuEdit editor){
        super(editor);
    }
    private int[] leftDragStart;
    private MenuComponent leftLayerStart = null;
    private int[] rightDragStart;
    private MenuComponent rightLayerStart = null;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        ImageStash.instance.bindTexture(0);
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex2d(x+width*.25, y+height*.75);
        GL11.glVertex2d(x+width*.375, y+height*.75);
        GL11.glVertex2d(x+width*.25, y+height*.625);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x+width*.4, y+height*.725);
        GL11.glVertex2d(x+width*.275, y+height*.6);
        GL11.glVertex2d(x+width*.5, y+height*.375);
        GL11.glVertex2d(x+width*.625, y+height*.5);

        GL11.glVertex2d(x+width*.525, y+height*.35);
        GL11.glVertex2d(x+width*.65, y+height*.475);
        GL11.glVertex2d(x+width*.75, y+height*.375);
        GL11.glVertex2d(x+width*.625, y+height*.25);
        GL11.glEnd();
    }
    @Override
    public void mouseReset(int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            leftDragStart = null;
            leftLayerStart = null;
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            rightDragStart = null;
            rightLayerStart = null;
        }
    }
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock();
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT||button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)editor.setblock(x,y,z,button==GLFW.GLFW_MOUSE_BUTTON_LEFT?selected:null);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            leftDragStart = new int[]{x,y,z};
            leftLayerStart = layer;
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            rightDragStart = new int[]{x,y,z};
            rightLayerStart = layer;
        }
    }
    @Override
    public void mouseReleased(MenuComponent layer, int x, int y, int z, int button){
        mouseReset(button);
    }
    @Override
    public void mouseDragged(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT){
            if(layer!=leftLayerStart){
                leftDragStart = new int[]{x,y,z};
                leftLayerStart = layer;
            }
            if(leftDragStart!=null){
                if(leftDragStart[0]==x&&leftDragStart[1]==y&&leftDragStart[2]==z)return;
                Block setTo = editor.getSelectedBlock();
                raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                    if(X==leftDragStart[0]&&Y==leftDragStart[1]&&Z==leftDragStart[2])return;
                    editor.setblock(X, Y, Z, setTo);
                });
                leftDragStart = new int[]{x,y,z};
            }
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT){
            if(layer!=rightLayerStart){
                rightDragStart = new int[]{x,y,z};
                rightLayerStart = layer;
            }
            if(rightDragStart!=null){
                if(rightDragStart[0]==x&&rightDragStart[1]==y&&rightDragStart[2]==z)return;
                Block setTo = null;
                raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
                    if(X==rightDragStart[0]&&Y==rightDragStart[1]&&Z==rightDragStart[2])return;
                    editor.setblock(X, Y, Z, setTo);
                }, false);
                rightDragStart = new int[]{x,y,z};
            }
        }
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){}
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){}
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){}
    @Override
    public String getTooltip(){
        return "Pencil tool (P)\nUse this tool to draw blocks one at a time\nHold CTRL to only place blocks where they are valid";
    }
}