package planner.tool;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.menu.MenuEdit;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class CopyTool extends EditorTool{
    public CopyTool(MenuEdit editor){
        super(editor);
    }
    private int[] dragStart;
    private int[] dragEnd;
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
        if(dragEnd!=null&&dragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(dragStart[1], dragEnd[1])&&layer<=Math.max(dragStart[1], dragEnd[1])){
                int minX = Math.min(dragStart[0], dragEnd[0]);
                int maxX = Math.max(dragStart[0], dragEnd[0]);
                int minZ = Math.min(dragStart[2], dragEnd[2]);
                int maxZ = Math.max(dragStart[2], dragEnd[2]);
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(x+w*minX, y+w*minZ, x+w*(maxX+1), y+w*(maxZ+1), 0);
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
        if(dragEnd!=null&&dragStart!=null){
            float border = 1/8f;
            if(layer>=Math.min(dragStart[2], dragEnd[2])&&layer<=Math.max(dragStart[2], dragEnd[2])){
                int minX = Math.min(dragStart[0], dragEnd[0]);
                int maxX = Math.max(dragStart[0], dragEnd[0]);
                int minY = Math.min(dragStart[1], dragEnd[1]);
                int maxY = Math.max(dragStart[1], dragEnd[1]);
                Core.applyColor(Core.theme.getSelectionColor(), .5f);
                Renderer2D.drawRect(x+w*minX, y+w*minY, x+w*(maxX+1), y+w*(maxY+1), 0);
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
        if(dragEnd!=null&&dragStart!=null){
            float border = 1/8f;
            int minZ = Math.min(dragStart[2], dragEnd[2])-1;
            int maxZ = Math.max(dragStart[2], dragEnd[2])-1;
            Core.applyColor(Core.theme.getSelectionColor(), .5f);
            Renderer2D.drawRect(x+w*minZ, y, x+w*(maxZ+1), y+w, 0);
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
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = dragEnd = null;
    }
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        editor.clearSelection();
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&dragStart!=null){
            editor.select(dragStart[0], dragStart[1], dragStart[2], x, y, z);
            editor.copySelectionToClipboard((dragStart[0]+x)/2, (dragStart[1]+y)/2, (dragStart[2]+z)/2);
            editor.clearSelection();
        }
        mouseReset(button);
    }
    @Override
    public void mouseDragged(MenuComponent layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragEnd = new int[]{x,y,z};
    }
    @Override
    public boolean isEditTool(){
        return false;
    }
    @Override
    public String getTooltip(){
        return "Copy tool\nUse this to select an area to copy\nOnce an area is selected, click to paste that selection";
    }
    @Override
    public void mouseMoved(MenuComponent layer, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(MenuComponent layer){}
}