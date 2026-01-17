package net.ncplanner.plannerator.planner.editor.tool;
import java.util.ArrayList;
import java.util.Iterator;
import net.ncplanner.plannerator.graphics.Renderer;
import net.ncplanner.plannerator.graphics.image.Image;
import net.ncplanner.plannerator.multiblock.Axis;
import net.ncplanner.plannerator.multiblock.BlockPos;
import net.ncplanner.plannerator.multiblock.BoundingBox;
import net.ncplanner.plannerator.multiblock.editor.EditorSpace;
import net.ncplanner.plannerator.multiblock.editor.action.PasteAction;
import net.ncplanner.plannerator.planner.Core;
import net.ncplanner.plannerator.planner.editor.ClipboardEntry;
import net.ncplanner.plannerator.planner.editor.Editor;
public class PasteTool extends EditorTool{
    private BlockPos cursorPos;
    public PasteTool(Editor editor, int id){
        super(editor, id);
    }
    @Override
    public void render(Renderer renderer, float x, float y, float width, float height, int themeIndex){
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.fillRect(x+width*.35f, y+height*.15f, x+width*.8f, y+height*.75f);
        renderer.setColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        renderer.fillRect(x+width*.4f, y+height*.2f, x+width*.75f, y+height*.7f);
        renderer.setColor(Core.theme.getEditorToolTextColor(themeIndex));
        renderer.fillRect(x+width*.2f, y+height*.25f, x+width*.65f, y+height*.85f);
        renderer.setColor(Core.theme.getEditorToolBackgroundColor(themeIndex));
        renderer.fillRect(x+width*.25f, y+height*.3f, x+width*.6f, y+height*.8f);
    }
    @Override
    public void mouseReset(EditorSpace editorSpace, int button){}
    @Override
    public void mousePressed(Object obj, EditorSpace editorSpace, BlockPos pos, int button){
        if(button==0){
            ArrayList<ClipboardEntry> clipboard = new ArrayList<>(editor.getClipboard(id));
            for(Iterator<ClipboardEntry> it = clipboard.iterator(); it.hasNext();){
                ClipboardEntry entry = it.next();
                if(!editorSpace.contains(pos.offset(entry.pos))||!editorSpace.isSpaceValid(entry.block, pos.offset(entry.pos)))it.remove();
            }
            editor.action(new PasteAction(clipboard, pos), true);
        }
    }
    @Override
    public void mouseReleased(Object obj, EditorSpace editorSpace, BlockPos pos, int button){}
    @Override
    public void mouseDragged(Object obj, EditorSpace editorSpace, BlockPos pos, int button){}
    @Override
    public boolean isEditTool(){
        return true;
    }
    @Override
    public void drawGhosts(Renderer renderer, EditorSpace editorSpace, int x1, int y1, int x2, int y2, int blocksWide, int blocksHigh, Axis axis, int layer, float x, float y, float width, float height, int blockSize, Image texture){
        if(cursorPos==null)return;
        synchronized(editor.getClipboard(id)){
            for(ClipboardEntry entry : editor.getClipboard(id)){
                BlockPos pos = entry.pos.offset(cursorPos);
                if(!editorSpace.isSpaceValid(entry.block, pos))continue;
                Axis xAxis = axis.get2DXAxis();
                Axis yAxis = axis.get2DYAxis();
                int sx = pos.x*xAxis.x+pos.y*xAxis.y+pos.z*xAxis.z-x1;
                int sy = pos.x*yAxis.x+pos.y*yAxis.y+pos.z*yAxis.z-y1;
                int sz = pos.x*axis.x+pos.y*axis.y+pos.z*axis.z;
                if(sz!=layer)continue;
                if(sx<0||sx>x2)continue;
                if(sy<0||sy>y2)continue;
                if(entry.block!=null)renderer.setWhite(.5f);
                else renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
                renderer.drawImage(entry.block==null?null:entry.block.getTexture(), x+sx*blockSize, y+sy*blockSize, x+(sx+1)*blockSize, y+(sy+1)*blockSize);
            }
        }
        renderer.setWhite();
    }
    @Override
    public void drawVRGhosts(Renderer renderer, EditorSpace editorSpace, float x, float y, float z, float width, float height, float depth, float blockSize, Image texture){
        if(cursorPos==null)return;
        synchronized(editor.getClipboard(id)){
            for(ClipboardEntry entry : editor.getClipboard(id)){
                BlockPos pos = entry.pos.offset(cursorPos);
                if(!editorSpace.isSpaceValid(entry.block, pos))continue;
                BoundingBox bbox = editor.getMultiblock().getBoundingBox();
                if(pos.x<bbox.x1||pos.x>bbox.x2)continue;
                if(pos.y<bbox.y1||pos.y>bbox.y2)continue;
                if(pos.z<bbox.z1||pos.z>bbox.z2)continue;
                if(entry.block!=null)renderer.setWhite(.5f);
                else renderer.setColor(Core.theme.getEditorBackgroundColor(), .5f);
                renderer.drawCube(x+pos.x*blockSize, y+pos.y*blockSize, z+pos.z*blockSize, x+(pos.x+1)*blockSize, y+(pos.y+1)*blockSize, z+(pos.z+1)*blockSize, entry.block==null?null:entry.block.getTexture());
            }
        }
        renderer.setWhite();
    }
    @Override
    public String getTooltip(){
        return "Paste tool\nSelect a region, and press Ctrl-X or Ctrl+C to cut or copy the selection and open this tool.\nThen click any location in your reactor to paste the selection in multiple places.\nPress Escape or select a different tool when done.\nPress Ctrl+V to ready the most recently copied selection";
    }
    @Override
    public void mouseMoved(Object obj, EditorSpace editorSpace, BlockPos pos){
        cursorPos = pos;
    }
    @Override
    public void mouseMovedElsewhere(Object obj, EditorSpace editorSpace){
        cursorPos = null;
    }
}