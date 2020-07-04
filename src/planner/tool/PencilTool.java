package planner.tool;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import multiblock.Block;
import simplelibrary.opengl.ImageStash;
public class PencilTool extends EditorTool{
    public PencilTool(MenuEdit editor){
        super(editor);
    }
    private int[] leftDragStart;
    private int leftLayerStart = -1;
    private int[] rightDragStart;
    private int rightLayerStart = -1;
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
            leftLayerStart = -1;
        }
        if(button==1){
            rightDragStart = null;
            rightLayerStart = -1;
        }
    }
    @Override
    public void mousePressed(int x, int y, int z, int button){
        Block selected = editor.getSelectedBlock();
        if(button==0||button==1)editor.setblock(x,y,z,button==0?selected:null);
        if(button==0){
            leftDragStart = new int[]{x,z};
            leftLayerStart = y;
        }
        if(button==1){
            rightDragStart = new int[]{x,z};
            rightLayerStart = y;
        }
    }
    @Override
    public void mouseReleased(int x, int y, int z, int button){
        mouseReset(button);
    }
    @Override
    public void mouseDragged(int x, int y, int z, int button){
        if(button==0){
            if(y!=leftLayerStart){
                leftDragStart = new int[]{x,z};
                leftLayerStart = y;
            }
            if(leftDragStart!=null){
                if(leftDragStart[0]==x&&leftDragStart[1]==z)return;
                Block setTo = editor.getSelectedBlock();
                raytrace(leftDragStart[0], leftDragStart[1], x, z, (X,Z) -> {
                    editor.setblock(X, y, Z, setTo);
                });
                leftDragStart = new int[]{x,z};
            }
        }
        if(button==1){
            if(y!=rightLayerStart){
                rightDragStart = new int[]{x,z};
                rightLayerStart = y;
            }
            if(rightDragStart!=null){
                if(rightDragStart[0]==x&&rightDragStart[1]==z)return;
                Block setTo = null;
                raytrace(rightDragStart[0], rightDragStart[1], x, z, (X,Z) -> {
                    editor.setblock(X, y, Z, setTo);
                }, false);
                rightDragStart = new int[]{x,z};
            }
        }
    }
    @Override
    public boolean isEditTool(){
        return true;
    }
}