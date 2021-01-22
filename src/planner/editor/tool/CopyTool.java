package planner.editor.tool;
import org.lwjgl.glfw.GLFW;
import planner.Core;
import planner.editor.Editor;
import simplelibrary.opengl.Renderer2D;
public class CopyTool extends EditorTool{
    public CopyTool(Editor editor, int id){
        super(editor, id);
    }
    private int[] dragStart;
    private int[] dragEnd;
    @Override
    public void render(double x, double y, double width, double height){
        Core.applyColor(Core.theme.getTextColor());
        Renderer2D.drawRect(x+width*.35, y+height*.15, x+width*.8, y+height*.75, 0);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        Renderer2D.drawRect(x+width*.4, y+height*.2, x+width*.75, y+height*.7, 0);
        Core.applyColor(Core.theme.getTextColor());
        Renderer2D.drawRect(x+width*.2, y+height*.25, x+width*.65, y+height*.85, 0);
        Core.applyColor(Core.theme.getEditorListBorderColor());
        Renderer2D.drawRect(x+width*.25, y+height*.3, x+width*.6, y+height*.8, 0);
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
    public void drawVRGhosts(double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        //TODO VR: Copy tool ghosts
    }
    @Override
    public void mouseReset(int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = dragEnd = null;
    }
    @Override
    public void mousePressed(Object layer, int x, int y, int z, int button){
        editor.clearSelection(id);
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT)dragStart = new int[]{x,y,z};
    }
    @Override
    public void mouseReleased(Object layer, int x, int y, int z, int button){
        if(button==GLFW.GLFW_MOUSE_BUTTON_LEFT&&dragStart!=null){
            editor.select(id, dragStart[0], dragStart[1], dragStart[2], x, y, z);
            editor.copySelection(id, (dragStart[0]+x)/2, (dragStart[1]+y)/2, (dragStart[2]+z)/2);
            editor.clearSelection(id);
        }
        mouseReset(button);
    }
    @Override
    public void mouseDragged(Object layer, int x, int y, int z, int button){
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
    public void mouseMoved(Object obj, int x, int y, int z){}
    @Override
    public void mouseMovedElsewhere(Object obj){}
}