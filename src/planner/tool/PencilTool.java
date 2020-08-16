package planner.tool;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
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
        if(button==0){
            leftDragStart = null;
            leftLayerStart = null;
        }
        if(button==1){
            rightDragStart = null;
            rightLayerStart = null;
        }
    }
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock();
        if(button==0||button==1)editor.setblock(x,y,z,button==0?selected:null);
        if(button==0){
            leftDragStart = new int[]{x,y,z};
            leftLayerStart = layer;
        }
        if(button==1){
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
        if(button==0){
            if(layer!=leftLayerStart){
                leftDragStart = new int[]{x,y,z};
                leftLayerStart = layer;
            }
            if(leftDragStart!=null){
                if(leftDragStart[0]==x&&leftDragStart[1]==z)return;
                Block setTo = editor.getSelectedBlock();
                raytrace(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z, (X,Y,Z) -> {
                    editor.setblock(X, Y, Z, setTo);
                });
                leftDragStart = new int[]{x,y,z};
            }
        }
        if(button==1){
            if(layer!=rightLayerStart){
                rightDragStart = new int[]{x,y,z};
                rightLayerStart = layer;
            }
            if(rightDragStart!=null){
                if(rightDragStart[0]==x&&rightDragStart[1]==z)return;
                Block setTo = null;
                raytrace(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z, (X,Y,Z) -> {
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
}