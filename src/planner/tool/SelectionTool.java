package planner.tool;
import org.lwjgl.opengl.GL11;
import planner.Core;
import planner.menu.MenuEdit;
import simplelibrary.opengl.Renderer2D;
public class SelectionTool extends EditorTool{
    public SelectionTool(MenuEdit editor){
        super(editor);
    }
    private int[] leftDragStart;
    private int[] leftDragEnd;
    private int[] rightDragStart;
    private int[] rightDragEnd;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, 0);
        Renderer2D.drawRect(width/10, height/10, width/3, height/6, 0);
        Renderer2D.drawRect(width/10, height/10, width/6, height/3, 0);
        Renderer2D.drawRect(width-width/10, height/10, width-width/3, height/6, 0);
        Renderer2D.drawRect(width-width/10, height/10, width-width/6, height/3, 0);
        Renderer2D.drawRect(width/10, height-height/10, width/3, height-height/6, 0);
        Renderer2D.drawRect(width/10, height-height/10, width/6, height-height/3, 0);
        Renderer2D.drawRect(width-width/10, height-height/10, width-width/3, height-height/6, 0);
        Renderer2D.drawRect(width-width/10, height-height/10, width-width/6, height-height/3, 0);
        GL11.glPopMatrix();
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int w, int texture){
        if(leftDragEnd!=null&&leftDragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(leftDragStart[1], leftDragEnd[1])&&layer<=Math.max(leftDragStart[1], leftDragEnd[1])){
                int minX = Math.min(leftDragStart[0], leftDragEnd[0]);
                int maxX = Math.max(leftDragStart[0], leftDragEnd[0]);
                int minZ = Math.min(leftDragStart[2], leftDragEnd[2]);
                int maxZ = Math.max(leftDragStart[2], leftDragEnd[2]);
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(x+w*minX, y+w*minZ, x+w*(maxX+1), y+w*(maxZ+1), 0);
                Core.applyColor(Core.theme.getSelectionColor());
                Renderer2D.drawRect(x+w*minX, y+w*minZ, x+w*(maxX+1), y+w*(border+minZ), 0);//top
                Renderer2D.drawRect(x+w*minX, y+w*(maxZ+1-border), x+w*(maxX+1), y+w*(maxZ+1), 0);//bottom
                Renderer2D.drawRect(x+w*minX, y+w*(minZ+border), x+w*(border+minX), y+w*(maxZ+1-border), 0);//left
                Renderer2D.drawRect(x+w*(maxX+1-border), y+w*(minZ+border), x+w*(maxX+1), y+w*(maxZ+1-border), 0);//right
            }
        }else if(Core.isControlPressed()){
        }else if(Core.isAltPressed()){
        }
        if(rightDragEnd!=null&&rightDragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(rightDragStart[1], rightDragEnd[1])&&layer<=Math.max(rightDragStart[1], rightDragEnd[1])){
                int minX = Math.min(rightDragStart[0], rightDragEnd[0]);
                int maxX = Math.max(rightDragStart[0], rightDragEnd[0]);
                int minZ = Math.min(rightDragStart[2], rightDragEnd[2]);
                int maxZ = Math.max(rightDragStart[2], rightDragEnd[2]);
                Core.applyColor(Core.theme.getSelectionColor());
                Renderer2D.drawRect(x+w*minX, y+w*minZ, x+w*(maxX+1), y+w*(border+minZ), 0);//top
                Renderer2D.drawRect(x+w*minX, y+w*(maxZ+1-border), x+w*(maxX+1), y+w*(maxZ+1), 0);//bottom
                Renderer2D.drawRect(x+w*minX, y+w*(minZ+border), x+w*(border+minX), y+w*(maxZ+1-border), 0);//left
                Renderer2D.drawRect(x+w*(maxX+1-border), y+w*(minZ+border), x+w*(maxX+1), y+w*(maxZ+1-border), 0);//right
            }
        }else if(Core.isControlPressed()){
        }else if(Core.isAltPressed()){
        }
        Core.applyWhite();
    }
    @Override
    public void mouseReset(int button){
        if(button==0)leftDragStart = leftDragEnd = null;
        if(button==1)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(int x, int y, int z, int button){
        if(Core.isControlPressed()){
            if(button==0)editor.selectGroup(x,y,z);
            if(button==1)editor.deselectGroup(x,y,z);
            return;
        }
        if(Core.isAltPressed()){
            if(button==0)editor.selectCluster(x,y,z);
            if(button==1)editor.deselectCluster(x,y,z);
            return;
        }
        if(button==0)leftDragStart = new int[]{x,y,z};
        if(button==1)rightDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(int x, int y, int z, int button){
        if(button==0&&leftDragStart!=null)editor.select(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z);
        if(button==1&&rightDragStart!=null)editor.deselect(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z);
        mouseReset(button);
    }
    @Override
    public void mouseDragged(int x, int y, int z, int button){
        if(button==0)leftDragEnd = new int[]{x,y,z};
        if(button==1)rightDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
}