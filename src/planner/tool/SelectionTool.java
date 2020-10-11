package planner.tool;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.MenuEdit;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
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
        Renderer2D.drawRect(x+width/10, y+height/10, x+width/3, y+height/6, 0);
        Renderer2D.drawRect(x+width/10, y+height/10, x+width/6, y+height/3, 0);
        Renderer2D.drawRect(x+width-width/10, y+height/10, x+width-width/3, y+height/6, 0);
        Renderer2D.drawRect(x+width-width/10, y+height/10, x+width-width/6, y+height/3, 0);
        Renderer2D.drawRect(x+width/10, y+height-height/10, x+width/3, y+height-height/6, 0);
        Renderer2D.drawRect(x+width/10, y+height-height/10, x+width/6, y+height-height/3, 0);
        Renderer2D.drawRect(x+width-width/10, y+height-height/10, x+width-width/3, y+height-height/6, 0);
        Renderer2D.drawRect(x+width-width/10, y+height-height/10, x+width-width/6, y+height-height/3, 0);
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
        }
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int w, int texture){
        if(leftDragEnd!=null&&leftDragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(leftDragStart[2], leftDragEnd[2])&&layer<=Math.max(leftDragStart[2], leftDragEnd[2])){
                int minX = Math.min(leftDragStart[0], leftDragEnd[0]);
                int maxX = Math.max(leftDragStart[0], leftDragEnd[0]);
                int minY = Math.min(leftDragStart[1], leftDragEnd[1]);
                int maxY = Math.max(leftDragStart[1], leftDragEnd[1]);
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(x+w*minX, y+w*minY, x+w*(maxX+1), y+w*(maxY+1), 0);
                Core.applyColor(Core.theme.getSelectionColor());
                Renderer2D.drawRect(x+w*minX, y+w*minY, x+w*(maxX+1), y+w*(border+minY), 0);//top
                Renderer2D.drawRect(x+w*minX, y+w*(maxY+1-border), x+w*(maxX+1), y+w*(maxY+1), 0);//bottom
                Renderer2D.drawRect(x+w*minX, y+w*(minY+border), x+w*(border+minX), y+w*(maxY+1-border), 0);//left
                Renderer2D.drawRect(x+w*(maxX+1-border), y+w*(minY+border), x+w*(maxX+1), y+w*(maxY+1-border), 0);//right
            }
        }
        if(rightDragEnd!=null&&rightDragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(rightDragStart[2], rightDragEnd[2])&&layer<=Math.max(rightDragStart[2], rightDragEnd[2])){
                int minX = Math.min(rightDragStart[0], rightDragEnd[0]);
                int maxX = Math.max(rightDragStart[0], rightDragEnd[0]);
                int minY = Math.min(rightDragStart[1], rightDragEnd[1]);
                int maxY = Math.max(rightDragStart[1], rightDragEnd[1]);
                Core.applyColor(Core.theme.getSelectionColor());
                Renderer2D.drawRect(x+w*minX, y+w*minY, x+w*(maxX+1), y+w*(border+minY), 0);//top
                Renderer2D.drawRect(x+w*minX, y+w*(maxY+1-border), x+w*(maxX+1), y+w*(maxY+1), 0);//bottom
                Renderer2D.drawRect(x+w*minX, y+w*(minY+border), x+w*(border+minX), y+w*(maxY+1-border), 0);//left
                Renderer2D.drawRect(x+w*(maxX+1-border), y+w*(minY+border), x+w*(maxX+1), y+w*(maxY+1-border), 0);//right
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int w, int texture){
        if(leftDragEnd!=null&&leftDragStart!=null){
            float border = 1/8f;
            int minZ = Math.min(leftDragStart[2], leftDragEnd[2])-1;
            int maxZ = Math.max(leftDragStart[2], leftDragEnd[2])-1;
            Core.applyColor(Core.theme.getSelectionColor(), .5f);
            Renderer2D.drawRect(x+w*minZ, y, x+w*(maxZ+1), y+w, 0);
            Core.applyColor(Core.theme.getSelectionColor());
            Renderer2D.drawRect(x+w*minZ, y, x+w*(border+minZ), y+w, 0);//top
            Renderer2D.drawRect(x+w*(maxZ+1-border), y, x+w*(maxZ+1), y+w, 0);//bottom
            Renderer2D.drawRect(x+w*(minZ+border), y, x+w*(maxZ+1-border), y+w*border, 0);//left
            Renderer2D.drawRect(x+w*(minZ+border), y+w*(1-border), x+w*(maxZ+1-border), y+w, 0);//right
        }
        if(rightDragEnd!=null&&rightDragStart!=null){
            float border = 1/8f;
            int minZ = Math.min(rightDragStart[2], rightDragEnd[2]);
            int maxZ = Math.max(rightDragStart[2], rightDragEnd[2]);
            Core.applyColor(Core.theme.getSelectionColor());
            Renderer2D.drawRect(x+w*minZ, y, x+w*(border+minZ), y+w, 0);//top
            Renderer2D.drawRect(x+w*(maxZ+1-border), y, x+w*(maxZ+1), y+w, 0);//bottom
            Renderer2D.drawRect(x+w*(minZ+border), y, x+w*(maxZ+1-border), y+w*border, 0);//left
            Renderer2D.drawRect(x+w*(minZ+border), y+w*(1-border), x+w*(maxZ+1-border), y+w, 0);//right
        }
        Core.applyWhite();
    }
    @Override
    public void mouseReset(int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = leftDragEnd = null;
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = rightDragEnd = null;
    }
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        if(!Core.isControlPressed()){
            editor.clearSelection();
        }
        if(Core.isShiftPressed()){
            if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)editor.selectGroup(x,y,z);
            if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)editor.deselectGroup(x,y,z);
            return;
        }
        if(Core.isAltPressed()){
            if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)editor.selectCluster(x,y,z);
            if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)editor.deselectCluster(x,y,z);
            return;
        }
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragStart = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&leftDragStart!=null)editor.select(leftDragStart[0], leftDragStart[1], leftDragStart[2], x, y, z);
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT&&rightDragStart!=null)editor.deselect(rightDragStart[0], rightDragStart[1], rightDragStart[2], x, y, z);
        mouseReset(button);
    }
    @Override
    public void mouseDragged(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)leftDragEnd = new int[]{x,y,z};
        if(button==GLFW.GLFW_MOUSE_BUTTON_RIGHT)rightDragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Select tool (S)\nUse this to select areas of the reactor\nCtrl-click to select multiple selections\nShift-click to select Groups of blocks that require each other\nAlt-click to select clusters\nEdits can only be made inside of selections (unless there are no selections)\nPress Delete to delete the selected area\nPress Escape to clear selection";
    }
    @Override
    public void mouseMoved(MenuComponent layer, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(MenuComponent layer){}
}