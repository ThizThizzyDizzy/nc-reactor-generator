package planner.editor.tool;
import java.util.ArrayList;
import java.util.Iterator;
import multiblock.Axis;
import multiblock.BoundingBox;
import multiblock.EditorSpace;
import multiblock.action.PasteAction;
import planner.Core;
import planner.editor.ClipboardEntry;
import planner.editor.Editor;
import planner.vr.VRCore;
import simplelibrary.opengl.Renderer2D;
public class PasteTool extends EditorTool{
    private int mouseX;
    private int mouseY;
    private int mouseZ;
    public PasteTool(Editor editor, int id){
        super(editor, id);
    }
    @Override
    public void render(double x, double y, double width, double height, int themeIndex){
        Core.applyColor(Core.theme.getEditorToolTextColor(themeIndex));
        Renderer2D.drawRect(x+width*.35, y+height*.15, x+width*.8, y+height*.75, 0);
        Core.applyColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        Renderer2D.drawRect(x+width*.4, y+height*.2, x+width*.75, y+height*.7, 0);
        Core.applyColor(Core.theme.getEditorToolTextColor(themeIndex));
        Renderer2D.drawRect(x+width*.2, y+height*.25, x+width*.65, y+height*.85, 0);
        Core.applyColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        Renderer2D.drawRect(x+width*.25, y+height*.3, x+width*.6, y+height*.8, 0);
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){}
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){
        if(button==0){
            ArrayList<ClipboardEntry> clipboard = new ArrayList<>(editor.getClipboard(id));
            for(Iterator<ClipboardEntry> it = clipboard.iterator(); it.hasNext();){
                ClipboardEntry entry = it.next();
                if(!editorSpace.contains(x+entry.x, y+entry.y, z+entry.z)||!editorSpace.isSpaceValid(entry.block, x+entry.x, y+entry.y, z+entry.z))it.remove();
            }
            editor.action(new PasteAction(clipboard, x, y, z), true);
        }
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){}
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, int x, int y, int z, int button){}
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, double x, double y, double width, double height, int blockSize, int texture){
        if(mouseX==-1||mouseY==-1||mouseZ==-1)return;
        synchronized(editor.getClipboard(id)){
            for(ClipboardEntry entry : editor.getClipboard(id)){
                int bx = entry.x+mouseX;
                int by = entry.y+mouseY;
                int bz = entry.z+mouseZ;
                if(!editorSpace.isSpaceValid(entry.block, bx, by, bz))continue;
                Axis xAxis = axis.get2DXAxis();
                Axis yAxis = axis.get2DYAxis();
                int sx = bx*xAxis.x+by*xAxis.y+bz*xAxis.z-x1;
                int sy = bx*yAxis.x+by*yAxis.y+bz*yAxis.z-y1;
                int sz = bx*axis.x+by*axis.y+bz*axis.z;
                if(sz!=layer)continue;
                if(sx<x1||sx>x2)continue;
                if(sy<y1||sy>y2)continue;
                if(entry.block!=null)Core.applyWhite(.5f);
                else Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
                Renderer2D.drawRect(x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize, entry.block==null?0:Core.getTexture(entry.block.getTexture()));
            }
        }
        Core.applyWhite();
    }
    @Override
    public void drawVRGhosts(EditorSpace editorSpace, double x, double y, double z, double width, double height, double depth, double blockSize, int texture){
        if(mouseX==-1||mouseY==-1||mouseZ==-1)return;
        synchronized(editor.getClipboard(id)){
            for(ClipboardEntry entry : editor.getClipboard(id)){
                int bx = entry.x+mouseX;
                int by = entry.y+mouseY;
                int bz = entry.z+mouseZ;
                if(!editorSpace.isSpaceValid(entry.block, bx, by, bz))continue;
                BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                if(bx<bbox.x1||bx>bbox.x2)continue;
                if(by<bbox.y1||by>bbox.y2)continue;
                if(bz<bbox.z1||bz>bbox.z2)continue;
                if(entry.block!=null)Core.applyWhite(.5f);
                else Core.applyColor(Core.theme.getEditorBackgroundColor(), .5f);
                VRCore.drawCube(x+bx*blockSize, y+by*blockSize, z+bz*blockSize, x+(bx+1)*blockSize, y+(by+1)*blockSize, z+(bz+1)*blockSize, entry.block==null?0:Core.getTexture(entry.block.getTexture()));
            }
        }
        Core.applyWhite();
    }
    @Override
    public String getTooltip(){
        return "Paste tool\nSelect a region, and press Ctrl-X or Ctrl+C to cut or copy the selection and open this tool.\nThen click any location in your reactor to paste the selection in multiple places.\nPress Escape or select a different tool when done.\nPress Ctrl+V to ready the most recently copied selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, int x, int y, int z){
        mouseX = x;
        mouseY = y;
        mouseZ = z;
    }
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){
        mouseX = mouseY = mouseZ = -1;
    }
}