package planner.editor.tool;
import planner.Core;
import planner.editor.ClipboardEntry;
import planner.editor.Editor;
import simplelibrary.opengl.Renderer2D;
import simplelibrary.opengl.gui.components.MenuComponent;
public class PasteTool extends EditorTool{
    private int mouseX;
    private int mouseY;
    private int mouseZ;
    public PasteTool(Editor editor){
        super(editor);
    }
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
    public void mouseReset(int button){}
    @Override
    public void mousePressed(MenuComponent layer, int x, int y, int z, int button){
        if(button==0)editor.pasteSelection(x, y, z);
    }
    @Override
    public void mouseReleased(MenuComponent layer, int x, int y, int z, int button){}
    @Override
    public void mouseDragged(MenuComponent layer, int x, int y, int z, int button){}
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        if(mouseX==-1||mouseY==-1||mouseZ==-1)return;
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(editor.getClipboard()){
            for(ClipboardEntry entry : editor.getClipboard()){
                if(entry.y+mouseY==layer){
                    int X = entry.x+mouseX;
                    int Z = entry.z+mouseZ;
                    if(X<0||X>=editor.getMultiblock().getX())continue;
                    if(Z<0||Z>=editor.getMultiblock().getZ())continue;
                    Renderer2D.drawRect(x+X*blockSize, y+Z*blockSize, x+(X+1)*blockSize, y+(Z+1)*blockSize, entry.block==null?0:Core.getTexture(entry.block.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawCoilGhosts(int layer, double x, double y, double width, double height, int blockSize, int texture){
        if(mouseX==-1||mouseY==-1||mouseZ==-1)return;
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(editor.getClipboard()){
            for(ClipboardEntry entry : editor.getClipboard()){
                if(entry.z+mouseZ==layer){
                    int X = entry.x+mouseX;
                    int Y = entry.y+mouseY;
                    if(X<0||X>=editor.getMultiblock().getX())continue;
                    if(Y<0||Y>=editor.getMultiblock().getY())continue;
                    Renderer2D.drawRect(x+X*blockSize, y+Y*blockSize, x+(X+1)*blockSize, y+(Y+1)*blockSize, entry.block==null?0:Core.getTexture(entry.block.getTexture()));
                }
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawBladeGhosts(double x, double y, double width, double height, int blockSize, int texture){
        if(mouseX==-1||mouseY==-1||mouseZ==-1)return;
        Core.applyColor(Core.theme.getEditorListBorderColor(), .5f);
        synchronized(editor.getClipboard()){
            for(ClipboardEntry entry : editor.getClipboard()){
                int Z = entry.z+mouseZ;
                if(Z<0||Z>=editor.getMultiblock().getZ())continue;
                Renderer2D.drawRect(x+Z*blockSize, y, x+(Z+1)*blockSize, y+blockSize, entry.block==null?0:Core.getTexture(entry.block.getTexture()));
            }
        }
        Core.applyWhite();
    }
    @Override
    public String getTooltip(){
        return "Paste tool\nSelect a region, and press Ctrl-X or Ctrl+C to cut or copy the selection and open this tool.\nThen click any location in your reactor to paste the selection in multiple places.\nPress Escape or select a different tool when done.\nPress Ctrl+V to ready the most recently copied selection";
    }
    @Override
    public void mouseMoved(MenuComponent layer, int x, int y, int z){
        mouseX = x;
        mouseY = y;
        mouseZ = z;
    }
    @Override
    public void mouseMovedElsewhere(MenuComponent layer){
        mouseX = mouseY = mouseZ = -1;
    }
}